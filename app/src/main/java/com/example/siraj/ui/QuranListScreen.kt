
package com.example.siraj.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siraj.model.QuranVerse
import com.example.siraj.utils.AudioPlayer
import kotlinx.coroutines.delay

val GradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
)
val ButtonColor = Color(0xFF80DEEA)
val DarkGreen = Color(0xFF004D40)
val TextDark = Color(0xFF1B5E20)
val TextLight = Color(0xFF4E342E)


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun QuranListScreen(
    context: Context,
    verses: List<QuranVerse>,
    onBack: () -> Unit,
    onChapterClick: (Int) -> Unit,
    isLoading: Boolean,
    error: String?,
    onClearError: () -> Unit
) {
    var currentVerse by remember { mutableStateOf<QuranVerse?>(null) }
    var showTranslation by remember { mutableStateOf(true) }
    var showTransliteration by remember { mutableStateOf(false) }
    var expandedVerses by remember { mutableStateOf(setOf<Int>()) }
    var readingMode by remember { mutableStateOf(false) }
    var textZoom by remember { mutableStateOf(1f)}

    Column(modifier = Modifier.fillMaxSize().background(GradientBackground)) {
        TopAppBar(
            title = {
                Text("Lecture du Coran", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                }
            },
            actions = {
                IconButton(onClick = { readingMode = !readingMode }) {
                    Icon(Icons.Default.MenuBook, contentDescription = "Lecture", tint = if (readingMode) DarkGreen else Color.Gray)
                }
                IconButton(onClick = { showTranslation = !showTranslation }) {
                    Icon(Icons.Default.Translate, contentDescription = "Traduction", tint = if (showTranslation) DarkGreen else Color.Gray)
                }
                IconButton(onClick = { showTransliteration = !showTransliteration }) {
                    Icon(Icons.Default.TextFields, contentDescription = "Translit.", tint = if (showTransliteration) DarkGreen else Color.Gray)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Taille du texte", fontSize = 14.sp)
            Slider(
                value = textZoom,
                onValueChange = { textZoom = it },
                valueRange = 0.8f..1.8f,
                steps = 5,
                modifier = Modifier.weight(1f).padding(start = 12.dp)
            )
        }


        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DarkGreen)
            }
        } else if (error != null) {
            Card(
                modifier = Modifier.padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Erreur : $error", color = Color.Red)
                    TextButton(onClick = onClearError) { Text("Fermer") }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(verses) { verse ->
                    val isExpanded = expandedVerses.contains(verse.id)

                    SourateCard(
                        verse = verse,
                        isExpanded = isExpanded,
                        isPlaying = currentVerse == verse,
                        readingMode = readingMode,
                        showTranslation = showTranslation,
                        showTransliteration = showTransliteration,
                        textSizeFactor = textZoom, //  on passe le zoom
                        onExpandToggle = {
                            expandedVerses = if (isExpanded) expandedVerses - verse.id else expandedVerses + verse.id
                            if (!isExpanded) onChapterClick(verse.id)
                        },
                        onPlayToggle = {
                            currentVerse = if (currentVerse == verse) null else verse
                            if (currentVerse == verse) AudioPlayer.play(context, verse.audio) else AudioPlayer.stop()
                        },
                        context = context
                    )
                }
            }
        }
    }
}


@Composable
fun SourateCard(
    verse: QuranVerse,
    isExpanded: Boolean,
    isPlaying: Boolean,
    readingMode: Boolean,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    textSizeFactor: Float,
    onExpandToggle: () -> Unit,
    onPlayToggle: () -> Unit,
    context: Context
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (isPlaying) Color(0xFFE0F7FA) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isPlaying) 8.dp else 4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // En-tête de la sourate
            SourateHeader(
                verse = verse,
                isExpanded = isExpanded,
                isPlaying = isPlaying,
                showTranslation = showTranslation,
                onExpandToggle = onExpandToggle,
                onPlayToggle = onPlayToggle
            )

            // Contrôles audio si en cours de lecture
            if (isPlaying) {
                Spacer(modifier = Modifier.height(12.dp))
                AudioControls()
            }

            // Contenu détaillé si développé
            if (isExpanded || readingMode) {
                Spacer(modifier = Modifier.height(16.dp))

                if (verse.versets.isNotEmpty()) {
                    // Affichage en mode lecture ou mode normal
                    if (readingMode) {
                        ReadingModeContent(
                            verse = verse,
                            showTranslation = showTranslation,
                            showTransliteration = showTransliteration,
                            textSizeFactor = textSizeFactor
                        )
                    } else {
                        VersetsList(
                            versets = verse.versets,
                            showTranslation = showTranslation,
                            showTransliteration = showTransliteration
                        )
                    }
                } else {
                    // Affichage simple si pas de versets détaillés
                    SimpleTextDisplay(
                        verse = verse,
                        showTranslation = showTranslation,
                        showTransliteration = showTransliteration
                    )
                }
            }
        }
    }
}

