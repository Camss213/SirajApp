package com.example.siraj.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

        Text(
            "Siraj",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF00695C)
        )

        Button(
            onClick = { navController.navigate("quran") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4)),
            enabled = !isLoading
        ) {
            Text("📖 Lire le Coran", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate("quran_search") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4DB6AC))
        ) {
            Icon(Icons.Default.Search, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Rechercher", fontSize = 18.sp)
        }

        Button(
            onClick = { navController.navigate("prayer_times") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF26A69A))
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

        if (isLoading) {
            CircularProgressIndicator(color = Color(0xFF00695C))
        }

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Row {
                TextButton(onClick = onRetry) {
                    Text("Réessayer")
                }
                TextButton(onClick = onClearError) {
                    Text("Ignorer")
                }
            }
        }
    }
}
