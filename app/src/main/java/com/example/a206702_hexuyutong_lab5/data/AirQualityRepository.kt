package com.example.travelmap

/**
 * Repository for live air-quality data.
 *
 * Mirrors [TravelRepository] (the Room one): the ViewModel only ever talks to a
 * repository, never to Retrofit directly. This keeps the network details in one
 * place and makes the data source easy to swap or mock.
 */
class AirQualityRepository(
    private val api: AirQualityApi = RetrofitClient.airQualityApi
) {
    /**
     * Fetches the current air quality for a coordinate.
     *
     * Wrapped in a [Result] so the caller can show a friendly error instead of
     * crashing when the device is offline or the API is unreachable.
     */
    suspend fun getAirQuality(latitude: Double, longitude: Double): Result<AirQualityResponse> =
        try {
            Result.success(api.getAirQuality(latitude, longitude))
        } catch (e: Exception) {
            Result.failure(e)
        }
}
