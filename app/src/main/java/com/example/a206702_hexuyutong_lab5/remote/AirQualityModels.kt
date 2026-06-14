package com.example.travelmap

import com.google.gson.annotations.SerializedName

/**
 * Data-from-the-Internet pillar — response models for the Open-Meteo Air Quality API.
 *
 * Endpoint (free, NO API key required):
 *   https://air-quality-api.open-meteo.com/v1/air-quality?latitude=..&longitude=..&current=..
 *
 * Gson maps the JSON keys onto these properties. Where a JSON key is not a valid
 * Kotlin name (e.g. "pm2_5", "european_aqi"), [SerializedName] bridges the two.
 * Every field is nullable because the API omits pollutants that have no reading.
 */
data class AirQualityResponse(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    @SerializedName("current_units") val currentUnits: AirQualityUnits? = null,
    val current: CurrentAirQuality? = null
)

/** The latest reading for the requested location. */
data class CurrentAirQuality(
    val time: String? = null,
    @SerializedName("european_aqi") val europeanAqi: Int? = null,
    @SerializedName("us_aqi") val usAqi: Int? = null,
    val pm10: Double? = null,
    @SerializedName("pm2_5") val pm25: Double? = null,
    @SerializedName("carbon_monoxide") val carbonMonoxide: Double? = null,
    @SerializedName("nitrogen_dioxide") val nitrogenDioxide: Double? = null,
    @SerializedName("sulphur_dioxide") val sulphurDioxide: Double? = null,
    val ozone: Double? = null
)

/** Measurement units (e.g. "μg/m³") so the UI can label each value correctly. */
data class AirQualityUnits(
    val pm10: String? = null,
    @SerializedName("pm2_5") val pm25: String? = null,
    @SerializedName("carbon_monoxide") val carbonMonoxide: String? = null,
    @SerializedName("nitrogen_dioxide") val nitrogenDioxide: String? = null,
    @SerializedName("sulphur_dioxide") val sulphurDioxide: String? = null,
    val ozone: String? = null
)