@Composable
fun SourateHeader(
    verse: QuranVerse,
    isExpanded: Boolean,
    isPlaying: Boolean,
    showTranslation: Boolean,
    onExpandToggle: () -> Unit,
    onPlayToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandToggle() },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = verse.sourate,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGreen,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = verse.texte,
                fontSize = 18.sp,
                color = TextDark,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 28.sp
            )

            if (showTranslation && verse.traduction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verse.traduction,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Bouton Play/Pause
            IconButton(onClick = onPlayToggle) {
                Icon(
                    imageVector = if (isPlaying && AudioPlayer.isPlaying())
                        Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = DarkGreen,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Icône expansion
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Réduire" else "Développer",
                tint = DarkGreen,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReadingModeContent(
    verse: QuranVerse,
    showTranslation: Boolean,
    showTransliteration: Boolean,
    textSizeFactor: Float = 1f
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            if (verse.id != 9) {
                Text(
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                    fontSize = (22 * textSizeFactor).sp,
                    fontWeight = FontWeight.Medium,
                    color = DarkGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = (36 * textSizeFactor).sp
                )

                if (showTranslation) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Au nom d’Allah, le Tout Miséricordieux, le Très Miséricordieux",
                        fontSize = (14 * textSizeFactor).sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            verse.versets.forEachIndexed { index, verset ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = buildAnnotatedString {
                            append(verset.arabe)
                            append("  ")

                            withStyle(
                                style = SpanStyle(
                                    fontSize = (20 * textSizeFactor).sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkGreen
                                )
                            ) {
                                append("۞ ${index + 1}")
                            }
                        },
                        textAlign = TextAlign.Center,
                        lineHeight = (36 * textSizeFactor).sp,
                        modifier = Modifier.fillMaxWidth()
                    )



                    if (showTransliteration && verset.transliteration.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = verset.transliteration,
                            fontSize = (14 * textSizeFactor).sp,
                            color = Color(0xFF888888),
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            lineHeight = (20 * textSizeFactor).sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (showTranslation && verset.traduction.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = verset.traduction,
                            fontSize = (16 * textSizeFactor).sp,
                            color = Color(0xFF37474F),
                            textAlign = TextAlign.Center,
                            lineHeight = (24 * textSizeFactor).sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (index < verse.versets.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Divider(color = Color(0xFFF0F0F0), thickness = 0.7.dp)
                    }
                }
            }
        }
    }
}



@Composable
fun VersetsList(
    versets: List<com.example.siraj.model.Verset>,
    showTranslation: Boolean,
    showTransliteration: Boolean
) {
    Text(
        text = "Versets :",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = DarkGreen,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    versets.forEach { verset ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Numéro du verset
                    Surface(
                        color = DarkGreen,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.size(28.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = verset.numero.toString(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Texte arabe
                    Text(
                        text = verset.arabe,
                        fontSize = 18.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f).padding(start = 12.dp),
                        color = TextDark,
                        lineHeight = 32.sp
                    )
                }

                // Translittération
                if (showTransliteration && verset.transliteration.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = verset.transliteration,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        fontStyle = FontStyle.Italic
                    )
                }

                // Traduction
                if (showTranslation) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = verset.traduction,
                        fontSize = 16.sp,
                        color = Color(0xFF37474F),
                        lineHeight = 24.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}


@Composable
fun SimpleTextDisplay(
    verse: QuranVerse,
    showTranslation: Boolean,
    showTransliteration: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Texte arabe
            Text(
                text = verse.texte,
                fontSize = 22.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1B5E20),
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                lineHeight = 34.sp
            )

            // Translittération
            if (showTransliteration && verse.transliteration.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = verse.transliteration,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    color = Color(0xFF555555),
                    lineHeight = 22.sp
                )
            }

            // Traduction
            if (showTranslation && verse.traduction.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = verse.traduction,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF37474F),
                    lineHeight = 24.sp
                )
            }
        }
    }
}



@Composable
private fun AudioControls() {
    var progress by remember { mutableStateOf(0f) }
    var timePassed by remember { mutableStateOf(0) }
    val totalDuration = 90

    LaunchedEffect(Unit) {
        progress = 0f
        timePassed = 0
        while (progress < 1f && AudioPlayer.isPlaying()) {
            delay(1000)
            timePassed++
            progress = timePassed.toFloat() / totalDuration
        }
    }

    Column {
        LinearProgressIndicator(
            progress = progress.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = ButtonColor,
            trackColor = Color(0xFFE0E0E0)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = String.format("%02d:%02d", timePassed / 60, timePassed % 60),
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = String.format("%02d:%02d", totalDuration / 60, totalDuration % 60),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { AudioPlayer.seekBackward() }) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Reculer",
                    tint = DarkGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            IconButton(onClick = { AudioPlayer.togglePause() }) {
                Icon(
                    imageVector = if (AudioPlayer.isPlaying()) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = DarkGreen,
                    modifier = Modifier.size(28.dp)
                )
            }
            IconButton(onClick = { AudioPlayer.seekForward() }) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Avancer",
                    tint = DarkGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}