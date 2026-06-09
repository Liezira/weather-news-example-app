class NewsArticle {
  final String id;
  final String title;
  final String body;
  final String published;
  final String source;

  const NewsArticle({
    required this.id,
    required this.title,
    required this.body,
    required this.published,
    required this.source,
  });

  factory NewsArticle.fromJson(Map<String, dynamic> json) {
    return NewsArticle(
      id: json['id']?.toString() ?? '',
      title: json['title']?.toString() ?? 'Tanpa Judul',
      body: json['body']?.toString() ?? '',
      published: json['published']?.toString() ?? '',
      source: json['source']?.toString() ?? '',
    );
  }
}
