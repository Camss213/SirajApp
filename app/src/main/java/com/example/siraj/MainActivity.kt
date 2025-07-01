package com.example.siraj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.siraj.ui.DhikrScreen
import com.example.siraj.ui.PrayerTimesScreen
import com.example.siraj.ui.QuranListScreen
import com.example.siraj.ui.theme.Theme_Siraj
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val verses = loadVersesFromAssets()

        setContent {
            Theme_Siraj {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { HomeScreen(navController) }
                    composable("quran") {
                        QuranListScreen(
                            context = this@MainActivity,
                            verses = verses,
                            onBack = { navController.popBackStack() }
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
        val jsonStr = assets.open("quran.json").bufferedReader().use { it.readText() }
        return Json.decodeFromString(jsonStr)
    }
}

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE8F5E9), Color(0xFFB2DFDB))
                )
            )
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_siraj),
            contentDescription = "Logo",
            modifier = Modifier.size(140.dp)
        )
        Text("Siraj", fontSize = 36.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00695C))
        Button(
            onClick = { navController.navigate("quran") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4))
        ) {
            Text(" Lire le Coran", fontSize = 18.sp)
        }
        Button(
            onClick = { navController.navigate("prayer_times") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC))
        ) {
            Text("Horaires de prière", fontSize = 18.sp)
        }
        Button(
            onClick = { navController.navigate("dhikr") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFA7FFEB))
        ) {
            Text("Dhikr", fontSize = 18.sp, color = Color(0xFF004D40))
        }
    }
}