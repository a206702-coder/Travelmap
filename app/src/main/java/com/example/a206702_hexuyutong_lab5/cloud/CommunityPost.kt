package com.example.travelmap

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Cloud Integration pillar — one document in the Firestore `community_posts`
 * collection (the public "Community Green Board").
 *
 * Firestore deserializes documents by reflection, so this class needs:
 *  - a no-argument constructor → achieved by giving every property a default value,
 *  - mutable-or-defaulted fields that match the document keys.
 *
 * [id] is the Firestore document id; it is filled in when reading and marked
 * `@get:Exclude` so it is NOT written back as a field inside the document.
 * [createdAt] uses `@ServerTimestamp`, so Firestore stamps the server time on write.
 */
data class CommunityPost(
    @get:Exclude val id: String = "",
    val title: String = "",
    val message: String = "",
    val author: String = "",
    val location: String = "",
    val sdg: String = "SDG 11",
    @ServerTimestamp val createdAt: Date? = null
)
