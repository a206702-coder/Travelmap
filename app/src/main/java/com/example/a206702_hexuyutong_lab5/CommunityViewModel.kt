package com.example.travelmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Status of a "publish to the cloud" action. */
sealed interface AddPostStatus {
    data object Idle : AddPostStatus
    data object Saving : AddPostStatus
    data object Success : AddPostStatus
    data class Error(val message: String) : AddPostStatus
}

/**
 * ViewModel for the Cloud Integration pillar (Firebase Firestore).
 *
 * - [posts] is a live [StateFlow] backed by a Firestore snapshot listener, so the
 *   community board updates in real time across all devices.
 * - [addPost] / [sharePlace] push new documents to the cloud.
 *
 * [isCloudAvailable] lets the UI explain when Firebase has not been configured yet
 * (no google-services.json), instead of silently showing an empty screen.
 */
class CommunityViewModel(private val cloudRepository: CloudRepository) : ViewModel() {

    val isCloudAvailable: Boolean = cloudRepository.isAvailable

    val posts: StateFlow<List<CommunityPost>> = cloudRepository.observePosts()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _addStatus = MutableStateFlow<AddPostStatus>(AddPostStatus.Idle)
    val addStatus: StateFlow<AddPostStatus> = _addStatus.asStateFlow()

    /** Publish a brand-new tip/spot written on the "Add Post" screen. */
    fun addPost(title: String, message: String, author: String, location: String) {
        publish(
            CommunityPost(
                title = title,
                message = message,
                author = author.ifBlank { "Anonymous" },
                location = location
            )
        )
    }

    /** Share one of the user's local (Room) travel records to the cloud board. */
    fun sharePlace(place: TravelPlace, author: String = "A206702") {
        publish(
            CommunityPost(
                title = place.name,
                message = place.desc,
                author = author,
                location = place.address
            )
        )
    }

    private fun publish(post: CommunityPost) {
        viewModelScope.launch {
            _addStatus.value = AddPostStatus.Saving
            _addStatus.value = cloudRepository.addPost(post).fold(
                onSuccess = { AddPostStatus.Success },
                onFailure = { AddPostStatus.Error(it.message ?: "Unknown error") }
            )
        }
    }

    fun resetStatus() {
        _addStatus.value = AddPostStatus.Idle
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TravelApplication
                CommunityViewModel(app.cloudRepository)
            }
        }
    }
}
