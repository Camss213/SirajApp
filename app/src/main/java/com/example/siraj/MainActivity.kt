package com.example.siraj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.siraj.ui.*
import com.example.siraj.ui.theme.Theme_Siraj
import com.example.siraj.viewmodel.QuranViewModel

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : Screen("home", "Accueil", Icons.Filled.Home)
    object Dhikr : Screen("dhikr", "Dhikr", Icons.Filled.Favorite)
    object Douas : Screen("douas", "Douas", Icons.Filled.Star)
    object Versets : Screen("versets", "Versets", Icons.Filled.MenuBook)
}

class MainActivity : ComponentActivity() {
    private val quranViewModel: QuranViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Theme_Siraj {
                val navController = rememberNavController()
                val uiState by quranViewModel.uiState.collectAsState()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                val bottomNavItems = listOf(Screen.Home, Screen.Dhikr, Screen.Douas, Screen.Versets)
                val showBottomBar = currentRoute in listOf(
                    Screen.Dhikr.route,
                    Screen.Douas.route,
                    Screen.Versets.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar {
                                bottomNavItems.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                                        label = { Text(screen.title) },
                                        selected = currentRoute == screen.route,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Home.route) {
                            HomeScreen(
                                navController = navController,
                                isLoading = uiState.isLoading,
                                error = uiState.error,
                                onRetry = { quranViewModel.refresh() },
                                onClearError = { quranViewModel.clearError() }
                            )
                        }

                        composable(Screen.Dhikr.route) {
                            DhikrScreen(navController = navController)
                        }

                        composable(Screen.Douas.route) {
                            DouasScreen(navController = navController)
                        }

                        composable(Screen.Versets.route) {
                            VersetsScreen(navController = navController)
                        }

                        composable("quran_search") {
                            QuranSearchScreen(
                                searchResults = uiState.searchResults,
                                onBack = { navController.popBackStack() },
                                onSearch = { query -> quranViewModel.searchVerses(query) },
                                onClearSearch = { quranViewModel.clearSearchResults() },
                                isLoading = uiState.isLoading,
                                error = uiState.error,
                                onClearError = { quranViewModel.clearError() }
                            )
                        }

                        composable("prayer_times") {
                            PrayerTimesScreen(onBack = { navController.popBackStack() })
                        }

                        composable("quran") {
                            QuranListScreen(
                                context = this@MainActivity,
                                verses = uiState.verses,
                                onBack = { navController.popBackStack() },
                                onChapterClick = { chapterId ->
                                    quranViewModel.loadChapterVerses(
                                        chapterId
                                    )
                                },
                                isLoading = uiState.isLoading,
                                error = uiState.error,
                                onClearError = { quranViewModel.clearError() }
                            )
                        }

                        composable("tajwid") {
                            TajwidCorrectionScreen(onBack = { navController.popBackStack() })
                        }

                        composable("rappels_slider") {
                            RappelsSliderScreen(navController = navController)
                        }

                    }
                }


                    // Affichage d'erreur global (optionnel)
                uiState.error?.let { error ->
                    LaunchedEffect(error) {
                        // Affiche une Snackbar ou une boîte de dialogue si besoin
                    }
                }
            }
        }
    }
}
