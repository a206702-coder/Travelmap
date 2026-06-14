package com.example.travelmap

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Builds the single, shared Retrofit instance and exposes the [AirQualityApi].
 *
 * - `baseUrl` is the Open-Meteo Air Quality host (paths in the API interface are
 *   appended to it).
 * - `GsonConverterFactory` deserializes the JSON body into our data classes.
 *
 * Kept as an `object` (singleton) and built lazily so the HTTP client is created
 * only once and reused for every request.
 */
object RetrofitClient {

    private const val BASE_URL = "https://air-quality-api.open-meteo.com/"

    val airQualityApi: AirQualityApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AirQualityApi::class.java)
    }
}
