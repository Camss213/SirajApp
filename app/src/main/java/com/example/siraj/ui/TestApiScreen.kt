package com.example.siraj.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siraj.model.QuranVerse
import com.example.siraj.service.SimpleQuranApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestApiScreen(
    onBack: () -> Unit,
    onSuccess: (List<QuranVerse>) -> Unit,
    apiService: SimpleQuranApiService
) {
    var isLoading by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Prêt à tester l'API") }
    var hasError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = { Text("Test API Coran", fontSize = 22.sp, color = Color.Black) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "🧪 Test de l'API",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00695C)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = status,
                    fontSize = 16.sp,
                    color = Color(0xFF37474F)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color(0xFF00695C),
                        strokeWidth = 4.dp
                    )
                } else {
                    // Test 1 : Al-Fatiha seulement
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                status = "Test téléchargement Al-Fatiha..."
                                try {
                                    val alFatiha = apiService.testDownloadAlFatiha()
                                    if (alFatiha != null) {
                                        status = "✅ Al-Fatiha téléchargée ! ${alFatiha.versets.size} versets"
                                        hasError = false
                                    }
                                } catch (e: Exception) {
                                    status = "❌ Erreur: ${e.message}"
                                    hasError = true
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text("🧪 Test 1: Télécharger Al-Fatiha", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Test 2 : Sourates essentielles
                    Button(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val sourates = apiService.downloadEssentialSourates { progress ->
                                        status = progress
                                    }
                                    status = "✅ ${sourates.size} sourates téléchargées !"
                                    hasError = false

                                    // Passer les sourates téléchargées
                                    onSuccess(sourates)

                                } catch (e: Exception) {
                                    status = "❌ Erreur: ${e.message}"
                                    hasError = true
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                    ) {
                        Text("🚀 Test 2: Sourates Essentielles", fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Charger depuis cache
                    Button(
                        onClick = {
                            val cachedSourates = apiService.loadEssentialSourates()
                            if (cachedSourates.isNotEmpty()) {
                                status = "✅ ${cachedSourates.size} sourates chargées depuis le cache"
                                onSuccess(cachedSourates)
                            } else {
                                status = "❌ Aucune donnée en cache"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
                    ) {
                        Text("💾 Charger depuis Cache", fontSize = 16.sp)
                    }
                }

                if (hasError) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "🔧 Vérifiez votre connexion Internet",
                        fontSize = 14.sp,
                        color = Color(0xFFF44336)
                    )
                }
            }
        }
    }
}