package com.example.siraj.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DhikrScreen(onBack: () -> Unit) {
    data class Dhikr(
        val name: String,
        val arabic: String,
        val count: Int
    )

    val dhikrList = listOf(
        Dhikr("Subhanallah", "سُبْحَانَ اللَّهِ", 33),
        Dhikr("Alhamdulillah", "الْحَمْدُ لِلَّهِ", 33),
        Dhikr("Allahu Akbar", "اللَّهُ أَكْبَرُ", 33)
    )

    var selectedDhikr by remember { mutableStateOf(dhikrList[0]) }
    var currentCount by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Dhikr",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )

            // Sélecteur de dhikr
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(dhikrList) { dhikr ->
                    FilterChip(
                        selected = selectedDhikr == dhikr,
                        onClick = {
                            selectedDhikr = dhikr
                            currentCount = 0
                        },
                        label = { Text(dhikr.name) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = DarkGreen,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Carte principale
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Texte arabe
                    Text(
                        text = selectedDhikr.arabic,
                        fontSize = 36.sp,
                        color = TextDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Compteur
                    Text(
                        text = currentCount.toString(),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkGreen
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Boutons
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Bouton +1
                        Button(
                            onClick = { currentCount++ },
                            colors = ButtonDefaults.buttonColors(containerColor = ButtonColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("+1", color = Color.Black)
                        }

                        // Bouton Reset
                        Button(
                            onClick = { currentCount = 0 },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, ButtonColor),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reset", color = DarkGreen)
                        }
                    }

                    // Message objectif atteint
                    if (currentCount >= selectedDhikr.count) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Objectif: ${selectedDhikr.count} atteint!",
                            color = DarkGreen,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}