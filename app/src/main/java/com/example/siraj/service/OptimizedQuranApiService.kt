package com.example.siraj.service

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.siraj.model.QuranVerse
import com.example.siraj.model.Verset
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class OptimizedQuranApiService(private val context: Context) {

    // TEST SIMPLE D'ABORD - UNE SEULE SOURATE
    suspend fun testDownloadAlFatiha(): QuranVerse? = suspendCancellableCoroutine { continuation ->
        val url = "http://api.alquran.cloud/v1/surah/1/editions/quran-uthmani,fr.hamidullah"
        val queue = Volley.newRequestQueue(context)

        Log.d("QuranAPI", "Test téléchargement Al-Fatiha...")

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    Log.d("QuranAPI", "Réponse reçue: ${response.take(200)}...")

                    val jsonResponse = JSONObject(response)
                    val data = jsonResponse.getJSONArray("data")

                    // Données arabes
                    val arabicSurah = data.getJSONObject(0)
                    val arabicAyahs = arabicSurah.getJSONArray("ayahs")

                    // Données traduction
                    val translationSurah = data.getJSONObject(1)
                    val translationAyahs = translationSurah.getJSONArray("ayahs")

                    val versets = mutableListOf<Verset>()

                    for (i in 0 until arabicAyahs.length()) {
                        val arabicAyah = arabicAyahs.getJSONObject(i)
                        val translationAyah = translationAyahs.getJSONObject(i)

                        val verset = Verset(
                            numero = arabicAyah.getInt("numberInSurah"),
                            arabe = arabicAyah.getString("text"),
                            traduction = translationAyah.getString("text"),
                            transliteration = ""
                        )
                        versets.add(verset)
                    }

                    val alFatiha = QuranVerse(
                        id = 1,
                        sourate = "Al-Fatiha (L'Ouverture)",
                        texte = "سورة الفاتحة",
                        audio = "al_fatiha",
                        traduction = "L'Ouverture",
                        versets = versets
                    )

                    Log.d("QuranAPI", "Al-Fatiha téléchargée: ${versets.size} versets")
                    continuation.resume(alFatiha)

                } catch (e: Exception) {
                    Log.e("QuranAPI", "Erreur parsing: ${e.message}")
                    continuation.resumeWithException(e)
                }
            },
            { error ->
                Log.e("QuranAPI", "Erreur réseau: ${error.message}")
                continuation.resumeWithException(error)
            }
        )

        queue.add(request)
        continuation.invokeOnCancellation { request.cancel() }
    }

    // TÉLÉCHARGER QUELQUES SOURATES IMPORTANTES D'ABORD
    suspend fun downloadEssentialSourates(
        onProgress: (String) -> Unit
    ): List<QuranVerse> = withContext(Dispatchers.IO) {

        val essentialSourates = listOf(1, 112, 113, 114, 110, 108, 109) // Les plus courtes
        val downloadedSourates = mutableListOf<QuranVerse>()

        for (sourateId in essentialSourates) {
            try {
                onProgress("Téléchargement sourate $sourateId...")
                val sourate = downloadSingleSourate(sourateId)
                if (sourate != null) {
                    downloadedSourates.add(sourate)
                }
                delay(500) // Pause entre requêtes
            } catch (e: Exception) {
                Log.e("QuranAPI", "Erreur sourate $sourateId: ${e.message}")
            }
        }

        // Sauvegarder
        saveToFile(downloadedSourates, "essential_sourates.json")
        downloadedSourates
    }

    // TÉLÉCHARGER UNE SOURATE SPÉCIFIQUE
    private suspend fun downloadSingleSourate(sourateId: Int): QuranVerse? = suspendCancellableCoroutine { continuation ->
        val url = "http://api.alquran.cloud/v1/surah/$sourateId/editions/quran-uthmani,fr.hamidullah"
        val queue = Volley.newRequestQueue(context)

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val jsonResponse = JSONObject(response)
                    val data = jsonResponse.getJSONArray("data")

                    val arabicSurah = data.getJSONObject(0)
                    val translationSurah = data.getJSONObject(1)

                    val arabicAyahs = arabicSurah.getJSONArray("ayahs")
                    val translationAyahs = translationSurah.getJSONArray("ayahs")

                    val versets = mutableListOf<Verset>()

                    for (i in 0 until arabicAyahs.length()) {
                        val arabicAyah = arabicAyahs.getJSONObject(i)
                        val translationAyah = translationAyahs.getJSONObject(i)

                        val verset = Verset(
                            numero = arabicAyah.getInt("numberInSurah"),
                            arabe = arabicAyah.getString("text"),
                            traduction = translationAyah.getString("text"),
                            transliteration = ""
                        )
                        versets.add(verset)
                    }

                    val sourateInfo = getSourateInfo(sourateId)
                    val quranVerse = QuranVerse(
                        id = sourateId,
                        sourate = "${sourateInfo.nom} (${sourateInfo.traduction})",
                        texte = sourateInfo.nomArabe,
                        audio = sourateInfo.audio,
                        traduction = sourateInfo.traduction,
                        versets = versets
                    )

                    continuation.resume(quranVerse)

                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            },
            { error ->
                continuation.resumeWithException(error)
            }
        )

        queue.add(request)
        continuation.invokeOnCancellation { request.cancel() }
    }

    // CHARGER LES SOURATES ESSENTIELLES DEPUIS LE CACHE
    fun loadEssentialSourates(): List<QuranVerse> {
        return try {
            val file = File(context.filesDir, "essential_sourates.json")
            if (file.exists()) {
                val jsonStr = file.readText()
                Json.decodeFromString(jsonStr)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // VÉRIFIER SI LES SOURATES ESSENTIELLES SONT TÉLÉCHARGÉES
    fun hasEssentialSourates(): Boolean {
        val file = File(context.filesDir, "essential_sourates.json")
        return file.exists() && file.length() > 1000
    }

    private fun saveToFile(verses: List<QuranVerse>, filename: String) {
        try {
            val json = Json.encodeToString(verses)
            val file = File(context.filesDir, filename)
            file.writeText(json)
            Log.d("QuranAPI", "Sauvegardé: $filename (${file.length()} bytes)")
        } catch (e: Exception) {
            Log.e("QuranAPI", "Erreur sauvegarde: ${e.message}")
        }
    }

    private fun getSourateInfo(id: Int): SourateInfo {
        val sourates = mapOf(
            1 to SourateInfo(1, "Al-Fatiha", "الفاتحة", "L'Ouverture", "al_fatiha"),
            112 to SourateInfo(112, "Al-Ikhlas", "الإخلاص", "Le Monothéisme Pur", "al_ikhlas"),
            113 to SourateInfo(113, "Al-Falaq", "الفلق", "L'Aube Naissante", "al_falaq"),
            114 to SourateInfo(114, "An-Nas", "الناس", "Les Hommes", "an_nas"),
            110 to SourateInfo(110, "An-Nasr", "النصر", "Les Secours", "an_nasr"),
            108 to SourateInfo(108, "Al-Kawthar", "الكوثر", "L'Abondance", "al_kawthar"),
            109 to SourateInfo(109, "Al-Kafirun", "الكافرون", "Les Infidèles", "al_kafirun")
        )
        return sourates[id] ?: sourates[1]!!
    }

    fun loadLocalQuran() {

    }
}

data class SourateInfo(
    val id: Int,
    val nom: String,
    val nomArabe: String,
    val traduction: String,
    val audio: String
)
