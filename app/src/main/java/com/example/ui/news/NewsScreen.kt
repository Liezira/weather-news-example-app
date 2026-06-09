package com.example.ui.news

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.data.model.news.Article

data class CategoryItem(val key: String, val displayName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    viewModel: NewsViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val categories = listOf(
        CategoryItem("general", "Umum"),
        CategoryItem("technology", "Teknologi"),
        CategoryItem("business", "Bisnis"),
        CategoryItem("science", "Sains"),
        CategoryItem("entertainment", "Hiburan"),
        CategoryItem("sports", "Olahraga"),
        CategoryItem("health", "Kesehatan")
    )

    var selectedCategory by remember { mutableStateOf("general") }

    // Fetch news on first launch
    LaunchedEffect(key1 = true) {
        viewModel.fetchNews(selectedCategory)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF240046),
                        Color(0xFF3C096C),
                        Color(0xFF5A189A)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.refresh() },
                    containerColor = Color(0xFFE0E1DD),
                    contentColor = Color(0xFF3C096C),
                    modifier = Modifier.testTag("news_refresh_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh Berita"
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Page Title
                Text(
                    text = "Berita Dunia",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Category Selector Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(categories) { category ->
                        val isSelected = category.key == selectedCategory
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedCategory = category.key
                                viewModel.fetchNews(category.key)
                            },
                            label = {
                                Text(
                                    text = category.displayName,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.White.copy(alpha = 0.1f),
                                labelColor = Color.White.copy(alpha = 0.7f),
                                selectedContainerColor = Color(0xFFE0E1DD),
                                selectedLabelColor = Color(0xFF240046)
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = Color.White.copy(alpha = 0.2f),
                                selectedBorderColor = Color.Transparent
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Responsive View based on state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    when (state) {
                        is NewsState.Initial -> {
                            CircularProgressIndicator(color = Color.White)
                        }
                        is NewsState.Loading -> {
                            CircularProgressIndicator(color = Color.White)
                        }
                        is NewsState.Loaded -> {
                            val articles = (state as NewsState.Loaded).articles
                            ArticleList(articles = articles, context = context)
                        }
                        is NewsState.Error -> {
                            val message = (state as NewsState.Error).message
                            NewsErrorView(message = message) {
                                viewModel.fetchNews(selectedCategory)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ArticleList(articles: List<Article>, context: Context) {
    val listState = rememberLazyListState()

    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(articles) { article ->
            ArticleCard(article = article, context = context)
        }
    }
}

@Composable
fun ArticleCard(article: Article, context: Context) {
    val formattedDate = article.publishedAt?.take(10) ?: "-"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable {
                if (!article.url.isNullOrBlank()) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(article.url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast
                            .makeText(context, "Tidak dapat membuka link", Toast.LENGTH_SHORT)
                            .show()
                    }
                } else {
                    Toast
                        .makeText(context, "Link berita tidak tersedia", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .testTag("news_article_card"),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // News Cover Image via Coil
            if (!article.urlToImage.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = article.urlToImage,
                    contentDescription = article.title ?: "Gambar berita",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color.White)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .background(Color.White.copy(alpha = 0.05f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.BrokenImage,
                                contentDescription = "Error image",
                                tint = Color.White.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .background(Color.White.copy(alpha = 0.05f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "No image",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Article Content Wrapper
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Source Name
                Text(
                    text = article.source?.name ?: "Sumber Belum Diketahui",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE0E1DD),
                        letterSpacing = 0.5.sp
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Title
                Text(
                    text = article.title ?: "Tanpa Judul",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Description
                Text(
                    text = article.description ?: "Tidak ada rincian cuplikan berita pada artikel ini.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.7f),
                        lineHeight = 20.sp
                    ),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Date Footer
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "Published at",
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun NewsErrorView(message: String, onRetry: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = "Error Icon",
            modifier = Modifier.size(64.dp),
            tint = Color(0xFFFF4D6D)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Gagal Memuat Berita",
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
                color = Color.White.copy(alpha = 0.8f)
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4D6D),
                contentColor = Color.White
            ),
            modifier = Modifier.testTag("news_error_retry")
        ) {
            Text("Muat Ulang")
        }
    }
}
