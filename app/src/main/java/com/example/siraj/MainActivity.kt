package com.example.siraj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.example.siraj.model.QuranVerse
import com.example.siraj.service.OptimizedQuranApiService
import com.example.siraj.ui.DhikrScreen
import com.example.siraj.ui.OptimizedDownloadScreen
import com.example.siraj.ui.PrayerTimesScreen
import com.example.siraj.ui.QuranListScreen
import com.example.siraj.ui.TestApiScreen
import com.example.siraj.ui.theme.Theme_Siraj
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {

    private lateinit var simpleApiService: OptimizedQuranApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        simpleApiService = OptimizedQuranApiService(this)

        setContent {
            Theme_Siraj {
                val navController = rememberNavController()

                var verses by remember { mutableStateOf<List<QuranVerse>>(emptyList()) }

                // Charger au démarrage
                LaunchedEffect(Unit) {
                    // D'abord essayer le cache API
                    val cachedSourates = simpleApiService.loadEssentialSourates()
                    if (cachedSourates.isNotEmpty()) {
                        verses = cachedSourates
                    } else {
                        // Fallback vers votre JSON assets
                        verses = loadVersesFromAssets()
                    }
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        TestHomeScreen(
                            navController = navController,
                            hasApiData = simpleApiService.hasEssentialSourates(),
                            totalVerses = verses.size
                        )
                    }

                    composable("quran") {
                        QuranListScreen(
                            context = this@MainActivity,
                            verses = verses,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("test_api") {
                        TestApiScreen(
                            onBack = { navController.popBackStack() },
                            onSuccess = { downloadedSourates ->
                                verses = downloadedSourates
                                navController.navigate("quran")
                            },
                            apiService = simpleApiService
                        )
                    }

                    composable("prayer_times") {
                        PrayerTimesScreen(onBack = { navController.popBackStack() })
                    }
                    composable("dhikr") {
                        DhikrScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }

    private fun loadVersesFromAssets(): List<QuranVerse> {
        return try {
            val jsonStr = assets.open("quran.json").bufferedReader().use { it.readText() }
            Json.decodeFromString(jsonStr)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Composable
fun TestHomeScreen(
    navController: NavHostController,
    hasApiData: Boolean,
    totalVerses: Int
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFB2DFDB))
                )
            )
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo_siraj),
            contentDescription = "Logo",
            modifier = Modifier.size(120.dp)
        )

        Text("Siraj API Test", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00695C))

        // Statut API
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (hasApiData) Color(0xFFE8F5E8) else Color(0xFFFFF3E0)
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (hasApiData) "✅ API Fonctionnelle" else "⚠️ Pas de données API",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (hasApiData) Color(0xFF4CAF50) else Color(0xFFFF9800)
                )
                Text(
                    text = "$totalVerses sourates disponibles",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
        }

        Button(
            onClick = { navController.navigate("test_api") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("🧪 Tester l'API", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate("quran") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
        ) {
            Text("📖 Lire le Coran", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate("prayer_times") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC))
        ) {
            Text("🕌 Horaires de prière", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate("dhikr") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7FFEB))
        ) {
            Text("📿 Dhikr", fontSize = 18.sp, color = Color(0xFF004D40))
        }
    }
}