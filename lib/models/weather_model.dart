class WeatherModel {
  final String city;
  final double temp;
  final double feelsLike;
  final int humidity;
  final String condition;
  final String description;
  final String icon;

  const WeatherModel({
    required this.city,
    required this.temp,
    required this.feelsLike,
    required this.humidity,
    required this.condition,
    required this.description,
    required this.icon,
  });

  factory WeatherModel.fromJson(Map<String, dynamic> json) {
    final main = json['main'] as Map<String, dynamic>;
    final weather = (json['weather'] as List).first as Map<String, dynamic>;

    return WeatherModel(
      city: json['name'] as String? ?? '',
      temp: (main['temp'] as num).toDouble(),
      feelsLike: (main['feels_like'] as num).toDouble(),
      humidity: main['humidity'] as int,
      condition: weather['main'] as String? ?? '',
      description: weather['description'] as String? ?? '',
      icon: weather['icon'] as String? ?? '',
    );
  }

  // URL icon cuaca dari OpenWeatherMap
  String get iconUrl => 'https://openweathermap.org/img/wn/$icon@2x.png';
}
