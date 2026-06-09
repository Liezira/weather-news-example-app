import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';

import 'cubit/weather_cubit.dart';
import 'cubit/news_cubit.dart';
import 'pages/weather_page.dart';
import 'pages/news_page.dart';
import 'services/weather_service.dart';
import 'services/news_service.dart';

void main() {
  runApp(
    // DEBUG: MultiBlocProvider menyediakan WeatherCubit & NewsCubit ke seluruh widget tree
    MultiBlocProvider(
      providers: [
        BlocProvider(create: (_) => WeatherCubit(WeatherService())),
        BlocProvider(create: (_) => NewsCubit(NewsService())),
      ],
      child: const MyApp(),
    ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Weather & News',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const HomePage(),
    );
  }
}

class HomePage extends StatefulWidget {
  const HomePage({super.key});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  int _currentIndex = 0;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      // DEBUG: IndexedStack mempertahankan state setiap halaman saat berpindah tab
      body: IndexedStack(
        index: _currentIndex,
        children: const [
          WeatherPage(),
          NewsPage(),
        ],
      ),
      bottomNavigationBar: NavigationBar(
        selectedIndex: _currentIndex,
        onDestinationSelected: (index) =>
            setState(() => _currentIndex = index),
        destinations: const [
          NavigationDestination(
            icon: Icon(Icons.cloud_outlined),
            selectedIcon: Icon(Icons.cloud),
            label: 'Cuaca',
          ),
          NavigationDestination(
            icon: Icon(Icons.newspaper_outlined),
            selectedIcon: Icon(Icons.newspaper),
            label: 'Berita',
          ),
        ],
      ),
    );
  }
}
