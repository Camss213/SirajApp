package com.example.siraj.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.siraj.model.NomAllah
import com.example.siraj.model.nomsAllah
import com.example.siraj.ui.theme.DarkGreen
import com.example.siraj.ui.theme.GradientBackground
import com.example.siraj.ui.theme.TextDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RappelsScreen(onBack: () -> Unit) {
    var compteur by remember { mutableStateOf(0) }
    var currentStep by remember { mutableStateOf(0) }
    var selectedNom by remember { mutableStateOf<NomAllah?>(null) }

    val dhikrPhrases = listOf("SubhanAllah", "Allahu Akbar", "Alhamdulillah")
    val maxCount = 33
    val isFinished = currentStep >= dhikrPhrases.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rappels", fontWeight = FontWeight.Bold, fontSize = 22.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = Color.Transparent
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier
                .fillMaxSize()
                .background(GradientBackground)
                .padding(16.dp)
        ) {
            // Dhikr clicker
            item {
                DhikrClicker(
                    compteur = compteur,
                    currentStep = currentStep,
                    dhikrPhrases = dhikrPhrases,
                    maxCount = maxCount,
                    isFinished = isFinished,
                    onClick = {
                        if (!isFinished) {
                            compteur++
                            if (compteur >= maxCount) {
                                compteur = 0
                                currentStep++
                            }
                        }
                    },
                    onReset = {
                        compteur = 0
                        currentStep = 0
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Douas
            item {
                SectionTitle("📿 Douas utiles")
                ReminderCard("اللّهُـمَّ أَجِرْنِي مِنَ النّار", "Ô Allah, protège-moi du feu de l'Enfer.")
                ReminderCard("اللّهُـمَّ اغْفِرْ لي", "Ô Allah, pardonne-moi.")
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Versets & Hadiths
            item {
                SectionTitle("📖 Versets & Hadiths")
                ReminderCard("أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ", "C’est par l’évocation d’Allah que les cœurs se tranquillisent. (13:28)")
                ReminderCard("أفضلُ الذِّكرِ لا إلهَ إلَّا الله", "La meilleure parole est : 'Il n’y a de divinité qu’Allah' (Hadith, Muslim)")
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Noms d'Allah
            item {
                SectionTitle("🌟 Les 99 Noms d’Allah")
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                var expanded by remember { mutableStateOf(false) }
                val nomsAffiches = if (expanded) nomsAllah else nomsAllah.take(12)

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = if (expanded) 1000.dp else 300.dp)
                ) {
                    items(nomsAffiches) { nom ->
                        Column(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(10.dp))
                                .clickable { selectedNom = nom }
                                .padding(10.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = nom.nom,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = nom.phonetique,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)                ) {
                    Text(if (expanded) "Voir moins ▲" else "Voir plus ▼", fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

        }

        // Popup de signification
        if (selectedNom != null) {
            AlertDialog(
                onDismissRequest = { selectedNom = null },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(selectedNom!!.nom, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DarkGreen)
                        Text(selectedNom!!.phonetique, fontSize = 14.sp, color = Color.Gray)
                    }
                },
                text = {
                    Text(
                        text = selectedNom!!.signification,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Justify
                    )
                },
                confirmButton = {
                    TextButton(onClick = { selectedNom = null }) {
                        Text("Fermer")
                    }
                }
            )
        }
    }
}

@Composable
fun DhikrClicker(
    compteur: Int,
    currentStep: Int,
    dhikrPhrases: List<String>,
    maxCount: Int,
    isFinished: Boolean,
    onClick: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Dhikr", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DarkGreen)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = if (!isFinished) dhikrPhrases[currentStep] else "Dhikr terminé ✅",
                fontSize = 20.sp,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text("$compteur / $maxCount", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onClick,
                    enabled = !isFinished,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                ) {
                    Text(if (!isFinished) "Cliquez" else "Terminé", color = Color.White)
                }

                OutlinedButton(onClick = onReset) {
                    Text("Reset")
                }
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = DarkGreen)
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ReminderCard(arabic: String, translation: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = arabic,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = TextDark
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = translation,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = Color.DarkGray
            )
        }
    }
}
