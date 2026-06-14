package com.example.travelmap

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Cloud Integration pillar — reads/writes the shared "Community Green Board"
 * stored in Firebase Cloud Firestore.
 *
 * IMPORTANT (graceful degradation): Firebase only auto-initializes when a real
 * `google-services.json` is present at build time. Before that file is added the
 * default [FirebaseApp] does not exist, so every method here checks [isAvailable]
 * first and degrades quietly (empty list / "not configured" error) instead of
 * crashing. Once the JSON is in place the same code starts syncing with no edits.
 */
class CloudRepository(private val appContext: Context) {

    /** True only when a real google-services.json has initialized Firebase. */
    val isAvailable: Boolean
        get() = FirebaseApp.getApps(appContext).isNotEmpty()

    private val firestore: FirebaseFirestore?
        get() = if (isAvailable) FirebaseFirestore.getInstance() else null

    /**
     * Live stream of community posts, newest first.
     *
     * `addSnapshotListener` pushes a fresh list every time the cloud data changes,
     * and `callbackFlow` adapts that listener into a Kotlin [Flow]. When Firebase
     * is not configured we simply emit an empty list.
     */
    fun observePosts(): Flow<List<CommunityPost>> {
        val db = firestore ?: return flowOf(emptyList())
        return callbackFlow {
            val registration = db.collection(COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val posts = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(CommunityPost::class.java)?.copy(id = doc.id)
                    } ?: emptyList()
                    trySend(posts)
                }
            // Detach the listener when the collector stops to avoid leaks.
            awaitClose { registration.remove() }
        }
    }

    /** Pushes a new post to the cloud. Returns a [Result] so the UI can react. */
    suspend fun addPost(post: CommunityPost): Result<Unit> {
        val db = firestore
            ?: return Result.failure(IllegalStateException("Firebase is not configured yet."))
        return try {
            db.collection(COLLECTION).add(post).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private const val COLLECTION = "community_posts"
    }
}
