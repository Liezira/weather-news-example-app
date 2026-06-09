package com.example.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.news.Article
import com.example.data.repository.NewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface NewsState {
    object Initial : NewsState
    object Loading : NewsState
    data class Loaded(val articles: List<Article>) : NewsState
    data class Error(val message: String) : NewsState
}

class NewsViewModel(
    private val repository: NewsRepository = NewsRepository()
) : ViewModel() {

    private val _state = MutableStateFlow<NewsState>(NewsState.Initial)
    val state: StateFlow<NewsState> = _state.asStateFlow()

    private var currentCategory: String = "general"

    fun fetchNews(category: String = "general") {
        currentCategory = category
        viewModelScope.launch {
            _state.value = NewsState.Loading
            repository.getTopHeadlines(category = category).fold(
                onSuccess = { response ->
                    val articles = response.articles?.filter { 
                        !it.title.isNullOrBlank() && it.title != "[Removed]" 
                    } ?: emptyList()

                    if (articles.isNotEmpty()) {
                        _state.value = NewsState.Loaded(articles)
                    } else {
                        _state.value = NewsState.Error("Tidak ada berita yang tersedia saat ini.")
                    }
                },
                onFailure = { throwable ->
                    _state.value = NewsState.Error(throwable.message ?: "Gagal memuat berita")
                }
            )
        }
    }

    fun refresh() {
        fetchNews(currentCategory)
    }
}
