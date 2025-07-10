package com.example.siraj.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.siraj.R
import com.example.siraj.ui.theme.GradientBackground
import com.example.siraj.ui.theme.TextDark
import com.example.siraj.ui.theme.DarkGreen

@Composable
fun DhikrScreen(navController: NavController) {
    var compteur by remember { mutableStateOf(0) }
    var currentStep by remember { mutableStateOf(0) }

    val dhikrPhrases = listOf("SubhanAllah", "Alhamdulillah", "Allahu Akbar")
    val maxCount = 33
    val isFinished = currentStep >= dhikrPhrases.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
            .padding(16.dp)
    ) {
        // Logo retour à l'accueil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable {
                    navController.navigate("home") {
                        popUpTo(0) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lanterne),
                    contentDescription = "Retour à l'accueil",
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.dikhr),
                    contentDescription = "Icône Dhikr",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(0.85f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = MaterialTheme.shapes.large,
                elevation = CardDefaults.cardElevation(3.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Dhikr", fontSize = 18.sp, color = DarkGreen)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (!isFinished) dhikrPhrases[currentStep] else "La hawla wa la quwwata illa billah",
                        fontSize = 20.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("$compteur / $maxCount", fontSize = 32.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                if (!isFinished) {
                                    compteur++
                                    if (compteur >= maxCount) {
                                        compteur = 0
                                        currentStep++
                                    }
                                }
                            },
                            enabled = !isFinished,
                            colors = ButtonDefaults.buttonColors(containerColor = DarkGreen)
                        ) {
                            Text(if (!isFinished) "Cliquez" else "Terminé", color = Color.White)
                        }

                        OutlinedButton(onClick = {
                            compteur = 0
                            currentStep = 0
                        }) {
                            Text("Reset")
                        }
                    }
                }
            }
        }
    }
}
