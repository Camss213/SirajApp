package com.example.siraj.ui

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajwidCorrectionScreen(onBack: () -> Unit) {
    var transcript by remember { mutableStateOf("") }
    var correction by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            transcript = spokenText?.firstOrNull() ?: ""
            // TODO : Send to API or match locally
            correction = simulateCorrection(transcript)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Correction Tajwid", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Appuie pour réciter une sourate, nous corrigerons les erreurs de tajwid si possible.",
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Récite un verset")
                }
                launcher.launch(intent)
            }) {
                Text("🎙️ Lancer la reconnaissance")
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (transcript.isNotBlank()) {
                Text("🔊 Transcription :", fontWeight = FontWeight.Bold)
                Text(transcript, color = Color.DarkGray)
            }

            if (correction.isNotBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text("✅ Correction suggérée :", fontWeight = FontWeight.Bold)
                Text(correction, color = Color(0xFF1B5E20))
            }
        }
    }
}

fun simulateCorrection(text: String): String {
    return if (text.contains("قل")) {
        "Bonne récitation, pensez à l'emphase sur la lettre ق (Qaf)."
    } else if (text.isBlank()) {
        "Aucune donnée reconnue. Veuillez réessayer."
    } else {
        "Texte reçu. Aucune faute de tajwid détectée dans cette version bêta."
    }
}
