package com.example.siraj.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
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
import com.example.siraj.R

@Composable
fun HomeScreen(
    navController: NavHostController,
    isLoading: Boolean,
    error: String?,
    onRetry: () -> Unit,
    onClearError: () -> Unit
) {
    val menuItems = listOf(
        Triple("Lecture du Coran", R.drawable.quran1) { navController.navigate("quran") },
        Triple("Horaires de prière", R.drawable.prayer_mat) { navController.navigate("prayer_times") },
        Triple("Rappels & Douaas", R.drawable.doua) { navController.navigate("rappels_slider") },
        Triple("Tajwid", R.drawable.tajweed_chart) { navController.navigate("tajwid") }
    )

    var currentIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFAFEFCF), // haut
                        Color(0xFF85CCC1)  // bas
                    )
                )
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Logo Siraj
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(top = 50.dp, bottom = 50.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logosiraj),
                contentDescription = "Logo Siraj",
                modifier = Modifier.size(200.dp)
            )
        }

        // Menu slider
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            IconButton(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Précédent",
                    tint = Color(0xFF00695C)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(1f)
                    .clickable { menuItems[currentIndex].third() }
            ) {
                Image(
                    painter = painterResource(id = menuItems[currentIndex].second),
                    contentDescription = null,
                    modifier = Modifier
                        .size(180.dp)
                        .padding(bottom = 8.dp)
                        .offset(y = (-16).dp)
                )

                Text(
                    text = menuItems[currentIndex].first,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF00695C),
                    textAlign = TextAlign.Center
                )
            }

            IconButton(
                onClick = { if (currentIndex < menuItems.lastIndex) currentIndex++ },
                enabled = currentIndex < menuItems.lastIndex,
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForward,
                    contentDescription = "Suivant",
                    tint = Color(0xFF00695C)
                )
            }
        }

        // Loading
        if (isLoading) {
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator(color = Color(0xFF004D40))
        }

        // Erreur
        error?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Row {
                    TextButton(onClick = onRetry) { Text("Réessayer") }
                    TextButton(onClick = onClearError) { Text("Ignorer") }
                }
            }
        }
    }
}
