package com.example.siraj.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siraj.model.QuranVerse
import com.example.siraj.service.OptimizedQuranApiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptimizedDownloadScreen(
    onBack: () -> Unit,
    onDownloadComplete: (List<QuranVerse>) -> Unit,
    apiService: OptimizedQuranApiService
) {
    var isDownloading by remember { mutableStateOf(false) }
    var downloadStatus by remember { mutableStateOf("") }
    var isCompleted by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text("Télécharger le Coran Complet", fontSize = 20.sp, color = Color.Black)
                },
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

                when {
                    isCompleted -> {
                        // Téléchargement terminé
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Terminé",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF4CAF50)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Coran Complet Téléchargé !",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "✅ 114 sourates\n✅ 6236 versets en arabe\n✅ Traductions françaises",
                            fontSize = 16.sp,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                val verses = apiService.loadLocalQuran()
                                onDownloadComplete(verses)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                        ) {
                            Text("Accéder au Coran", fontSize = 18.sp)
                        }
                    }

                    isDownloading -> {
                        // En cours de téléchargement
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = Color(0xFF00695C),
                            strokeWidth = 6.dp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Téléchargement en cours...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = downloadStatus,
                            fontSize = 16.sp,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center
                        )
                    }

                    hasError -> {
                        // Erreur
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Erreur",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFFF44336)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Erreur de téléchargement",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF44336)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Vérifiez votre connexion Internet et réessayez.",
                            fontSize = 16.sp,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                hasError = false
                                scope.launch {
                                    isDownloading = true
                                    try {
                                        val verses = apiService.downloadCompleteQuran { status ->
                                            downloadStatus = status
                                        }
                                        isDownloading = false
                                        isCompleted = true
                                    } catch (e: Exception) {
                                        isDownloading = false
                                        hasError = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                        ) {
                            Text("Réessayer", fontSize = 18.sp)
                        }
                    }

                    else -> {
                        // Prêt à télécharger
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Télécharger",
                            modifier = Modifier.size(80.dp),
                            tint = Color(0xFF00695C)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Télécharger le Coran Complet",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "🚀 SUPER RAPIDE !\n• Seulement 2 requêtes au lieu de 114\n• Téléchargement en ~30 secondes\n• 114 sourates complètes\n• 6236 versets avec traductions",
                            fontSize = 16.sp,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = {
                                scope.launch {
                                    isDownloading = true
                                    try {
                                        val verses = apiService.downloadCompleteQuran { status ->
                                            downloadStatus = status
                                        }
                                        isDownloading = false
                                        isCompleted = true
                                    } catch (e: Exception) {
                                        isDownloading = false
                                        hasError = true
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C))
                        ) {
                            Text("🚀 Téléchargement Ultra-Rapide", fontSize = 18.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "⚡ Optimisé avec vos URLs\n📱 ~2-3 MB • 📶 Internet requis",
                            fontSize = 14.sp,
                            color = Color(0xFF666666),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}



