package com.example.siraj.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.example.siraj.network.Chapter
import com.example.siraj.network.Verse


class QuranRepository {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    /**
     * Récupère toutes les sourates
     */
    suspend fun getChapters(): Result<List<Chapter>> = try {
        val response: ChaptersResponse = client
            .get("https://api.alquran.cloud/v1/surah")
            .body()
        Result.success(response.data)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * Récupère les versets d'une sourate
     */
    suspend fun getChapterVerses(chapterNumber: Int): Result<List<Verse>> = try {
        val response: VersesResponse = client
            .get("https://api.alquran.cloud/v1/surah/$chapterNumber") {
                parameter("edition", "quran-uthmani")
            }
            .body()
        Result.success(response.data.ayahs)
    } catch (e: Exception) {
        Result.failure(e)
    }
}


@Serializable
data class ChaptersResponse(
    val data: List<Chapter>
)

@Serializable
data class VersesResponse(
    val data: VersesData
)

@Serializable
data class VersesData(
    val ayahs: List<Verse>
)


