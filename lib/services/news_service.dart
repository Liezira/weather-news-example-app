import 'package:dio/dio.dart';
import '../constants/api_constants.dart';
import '../models/news_model.dart';

class NewsService {
  final Dio _dio = Dio();

  Future<List<NewsArticle>> getNews() async {
    final response = await _dio.get(
      ApiConstants.newsBaseUrl,
      // Aktifkan baris di bawah jika API News kamu butuh key di header:
      // options: Options(headers: {'Authorization': 'Bearer ${ApiConstants.newsApiKey}'}),
    );

    // DEBUG: handle dua format respons - array langsung atau object {news: [...]}
    final dynamic data = response.data;
    final List<dynamic> list =
        data is List ? data : (data['news'] as List? ?? []);

    return list
        .map((e) => NewsArticle.fromJson(e as Map<String, dynamic>))
        .toList();
  }
}
