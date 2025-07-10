package com.example.siraj.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.siraj.R
import com.example.siraj.ui.theme.GradientBackground
import kotlinx.coroutines.launch
import com.google.accompanist.pager.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RappelsSliderScreen(navController: NavController) {
    val pagerState = rememberPagerState(initialPage = 1)
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
        HorizontalPager(
            count = 3,
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { page ->
            when (page) {
                0 -> DhikrScreen(navController = navController)
                1 -> DouasScreen(navController = navController)
                2 -> VersetsScreen(navController = navController)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        BottomIconBar(
            currentPage = pagerState.currentPage,
            onPageSelected = { page ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(page)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun BottomIconBar(currentPage: Int, onPageSelected: (Int) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        val icons = listOf(
            R.drawable.dikhr,  // index 0
            R.drawable.base,   // index 1 = Douas
            R.drawable.quran   // index 2
        )

        icons.forEachIndexed { index, iconRes ->
            val backgroundColor = if (currentPage == index) Color(0xFFE0F2F1) else Color.White

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable { onPageSelected(index) }
                    .padding(8.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.fillMaxSize(0.8f)
                )
            }
        }
    }
}
