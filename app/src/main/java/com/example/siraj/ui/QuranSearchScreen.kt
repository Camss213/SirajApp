package com.example.siraj.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.siraj.model.QuranVerse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSearchScreen(
    searchResults: List<QuranVerse>,
    onBack: () -> Unit,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    isLoading: Boolean = false,
    error: String? = null,
    onClearError: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Barre de titre
            TopAppBar(
                title = {
                    Text(
                        "Recherche dans le Coran",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    if (searchResults.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            onClearSearch()
                        }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Effacer",
                                tint = Color.Black
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )

            // Barre de recherche
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Rechercher",
                        tint = Color(0xFF00695C),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { newQuery ->
                            searchQuery = newQuery
                            if (newQuery.length >= 2) {
                                onSearch(newQuery)
                            } else if (newQuery.isEmpty()) {
                                onClearSearch()
                            }
                        },
                        placeholder = {
                            Text(
                                "Rechercher sourates, versets...",
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                if (searchQuery.isNotEmpty()) {
                                    onSearch(searchQuery)
                                }
                                keyboardController?.hide()
                            }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF00695C),
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchQuery = ""
                                onClearSearch()
                            }
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Effacer recherche",
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }

            // Conseils de recherche
            if (searchQuery.isEmpty() && searchResults.isEmpty()) {
                SearchTips()
            }

            // Indicateur de chargement
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF00695C),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Recherche en cours...",
                            fontSize = 16.sp,
                            color = Color(0xFF00695C)
                        )
                    }
                }
            }

            // Affichage des erreurs
            error?.let { errorMessage ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = Color(0xFFD32F2F),
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Erreur de recherche",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD32F2F)
                        )
                        Text(
                            errorMessage,
                            fontSize = 14.sp,
                            color = Color(0xFF757575),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = onClearError) {
                            Text("Fermer", color = Color(0xFF00695C))
                        }
                    }
                }
            }

            // Résultats de recherche
            if (!isLoading && error == null) {
                when {
                    searchQuery.isNotEmpty() && searchResults.isEmpty() -> {
                        NoResultsFound(searchQuery)
                    }
                    searchResults.isNotEmpty() -> {
                        SearchResults(searchResults, searchQuery)
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchTips() {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFF00695C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Conseils de recherche",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val tips = listOf(
                        "Recherchez par nom de sourate : « Al-Fatiha », « La Vache »",
                        "Utilisez des mots-clés en français ou en arabe",
                        "Recherchez des concepts : « paradis », « miséricorde »",
                        "Tapez au moins 2 caractères pour lancer la recherche",
                        "La recherche inclut les traductions et translittérations"
                    )

                    tips.forEach { tip ->
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Text(
                                "•",
                                fontSize = 16.sp,
                                color = Color(0xFF00695C),
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                tip,
                                fontSize = 14.sp,
                                color = Color(0xFF37474F),
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = Color(0xFF689F38),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Recherches populaires",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF689F38)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val popularSearches = listOf(
                        "Al-Fatiha", "Ayat al-Kursi", "Al-Ikhlas",
                        "Miséricorde", "Paradis", "Patience"
                    )

                    popularSearches.chunked(2).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            row.forEach { search ->
                                SuggestionChip(
                                    onClick = { /* TODO: Implémenter la recherche suggestion */ },
                                    label = {
                                        Text(
                                            search,
                                            fontSize = 12.sp
                                        )
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NoResultsFound(query: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SearchOff,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Aucun résultat trouvé",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF37474F)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Aucun résultat pour « $query »",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Essayez avec d'autres mots-clés ou vérifiez l'orthographe",
                fontSize = 14.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SearchResults(results: List<QuranVerse>, query: String) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                "${results.size} résultat(s) trouvé(s) pour « $query »",
                fontSize = 14.sp,
                color = Color(0xFF00695C),
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(results) { verse ->
            SearchResultCard(verse = verse, query = query)
        }
    }
}

@Composable
private fun SearchResultCard(verse: QuranVerse, query: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // En-tête de la sourate
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = verse.sourate,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00695C),
                    modifier = Modifier.weight(1f)
                )

                Surface(
                    color = Color(0xFF00695C).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${verse.versets.size} verset(s)",
                        fontSize = 12.sp,
                        color = Color(0xFF00695C),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Aperçu du texte
            if (verse.versets.isNotEmpty()) {
                val firstVerse = verse.versets.first()

                Text(
                    text = firstVerse.arabe,
                    fontSize = 16.sp,
                    color = Color(0xFF37474F),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = firstVerse.traduction,
                    fontSize = 14.sp,
                    color = Color(0xFF666666),
                    lineHeight = 20.sp,
                    maxLines = 3
                )

                if (verse.versets.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "... et ${verse.versets.size - 1} autre(s) verset(s)",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            } else {
                Text(
                    text = verse.texte,
                    fontSize = 16.sp,
                    color = Color(0xFF37474F),
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 24.sp,
                    maxLines = 2
                )

                if (verse.traduction.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = verse.traduction,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        lineHeight = 20.sp,
                        maxLines = 3
                    )
                }
            }
        }
    }
}