package com.example.siraj.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.siraj.adapter.QuranApiAdapter
import com.example.siraj.model.QuranVerse
import com.example.siraj.network.QuranRepository
import com.example.siraj.network.Chapter
import com.example.siraj.network.Verse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class QuranUiState(
    val chapters: List<Chapter> = emptyList(),
    val verses: List<QuranVerse> = emptyList(),
    val currentChapter: Chapter? = null,
    val currentVerses: List<Verse> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchResults: List<QuranVerse> = emptyList()
)

class QuranViewModel : ViewModel() {
    private val repository = QuranRepository()

    private val _uiState = MutableStateFlow(QuranUiState())
    val uiState: StateFlow<QuranUiState> = _uiState.asStateFlow()

    init {
        loadChapters()
    }

    fun loadChapters() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getChapters().fold(
                onSuccess = { chapters ->
                    val quranVerses = QuranApiAdapter.chaptersToQuranVerses(chapters)
                    _uiState.value = _uiState.value.copy(
                        chapters = chapters,
                        verses = quranVerses,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun loadChapterVerses(chapterNumber: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.getChapterVerses(chapterNumber).fold(
                onSuccess = { verses ->
                    val chapter = _uiState.value.chapters.find { it.number == chapterNumber }
                    val quranVerse = QuranApiAdapter.versesToQuranVerse(verses, chapter)

                    val updatedVerses = _uiState.value.verses.map { verse ->
                        if (verse.id == chapterNumber) quranVerse else verse
                    }

                    _uiState.value = _uiState.value.copy(
                        verses = updatedVerses,
                        currentChapter = chapter,
                        currentVerses = verses,
                        isLoading = false
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        error = error.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun searchVerses(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val searchResults = _uiState.value.verses.filter { verse ->
                verse.sourate.contains(query, ignoreCase = true) ||
                        verse.texte.contains(query, ignoreCase = true) ||
                        verse.traduction.contains(query, ignoreCase = true) ||
                        verse.versets.any {
                            it.arabe.contains(query, ignoreCase = true) ||
                                    it.traduction.contains(query, ignoreCase = true) ||
                                    it.transliteration.contains(query, ignoreCase = true)
                        }
            }

            _uiState.value = _uiState.value.copy(
                searchResults = searchResults,
                isLoading = false
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSearchResults() {
        _uiState.value = _uiState.value.copy(searchResults = emptyList())
    }

    fun getChapterById(chapterId: Int): Chapter? {
        return _uiState.value.chapters.find { it.number == chapterId }
    }

    fun getVerseById(verseId: Int): QuranVerse? {
        return _uiState.value.verses.find { it.id == verseId }
    }

    fun refresh() {
        loadChapters()
    }

    fun preloadPopularChapters() {
        val popularChapters = listOf(1, 2, 3, 18, 36, 55, 67, 112, 113, 114)

        viewModelScope.launch {
            popularChapters.forEach { chapterNumber ->
                repository.getChapterVerses(chapterNumber).fold(
                    onSuccess = { verses ->
                        val chapter = _uiState.value.chapters.find { it.number == chapterNumber }
                        val quranVerse = QuranApiAdapter.versesToQuranVerse(verses, chapter)

                        val updatedVerses = _uiState.value.verses.map { verse ->
                            if (verse.id == chapterNumber) quranVerse else verse
                        }

                        _uiState.value = _uiState.value.copy(verses = updatedVerses)
                    },
                    onFailure = {
                        // Ignore error silently
                    }
                )
            }
        }
    }
}
