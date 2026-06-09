import 'package:flutter_bloc/flutter_bloc.dart';
import '../services/weather_service.dart';
import 'weather_state.dart';

class WeatherCubit extends Cubit<WeatherState> {
  final WeatherService _service;

  WeatherCubit(this._service) : super(WeatherInitial());

  Future<void> fetchWeather(String city) async {
    if (city.trim().isEmpty) return;

    try {
      emit(WeatherLoading());
      final weather = await _service.getWeather(city.trim());
      emit(WeatherLoaded(weather));
    } catch (e) {
      // DEBUG: handles network error, city not found, invalid API key, etc.
      emit(WeatherError('Gagal mengambil data: ${e.toString()}'));
    }
  }
}
