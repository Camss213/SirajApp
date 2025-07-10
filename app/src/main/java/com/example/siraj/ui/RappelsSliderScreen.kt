package com.example.siraj.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.siraj.R
import com.example.siraj.ui.theme.GradientBackground

@Composable
fun RappelsSliderScreen(navController: NavController) {
    var currentPage by remember { mutableStateOf(1) } // Douas par défaut

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
        // Zone de swipe
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -50 && currentPage < 2) currentPage++
                        else if (dragAmount > 50 && currentPage > 0) currentPage--
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when (currentPage) {
                0 -> DhikrScreen(navController = navController)
                1 -> DouasScreen(navController = navController)
                2 -> VersetsScreen(navController = navController)
            }
        }

        // Barre inférieure stylisée
        BottomRoundedBar(currentPage = currentPage, onIconClick = { currentPage = it })
    }
}

@Composable
fun BottomRoundedBar(currentPage: Int, onIconClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // ✅ Fond noir arrondi en image PNG
        Image(
            painter = painterResource(id = R.drawable.rond),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .align(Alignment.BottomCenter)
        )

        // ✅ Icône centrale (Doua) surélevée dans le creux
        Box(
            modifier = Modifier
                .size(76.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-38).dp) // Positionnement fin
                .clip(CircleShape)
                .background(if (currentPage == 1) Color(0xFFE0F2F1) else Color.White)
                .clickable { onIconClick(1) },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.doua),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }

        // ✅ Icônes gauche et droite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .offset(y = (-14).dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconItem(
                resId = R.drawable.quran,
                selected = currentPage == 0,
                onClick = { onIconClick(0) }
            )

            IconItem(
                resId = R.drawable.dikhr,
                selected = currentPage == 2,
                onClick = { onIconClick(2) }
            )
        }
    }
}

@Composable
fun IconItem(
    resId: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(if (selected) Color(0xFFE0F2F1) else Color.White)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(32.dp)
        )
    }
}
