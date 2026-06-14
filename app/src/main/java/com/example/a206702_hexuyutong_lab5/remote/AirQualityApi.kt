package com.example.travelmap

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit interface — the heart of the "Data from the Internet" pillar.
 *
 * Retrofit turns this annotated interface into a working HTTP client at runtime.
 * `@GET` is the endpoint path (appended to the base URL), and each `@Query`
 * becomes a `?key=value` parameter in the URL. The function is `suspend`, so the
 * network call runs on a background thread and returns the parsed [AirQualityResponse].
 *
 * Example call this builds:
 *   GET https://air-quality-api.open-meteo.com/v1/air-quality
 *       ?latitude=3.139&longitude=101.6869
 *       &current=european_aqi,us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone
 */
interface AirQualityApi {

    @GET("v1/air-quality")
    suspend fun getAirQuality(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = DEFAULT_POLLUTANTS
    ): AirQualityResponse

    companion object {
        /** Pollutants we ask Open-Meteo for, as a comma-separated list. */
        const val DEFAULT_POLLUTANTS =
            "european_aqi,us_aqi,pm10,pm2_5,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone"
    }
}
