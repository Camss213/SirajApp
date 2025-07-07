package com.example.siraj.adapter

import com.example.siraj.model.QuranVerse
import com.example.siraj.model.Verset
import com.example.siraj.network.Chapter
import com.example.siraj.network.Verse

object QuranApiAdapter {

    /**
     * Convertit un chapitre + liste de versets en QuranVerse
     */
    fun chapterToQuranVerse(chapter: Chapter, verses: List<Verse>): QuranVerse {
        return QuranVerse(
            id = chapter.number,
            sourate = "${chapter.englishName} - ${chapter.name}",
            texte = chapter.name,
            traduction = chapter.englishNameTranslation,
            audio = "", // Pas d’audio fourni par cette API
            versets = verses.map { verseToVerset(it) }
        )
    }

    /**
     * Convertit un verset brut de l’API en Verset local
     */
    fun verseToVerset(verse: Verse): Verset {
        return Verset(
            numero = verse.number,
            arabe = verse.text,
            traduction = "", // Ajout manuel possible plus tard
            transliteration = "", // API ne fournit pas encore ça
            audio = "" // Non fourni par cette édition
        )
    }

    /**
     * Convertit une liste de versets + un chapitre en QuranVerse
     */
    fun versesToQuranVerse(verses: List<Verse>, chapterInfo: Chapter?): QuranVerse {
        if (verses.isEmpty()) {
            return QuranVerse(
                id = 0,
                sourate = "",
                texte = "",
                traduction = "",
                audio = "",
                versets = emptyList()
            )
        }

        val chapterNumber = chapterInfo?.number ?: 0

        return QuranVerse(
            id = chapterNumber,
            sourate = chapterInfo?.englishName ?: "Sourate $chapterNumber",
            texte = chapterInfo?.name ?: "",
            traduction = chapterInfo?.englishNameTranslation ?: "",            audio = "",
            versets = verses.map { verseToVerset(it) }
        )
    }

    /**
     * Pour afficher toutes les sourates sur la page d’accueil
     */
    fun chaptersToQuranVerses(chapters: List<Chapter>): List<QuranVerse> {
        return chapters.map { chapter ->
            QuranVerse(
                id = chapter.number,
                sourate = "${chapter.englishName} - ${chapter.name}",
                texte = chapter.name,
                traduction = chapter.englishNameTranslation,
                audio = "",
                versets = emptyList()
            )
        }
    }

    /**
     * Pour un verset unique (ex: recherche par clé)
     */
    fun singleVerseToQuranVerse(verse: Verse, chapter: Chapter?): QuranVerse {
        val chapterNumber = chapter?.number ?: 0
        return QuranVerse(
            id = chapterNumber,
            sourate = chapter?.englishName ?: "Sourate $chapterNumber",
            texte = verse.text,
            traduction = "",
            audio = "",
            versets = listOf(verseToVerset(verse))
        )
    }
}
