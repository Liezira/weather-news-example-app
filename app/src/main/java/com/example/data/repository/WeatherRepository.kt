package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.NetworkClient
import com.example.data.model.weather.*
import retrofit2.HttpException
import java.io.IOException
import java.util.Locale

class WeatherRepository {
    private val weatherApi = NetworkClient.weatherApi

    suspend fun getWeather(city: String): Result<WeatherResponse> {
        val apiKey = BuildConfig.OPENWEATHER_API_KEY
        val isDemo = apiKey.isBlank() || apiKey == "YOUR_OPENWEATHER_API_KEY"

        if (isDemo) {
            return Result.success(getSimulatedWeather(city, "Demo Mode (API Key Belum Terpasang)"))
        }

        return try {
            val response = weatherApi.getWeather(city = city, apiKey = apiKey)
            Result.success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                // Return simulated weather but inform about invalid key
                return Result.success(getSimulatedWeather(city, "Simulasi (API Key 401 Unauthorized)"))
            }
            val message = when (e.code()) {
                404 -> "Kota '$city' tidak ditemukan"
                429 -> "Batas request harian OpenWeatherMap tercapai (429)"
                else -> "Kesalahan server cuaca: ${e.message()}"
            }
            Result.failure(Exception(message))
        } catch (e: IOException) {
            // Also fall back to simulation if offline, indicating cache/simulation
            Result.success(getSimulatedWeather(city, "Simulasi Offline (Tidak Ada Koneksi)"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.localizedMessage ?: e.message}"))
        }
    }

    private data class SimulatedWeatherStats(
        val temp: Double,
        val description: String,
        val icon: String,
        val humidity: Int,
        val windSpeed: Double,
        val feelsLike: Double
    )

    private fun getSimulatedWeather(city: String, sourceTag: String): WeatherResponse {
        val cleanCity = city.trim().lowercase(Locale.getDefault())
        
        val stats = when {
            cleanCity.contains("jakarta") || cleanCity.contains("surabaya") || cleanCity.contains("medan") -> {
                SimulatedWeatherStats(32.5, "Cerah Berawan", "02d", 78, 3.2, 36.0)
            }
            cleanCity.contains("bandung") || cleanCity.contains("bogor") || cleanCity.contains("malang") -> {
                SimulatedWeatherStats(22.0, "Hujan Ringan", "10d", 88, 2.1, 23.0)
            }
            cleanCity.contains("tokyo") || cleanCity.contains("seoul") || cleanCity.contains("kyoto") -> {
                SimulatedWeatherStats(18.2, "Angin Berhembus Dingin", "01d", 55, 4.5, 17.5)
            }
            cleanCity.contains("london") || cleanCity.contains("paris") || cleanCity.contains("amsterdam") -> {
                SimulatedWeatherStats(14.0, "Rintik Gerimis & Berawan", "09d", 82, 5.1, 13.0)
            }
            cleanCity.contains("asmat") || cleanCity.contains("papua") -> {
                SimulatedWeatherStats(29.0, "Hujan Petir", "11d", 95, 4.0, 32.0)
            }
            cleanCity.contains("mekkah") || cleanCity.contains("cairo") || cleanCity.contains("dubai") -> {
                SimulatedWeatherStats(41.0, "Sangat Panas dan Terang", "01d", 20, 6.2, 44.0)
            }
            else -> {
                // Generate semi-random deterministic stats based on city name hash
                val hash = cleanCity.hashCode().coerceAtLeast(0)
                val isRainy = hash % 3 == 0
                val isCloudy = hash % 3 == 1
                val baseTemp = 24.0 + (hash % 11) // 24 to 35
                
                if (isRainy) {
                    SimulatedWeatherStats(baseTemp - 3.0, "Hujan Sedang", "09d", 85 + (hash % 10), 2.5 + (hash % 3), baseTemp - 1.0)
                } else if (isCloudy) {
                    SimulatedWeatherStats(baseTemp - 1.0, "Berawan Tebal", "04d", 70 + (hash % 15), 1.8 + (hash % 3), baseTemp)
                } else {
                    SimulatedWeatherStats(baseTemp + 1.5, "Cerah Terang", "01d", 45 + (hash % 20), 3.0 + (hash % 4), baseTemp + 3.0)
                }
            }
        }

        // Return rich response with sourceTag embedded in country
        return WeatherResponse(
            name = city.trim().split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } },
            coord = Coord(lon = 106.8451, lat = -6.2088),
            weather = listOf(
                WeatherDescription(
                    id = 800,
                    main = "Simulated",
                    description = stats.description,
                    icon = stats.icon
                )
            ),
            main = MainWeatherData(
                temp = stats.temp,
                feelsLike = stats.feelsLike,
                tempMin = (stats.temp - 3),
                tempMax = (stats.temp + 3),
                pressure = 1012,
                humidity = stats.humidity
            ),
            wind = WindWeatherData(
                speed = stats.windSpeed,
                deg = 200,
                gust = stats.windSpeed * 1.3
            ),
            clouds = CloudsWeatherData(all = 40),
            sys = SysWeatherData(
                country = sourceTag, // embed tag here to show neatly in UI!
                sunrise = 1718000000L,
                sunset = 1718045000L
            ),
            cod = 200
        )
    }
}

