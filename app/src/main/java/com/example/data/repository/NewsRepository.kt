package com.example.data.repository

import com.example.BuildConfig
import com.example.data.api.NetworkClient
import com.example.data.model.news.Article
import com.example.data.model.news.ArticleSource
import com.example.data.model.news.NewsResponse
import retrofit2.HttpException
import java.io.IOException

class NewsRepository {
    private val newsApi = NetworkClient.newsApi

    suspend fun getTopHeadlines(category: String, country: String = "us"): Result<NewsResponse> {
        val apiKey = BuildConfig.NEWS_API_KEY
        val isDemo = apiKey.isBlank() || apiKey == "YOUR_NEWS_API_KEY"

        if (isDemo) {
            return Result.success(getSimulatedNews(category, "Demo Mode (API Key Belum Terpasang)"))
        }

        return try {
            val response = newsApi.getTopHeadlines(category = category, country = country, apiKey = apiKey)
            Result.success(response)
        } catch (e: HttpException) {
            if (e.code() == 401) {
                return Result.success(getSimulatedNews(category, "Simulasi (API Key 401 Unauthorized)"))
            }
            val message = when (e.code()) {
                429 -> "Batas request harian NewsAPI tercapai (100 request/hari pada tier gratis)"
                else -> "Kesalahan server berita: ${e.message()}"
            }
            Result.failure(Exception(message))
        } catch (e: IOException) {
            Result.success(getSimulatedNews(category, "Simulasi Offline (Tidak Ada Koneksi)"))
        } catch (e: Exception) {
            Result.failure(Exception("Terjadi kesalahan: ${e.localizedMessage ?: e.message}"))
        }
    }

    private fun getSimulatedNews(category: String, sourceTag: String): NewsResponse {
        val articles = when (category.lowercase()) {
            "technology" -> listOf(
                Article(
                    source = ArticleSource(id = "tech", name = sourceTag),
                    title = "Era Baru Inteligensi Buatan: GPT-5 Dikabarkan Mulai Rilis Terbatas",
                    description = "Perkembangan asisten AI generasi terbaru menunjukkan kemampuan penalaran yang menakjubkan mirip kognisi manusia.",
                    author = "Rian Wijaya",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1677442136019-21780efad99a?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T08:00:00Z"
                ),
                Article(
                    source = ArticleSource(id = "tech", name = sourceTag),
                    title = "Terobosan Komputasi Kuantum Terbaru Mampu Atasi Enkripsi dalam Detik",
                    description = "Para ilmuwan berhasil menstabilkan qubit pada suhu yang lebih bersahabat untuk komputasi super mutakhir.",
                    author = "Aisha Putri",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1635070041078-e363dbe005cb?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T07:15:00Z"
                )
            )
            "business" -> listOf(
                Article(
                    source = ArticleSource(id = "biz", name = sourceTag),
                    title = "IHSG Melambung Tinggi Ditopang Sektor Teknologi dan Konstruksi Hijau",
                    description = "Indeks Harga Saham Gabungan kembali mencetak rekor baru seiring masuknya investasi asing pada infrastruktur berkelanjutan.",
                    author = "Budi Hartono",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1590283603385-17ffb3a7f29f?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T09:30:00Z"
                ),
                Article(
                    source = ArticleSource(id = "biz", name = sourceTag),
                    title = "Startup Kendaraan Listrik Lokal Meraih Pendanaan Seri B Sebesar \$50 Juta",
                    description = "Langkah ekspansi baterai swapped di seluruh pelosok Asia Tenggara semakin nyata dan efisien.",
                    author = "Santi Natalia",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1563986768609-322da13575f3?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T06:45:00Z"
                )
            )
            "science" -> listOf(
                Article(
                    source = ArticleSource(id = "sci", name = sourceTag),
                    title = "teleskop luar angkasa Webb Temukan Tanda Air di Planet Luar Tata Surya",
                    description = "Penemuan komponen uap air pada planet berbatu membuka peluang riset kelayakan huni di galaksi tetangga.",
                    author = "Dr. Anto Semesta",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T05:00:00Z"
                )
            )
            "sports" -> listOf(
                Article(
                    source = ArticleSource(id = "sport", name = sourceTag),
                    title = "Timnas Meraih Kemenangan Dramatis di Menit Akhir Babak Kualifikasi",
                    description = "Gol salto spektakuler di waktu tambahan memastikan tiket emas menuju turnamen internasional mendatang.",
                    author = "Yudi Sportivo",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1508098682722-e99c43a406b2?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T04:20:00Z"
                )
            )
            "entertainment" -> listOf(
                Article(
                    source = ArticleSource(id = "show", name = sourceTag),
                    title = "Sutradara Tanah Air Menang Penghargaan Best Director di Festival Film Cannes",
                    description = "Karya epik bertema mitologi budaya lokal dikemas dengan sinematografi artistik yang memukau dewan juri dunia.",
                    author = "Larasati",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1517604931442-7e0c8ed2963c?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T03:00:00Z"
                )
            )
            "health" -> listOf(
                Article(
                    source = ArticleSource(id = "health", name = sourceTag),
                    title = "Metode Terapi Baru Klaim Mampu Mempercepat Pemulihan Cedera Sendi",
                    description = "Studi klinis terbaru membuktikan stimulasi getaran mikro mempercepat regenerasi sel hingga 40%.",
                    author = "dr. Amanda",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T02:40:00Z"
                )
            )
            else -> listOf( // "general"
                Article(
                    source = ArticleSource(id = "gen", name = sourceTag),
                    title = "Presidensi Global Menyetujui Pakta Iklim Zero-Emission 2040",
                    description = "Kesepakatan bersejarah ini mengharuskan pengurangan emisi karbon industri secara drastis melalui substitusi energi terbarukan.",
                    author = "Humas Dunia",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T09:00:00Z"
                ),
                Article(
                    source = ArticleSource(id = "gen", name = sourceTag),
                    title = "Inovasi Filter Air Murah Berbasis Karbon Aktif Bambu Mulai Dikirim ke Desa-Desa",
                    description = "Solusi penyedia air bersih ramah lingkungan ini sepenuhnya diproduksi secara mandiri oleh karang taruna setempat.",
                    author = "Dewilestari",
                    url = "https://aistudio.google.com",
                    urlToImage = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?w=600&auto=format&fit=crop&q=80",
                    publishedAt = "2026-06-09T08:30:00Z"
                )
            )
        }
        return NewsResponse(status = "ok", totalResults = articles.size, articles = articles)
    }
}
