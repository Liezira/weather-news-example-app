import 'package:dio/dio.dart';
import '../constants/api_constants.dart';
import '../models/weather_model.dart';

class WeatherService {
  final Dio _dio = Dio();

  Future<WeatherModel> getWeather(String city) async {
    // DEBUG: Dio sudah decode JSON otomatis, tidak perlu jsonDecode manual
    final response = await _dio.get(
      ApiConstants.weatherBaseUrl,
      queryParameters: {
        'q': city,
        'appid': ApiConstants.weatherApiKey,
        'units': 'metric', // Celsius
        'lang': 'id',      // Deskripsi dalam Bahasa Indonesia
      },
    );
    return WeatherModel.fromJson(response.data as Map<String, dynamic>);
  }
}
