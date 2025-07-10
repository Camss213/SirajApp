package com.example.siraj.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.siraj.R
import com.example.siraj.ui.theme.GradientBackground
import com.example.siraj.ui.theme.TextDark

@Composable
fun VersetsScreen(navController: NavController? = null) {
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
                    navController?.navigate("home") {
                        popUpTo("home") { inclusive = true }
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
                    painter = painterResource(id = R.drawable.quran),
                    contentDescription = "Icône Coran",
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

//        Text(
//            text = "📖 Versets & Hadiths",
//            fontSize = 18.sp,
//            color = TextDark,
//            modifier = Modifier.align(Alignment.CenterHorizontally)
//        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            ReminderCard(
                arabic = "أَلَا بِذِكْرِ اللَّهِ تَطْمَئِنُّ الْقُلُوبُ",
                translation = "C’est par l’évocation d’Allah que les cœurs se tranquillisent. (13:28)"
            )
            ReminderCard(
                arabic = "أفضلُ الذِّكرِ لا إلهَ إلَّا الله",
                translation = "La meilleure parole est : 'Il n’y a de divinité qu’Allah' (Hadith, Muslim)"
            )
        }
    }
}
