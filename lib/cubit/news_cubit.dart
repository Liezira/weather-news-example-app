import 'package:flutter_bloc/flutter_bloc.dart';
import '../services/news_service.dart';
import 'news_state.dart';

class NewsCubit extends Cubit<NewsState> {
  final NewsService _service;

  NewsCubit(this._service) : super(NewsInitial()) {
    fetchNews(); // DEBUG: auto-fetch berita saat cubit pertama kali dibuat
  }

  Future<void> fetchNews() async {
    try {
      emit(NewsLoading());
      final articles = await _service.getNews();
      emit(NewsLoaded(articles));
    } catch (e) {
      // DEBUG: handles network error, invalid response, timeout, etc.
      emit(NewsError('Gagal mengambil berita: ${e.toString()}'));
    }
  }
}
