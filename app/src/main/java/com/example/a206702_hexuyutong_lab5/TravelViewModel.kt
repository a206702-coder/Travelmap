package com.example.travelmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel — exposes data from the [TravelRepository] to the UI as [StateFlow]
 * and forwards user actions (add/delete) back to the repository.
 *
 * The repository Flow is converted to a StateFlow with [stateIn] so Compose can
 * collect it as state and always has an immediate initial value.
 */
class TravelViewModel(private val repository: TravelRepository) : ViewModel() {

    val travelList: StateFlow<List<TravelPlace>> = repository.allPlaces
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    /** Stream for a single place, used by the detail screen. */
    fun getPlaceById(id: Int): StateFlow<TravelPlace?> = repository.getPlace(id)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    fun addTravelPlace(name: String, address: String, date: String, desc: String) {
        viewModelScope.launch {
            repository.insert(
                TravelPlace(name = name, address = address, date = date, desc = desc)
            )
        }
    }

    fun deleteTravelPlace(place: TravelPlace) {
        viewModelScope.launch { repository.delete(place) }
    }

    companion object {
        /**
         * Factory that pulls the repository out of [TravelApplication] so the
         * ViewModel can be created with `viewModel(factory = TravelViewModel.Factory)`.
         */
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TravelApplication
                TravelViewModel(application.repository)
            }
        }
    }
}
