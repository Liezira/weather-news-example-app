package com.example.data.model.weather

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherResponse(
    @Json(name = "coord") val coord: Coord? = null,
    @Json(name = "weather") val weather: List<WeatherDescription>? = null,
    @Json(name = "main") val main: MainWeatherData? = null,
    @Json(name = "wind") val wind: WindWeatherData? = null,
    @Json(name = "clouds") val clouds: CloudsWeatherData? = null,
    @Json(name = "sys") val sys: SysWeatherData? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "cod") val cod: Int? = null
)

@JsonClass(generateAdapter = true)
data class Coord(
    @Json(name = "lon") val lon: Double? = null,
    @Json(name = "lat") val lat: Double? = null
)

@JsonClass(generateAdapter = true)
data class WeatherDescription(
    @Json(name = "id") val id: Int? = null,
    @Json(name = "main") val main: String? = null,
    @Json(name = "description") val description: String? = null,
    @Json(name = "icon") val icon: String? = null
)

@JsonClass(generateAdapter = true)
data class MainWeatherData(
    @Json(name = "temp") val temp: Double? = null,
    @Json(name = "feels_like") val feelsLike: Double? = null,
    @Json(name = "temp_min") val tempMin: Double? = null,
    @Json(name = "temp_max") val tempMax: Double? = null,
    @Json(name = "pressure") val pressure: Int? = null,
    @Json(name = "humidity") val humidity: Int? = null
)

@JsonClass(generateAdapter = true)
data class WindWeatherData(
    @Json(name = "speed") val speed: Double? = null,
    @Json(name = "deg") val deg: Int? = null,
    @Json(name = "gust") val gust: Double? = null
)

@JsonClass(generateAdapter = true)
data class CloudsWeatherData(
    @Json(name = "all") val all: Int? = null
)

@JsonClass(generateAdapter = true)
data class SysWeatherData(
    @Json(name = "country") val country: String? = null,
    @Json(name = "sunrise") val sunrise: Long? = null,
    @Json(name = "sunset") val sunset: Long? = null
)
