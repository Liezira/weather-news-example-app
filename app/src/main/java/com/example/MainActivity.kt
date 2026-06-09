package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.news.NewsScreen
import com.example.ui.news.NewsViewModel
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.weather.WeatherScreen
import com.example.ui.weather.WeatherViewModel

enum class MainTab {
    WEATHER,
    NEWS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContainer()
            }
        }
    }
}

@Composable
fun MainAppContainer() {
    val weatherViewModel: WeatherViewModel = viewModel()
    val newsViewModel: NewsViewModel = viewModel()
    var currentTab by remember { mutableStateOf(MainTab.WEATHER) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = when (currentTab) {
                    MainTab.WEATHER -> Color(0xFF1B263B)
                    MainTab.NEWS -> Color(0xFF3C096C)
                },
                contentColor = Color.White,
                tonalElevation = NavigationBarDefaults.Elevation,
                modifier = Modifier
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == MainTab.WEATHER,
                    onClick = { currentTab = MainTab.WEATHER },
                    label = { Text("Cuaca", color = Color.White) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.WEATHER) {
                                Icons.Filled.Cloud
                            } else {
                                Icons.Outlined.Cloud
                            },
                            contentDescription = "Menu Cuaca"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF0D1B2A),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE0E1DD),
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_weather")
                )

                NavigationBarItem(
                    selected = currentTab == MainTab.NEWS,
                    onClick = { currentTab = MainTab.NEWS },
                    label = { Text("Berita", color = Color.White) },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == MainTab.NEWS) {
                                Icons.Filled.Newspaper
                            } else {
                                Icons.Outlined.Newspaper
                            },
                            contentDescription = "Menu Berita"
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF240046),
                        selectedTextColor = Color.White,
                        indicatorColor = Color(0xFFE0E1DD),
                        unselectedIconColor = Color.White.copy(alpha = 0.6f),
                        unselectedTextColor = Color.White.copy(alpha = 0.6f)
                    ),
                    modifier = Modifier.testTag("tab_news")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "tab_fade_transition"
            ) { tab ->
                when (tab) {
                    MainTab.WEATHER -> {
                        WeatherScreen(viewModel = weatherViewModel)
                    }
                    MainTab.NEWS -> {
                        NewsScreen(viewModel = newsViewModel)
                    }
                }
            }
        }
    }
}
