package com.example.travelmap

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

/**
 * Air Quality screen — demonstrates TWO pillars at once:
 *   • Sensor: reads the device GPS location.
 *   • Data from the Internet: fetches live air quality for that location.
 *
 * Handles the runtime location permission with the Activity Result API: if the
 * permission is already granted we fetch immediately, otherwise we ask for it and
 * fetch once the user accepts.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirQualityScreen(viewModel: AirQualityViewModel, navController: NavController) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) viewModel.fetchForCurrentLocation() else viewModel.onPermissionDenied()
    }

    fun checkPermissionAndFetch() {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            viewModel.fetchForCurrentLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Air Quality Near Me") }) },
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Check the air quality at your current location to plan greener, " +
                    "healthier travel (SDG 11).",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { checkPermissionAndFetch() },
                enabled = uiState !is AirQualityUiState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.MyLocation, null)
                Spacer(Modifier.width(8.dp))
                Text("Use GPS & Check Air Quality")
            }

            Spacer(Modifier.height(24.dp))

            when (val state = uiState) {
                is AirQualityUiState.Idle -> {
                    Text(
                        "Tap the button above to read your GPS location and load live data.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is AirQualityUiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Reading GPS and contacting Open-Meteo…")
                }

                is AirQualityUiState.Error -> {
                    Card(
                        Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            state.message,
                            Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                is AirQualityUiState.Success -> AirQualityResult(state)
            }
        }
    }
}

@Composable
private fun AirQualityResult(state: AirQualityUiState.Success) {
    val data = state.data
    val units = state.units
    val (label, color) = aqiCategory(data.europeanAqi)

    // Location header
    Card(Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.LocationOn, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text(
                "Lat ${"%.4f".format(state.latitude)}, " +
                    "Lng ${"%.4f".format(state.longitude)}",
                fontWeight = FontWeight.Medium
            )
        }
    }

    Spacer(Modifier.height(16.dp))

    // Big AQI badge
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.15f))
    ) {
        Column(
            Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.Air, null, tint = color, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(8.dp))
            Text("European AQI", style = MaterialTheme.typography.titleMedium)
            Text(
                data.europeanAqi?.toString() ?: "—",
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Surface(shape = CircleShape, color = color) {
                Text(
                    label,
                    Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(Modifier.height(16.dp))

    // Pollutant breakdown
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Pollutants", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            PollutantRow("PM2.5", data.pm25, units?.pm25)
            PollutantRow("PM10", data.pm10, units?.pm10)
            PollutantRow("Ozone (O₃)", data.ozone, units?.ozone)
            PollutantRow("NO₂", data.nitrogenDioxide, units?.nitrogenDioxide)
            PollutantRow("SO₂", data.sulphurDioxide, units?.sulphurDioxide)
            PollutantRow("CO", data.carbonMonoxide, units?.carbonMonoxide)
            data.usAqi?.let {
                Spacer(Modifier.height(8.dp))
                Text("US AQI: $it", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun PollutantRow(name: String, value: Double?, unit: String?) {
    Row(
        Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name)
        Text(if (value == null) "—" else "%.1f %s".format(value, unit ?: ""))
    }
}

/** Maps a European AQI value to a human label + colour band. */
private fun aqiCategory(eaqi: Int?): Pair<String, Color> = when {
    eaqi == null -> "Unknown" to Color.Gray
    eaqi <= 20 -> "Good" to Color(0xFF4CAF50)
    eaqi <= 40 -> "Fair" to Color(0xFF8BC34A)
    eaqi <= 60 -> "Moderate" to Color(0xFFFFC107)
    eaqi <= 80 -> "Poor" to Color(0xFFFF9800)
    eaqi <= 100 -> "Very Poor" to Color(0xFFF44336)
    else -> "Extremely Poor" to Color(0xFF9C27B0)
}
