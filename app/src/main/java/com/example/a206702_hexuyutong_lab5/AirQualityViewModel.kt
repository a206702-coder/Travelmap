package com.example.travelmap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** The screen state for the Air Quality feature (Sensor + Internet pillars). */
sealed interface AirQualityUiState {
    data object Idle : AirQualityUiState
    data object Loading : AirQualityUiState
    data class Success(
        val latitude: Double,
        val longitude: Double,
        val data: CurrentAirQuality,
        val units: AirQualityUnits?
    ) : AirQualityUiState
    data class Error(val message: String) : AirQualityUiState
}

/**
 * ViewModel that ties the GPS Sensor pillar to the Data-from-the-Internet pillar:
 *
 *   1. Reads the device's current location from [LocationHelper] (GPS sensor).
 *   2. Sends that latitude/longitude to [AirQualityRepository] (Open-Meteo API).
 *   3. Exposes the result as [uiState] for the Compose screen to render.
 *
 * All work runs inside [viewModelScope] so it survives configuration changes and
 * is cancelled automatically when the ViewModel is cleared.
 */
class AirQualityViewModel(
    private val locationHelper: LocationHelper,
    private val repository: AirQualityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AirQualityUiState>(AirQualityUiState.Idle)
    val uiState: StateFlow<AirQualityUiState> = _uiState.asStateFlow()

    /** Call only AFTER the location permission has been granted. */
    fun fetchForCurrentLocation() {
        viewModelScope.launch {
            _uiState.value = AirQualityUiState.Loading

            // Step 1 — GPS sensor
            val location = try {
                locationHelper.getCurrentLocation()
            } catch (e: Exception) {
                _uiState.value = AirQualityUiState.Error("Could not read GPS: ${e.message}")
                return@launch
            }
            if (location == null) {
                _uiState.value = AirQualityUiState.Error(
                    "GPS returned no fix. Enable location and try again (works best outdoors)."
                )
                return@launch
            }

            // Step 2 — live REST API call
            repository.getAirQuality(location.latitude, location.longitude)
                .onSuccess { response ->
                    val current = response.current
                    if (current == null) {
                        _uiState.value =
                            AirQualityUiState.Error("No air-quality data available for this location.")
                    } else {
                        _uiState.value = AirQualityUiState.Success(
                            latitude = response.latitude,
                            longitude = response.longitude,
                            data = current,
                            units = response.currentUnits
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.value = AirQualityUiState.Error("Network error: ${e.message}")
                }
        }
    }

    /** Surface a clear message when the user declines the location permission. */
    fun onPermissionDenied() {
        _uiState.value = AirQualityUiState.Error(
            "Location permission denied. Allow location access to check the air quality near you."
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as TravelApplication
                AirQualityViewModel(app.locationHelper, app.airQualityRepository)
            }
        }
    }
}
