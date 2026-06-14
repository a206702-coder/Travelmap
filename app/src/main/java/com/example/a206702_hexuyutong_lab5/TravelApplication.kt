package com.example.travelmap

import android.app.Application

/**
 * Custom [Application] that owns the long-lived, app-wide singletons used by the
 * four data/hardware pillars.
 *
 * Everything is created lazily and lives for the whole process, which is exactly
 * what these shared objects need. Registered via `android:name` in the manifest,
 * and pulled into each ViewModel through its `Factory`.
 */
class TravelApplication : Application() {
    // Local Persistence (Room)
    val database: TravelDatabase by lazy { TravelDatabase.getDatabase(this) }
    val repository: TravelRepository by lazy { TravelRepository(database.travelDao()) }

    // Data from the Internet (Retrofit → Open-Meteo Air Quality)
    val airQualityRepository: AirQualityRepository by lazy { AirQualityRepository() }

    // Sensor (GPS / FusedLocationProviderClient)
    val locationHelper: LocationHelper by lazy { LocationHelper(this) }

    // Cloud Integration (Firebase Firestore)
    val cloudRepository: CloudRepository by lazy { CloudRepository(this) }
}
