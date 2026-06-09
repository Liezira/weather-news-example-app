package com.example.ui.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.weather.WeatherResponse
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface WeatherState {
    object Initial : WeatherState
    object Loading : WeatherState
    data class Loaded(val weather: WeatherResponse) : WeatherState
    data class Error(val message: String) : WeatherState
}

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<WeatherState>(WeatherState.Initial)
    val state: StateFlow<WeatherState> = _state.asStateFlow()

    fun showWeather(city: String) {
        val trimmedCity = city.trim()
        if (trimmedCity.isEmpty()) {
            _state.value = WeatherState.Error("Nama kota tidak boleh kosong")
            return
        }

        viewModelScope.launch {
            _state.value = WeatherState.Loading
            repository.getWeather(trimmedCity).fold(
                onSuccess = { response ->
                    _state.value = WeatherState.Loaded(response)
                },
                onFailure = { throwable ->
                    _state.value = WeatherState.Error(throwable.message ?: "Gagal memuat cuaca")
                }
            )
        }
    }

    fun reset() {
        _state.value = WeatherState.Initial
    }
}
