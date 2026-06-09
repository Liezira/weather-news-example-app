package com.example.ui.weather

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DeviceThermostat
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.data.model.weather.WeatherResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var cityInput by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    // Automatically load Jakarta on first launch so there is instant, gorgeous visual feedback
    LaunchedEffect(key1 = true) {
        if (state is WeatherState.Initial) {
            viewModel.showWeather("Jakarta")
        }
    }

    val searchAction = {
        if (cityInput.isNotBlank()) {
            viewModel.showWeather(cityInput)
            focusManager.clearFocus()
            keyboardController?.hide()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1B2A),
                        Color(0xFF1B263B),
                        Color(0xFF415A77)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title Header with modern display typography
            Text(
                text = "Cuaca Terkini",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Search Bar
            OutlinedTextField(
                value = cityInput,
                onValueChange = { cityInput = it },
                label = { Text("Masukkan Nama Kota", color = Color(0xFFE0E1DD)) },
                placeholder = { Text("Contoh: Jakarta, Bandung, Surabaya", color = Color(0xFFE0E1DD).copy(alpha = 0.6f)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationCity,
                        contentDescription = "Icon Kota",
                        tint = Color(0xFFE0E1DD)
                    )
                },
                trailingIcon = {
                    if (cityInput.isNotEmpty()) {
                        IconButton(onClick = { cityInput = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear text",
                                tint = Color(0xFFE0E1DD)
                            )
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF8D99AE),
                    unfocusedBorderColor = Color(0xFF415A77),
                    focusedLabelColor = Color.White,
                    unfocusedLabelColor = Color(0xFFE0E1DD),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1B263B).copy(alpha = 0.5f),
                    unfocusedContainerColor = Color(0xFF1B263B).copy(alpha = 0.5f)
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { searchAction() }
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("weather_city_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Button
            Button(
                onClick = { searchAction() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE0E1DD),
                    contentColor = Color(0xFF0D1B2A)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("weather_search_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cari Cuaca", fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Content Area state switcher
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is WeatherState.Initial -> {
                        WeatherInitialState()
                    }
                    is WeatherState.Loading -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                    is WeatherState.Loaded -> {
                        WeatherReportView(weather = currentState.weather)
                    }
                    is WeatherState.Error -> {
                        WeatherErrorState(message = currentState.message) {
                            searchAction()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherInitialState() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Cloud,
            contentDescription = "Cloud Icon",
            modifier = Modifier.size(80.dp),
            tint = Color(0xFFE0E1DD).copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ketahui Cuaca di Kota Anda",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Masukkan nama kota di kolom pencarian untuk melihat prakiraan cuaca saat ini secara real-time.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFE0E1DD).copy(alpha = 0.7f)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WeatherErrorState(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error Icon",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFE63946)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Gagal Memuat Data",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFFE0E1DD).copy(alpha = 0.8f)
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE63946),
                contentColor = Color.White
            ),
            modifier = Modifier.testTag("weather_error_retry")
        ) {
            Text("Coba Lagi")
        }
    }
}

@Composable
fun WeatherReportView(weather: WeatherResponse) {
    val temp = weather.main?.temp?.let { "${it.toInt()}°C" } ?: "-"
    val description = weather.weather?.firstOrNull()?.description ?: "-"
    val iconCode = weather.weather?.firstOrNull()?.icon ?: "01d"
    val humidity = weather.main?.humidity?.let { "$it%" } ?: "-"
    val windSpeed = weather.wind?.speed?.let { "$it m/s" } ?: "-"
    val feelsLike = weather.main?.feelsLike?.let { "${it.toInt()}°C" } ?: "-"
    val countryTag = weather.sys?.country ?: ""
    val isSimulated = countryTag.contains("Demo") || countryTag.contains("Simulasi")

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1B263B).copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isSimulated) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E1DD).copy(alpha = 0.15f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Cloud,
                                contentDescription = "Simulated Data Indicator",
                                tint = Color(0xFFE0E1DD),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = countryTag,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFFE0E1DD),
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                // Weather icon via Coil
                SubcomposeAsyncImage(
                    model = "https://openweathermap.org/img/wn/$iconCode@4x.png",
                    contentDescription = description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(120.dp),
                    loading = {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(36.dp))
                        }
                    },
                    error = {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = description,
                            modifier = Modifier.size(80.dp),
                            tint = Color.White
                        )
                    }
                )

                Text(
                    text = weather.name ?: "Unknown",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = temp,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Light,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description.uppercase(),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE0E1DD),
                        letterSpacing = 1.sp
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (isSimulated) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0E1DD).copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = "Tips API Key",
                        tint = Color(0xFFE0E1DD).copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tips: Untuk melihat data riil real-time secara langsung, konfigurasikan kunci 'OPENWEATHER_API_KEY' Anda pada tab Secrets di AI Studio.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFE0E1DD).copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid-like details for humidity, wind, feelsLike
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DetailItem(
                value = humidity,
                label = "Kelembapan",
                icon = Icons.Default.WaterDrop,
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                value = windSpeed,
                label = "Angin",
                icon = Icons.Default.Air,
                modifier = Modifier.weight(1f)
            )
            DetailItem(
                value = feelsLike,
                label = "Terasa",
                icon = Icons.Default.DeviceThermostat,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DetailItem(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1B263B).copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFE0E1DD).copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    color = Color(0xFFE0E1DD).copy(alpha = 0.5f)
                )
            )
        }
    }
}
