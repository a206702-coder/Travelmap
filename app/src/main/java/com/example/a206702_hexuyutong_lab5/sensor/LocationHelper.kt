package com.example.travelmap

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.tasks.await

/**
 * Sensor pillar (GPS / Location).
 *
 * Wraps Google's [com.google.android.gms.location.FusedLocationProviderClient],
 * which fuses GPS, Wi-Fi and cell signals into a single best-estimate location.
 *
 * [getCurrentLocation] requests a fresh, high-accuracy fix and suspends until the
 * device returns one (via `await()` on the Play Services Task). The caller MUST
 * make sure the ACCESS_FINE/COARSE_LOCATION runtime permission is granted first —
 * the screen does this before invoking the ViewModel.
 */
class LocationHelper(context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context.applicationContext)

    @SuppressLint("MissingPermission") // permission is checked in the UI before this is called
    suspend fun getCurrentLocation(): Location? =
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null // CancellationToken — not needed here
        ).await()
}
