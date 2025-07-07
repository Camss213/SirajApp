package com.example.siraj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.siraj.ui.*
import com.example.siraj.ui.theme.Theme_Siraj
import com.example.siraj.viewmodel.QuranViewModel
import com.example.siraj.ui.HomeScreen
import com.example.siraj.ui.RappelsScreen



class MainActivity : ComponentActivity() {
    private val quranViewModel: QuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Theme_Siraj {
                val navController = rememberNavController()
                val uiState by quranViewModel.uiState.collectAsState()

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(
                            navController = navController,
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onRetry = { quranViewModel.refresh() },
                            onClearError = { quranViewModel.clearError() }
                        )
                    }

                    composable("quran_search") {
                        QuranSearchScreen(
                            searchResults = uiState.searchResults,
                            onBack = { navController.popBackStack() },
                            onSearch = { query ->
                                quranViewModel.searchVerses(query)
                            },
                            onClearSearch = { quranViewModel.clearSearchResults() },
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onClearError = { quranViewModel.clearError() }
                        )
                    }

                    composable("prayer_times") {
                        PrayerTimesScreen(onBack = { navController.popBackStack() })
                    }

                    composable("rappels") {
                        RappelsScreen(onBack = { navController.popBackStack() })
                    }

                    composable("quran") {
                        QuranListScreen(
                            context = this@MainActivity,
                            verses = uiState.verses,
                            onBack = { navController.popBackStack() },
                            onChapterClick = { chapterId -> quranViewModel.loadChapterVerses(chapterId) },
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onClearError = { quranViewModel.clearError() }
                        )
                    }

                }

                // Affichage des erreurs globales (SnackBar ou autre composable si besoin)
                uiState.error?.let { error ->
                    LaunchedEffect(error) {
                        // TODO : Afficher un snackbar/dialog si nécessaire
                    }
                }
            }
        }
    }
}
