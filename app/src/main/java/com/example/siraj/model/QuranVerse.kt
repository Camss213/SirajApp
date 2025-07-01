package com.example.siraj.model

import kotlinx.serialization.Serializable



@Serializable
data class QuranVerse(
    val id: Int,
    val sourate: String,
    val texte: String,
    val audio: String,
    // Nouveaux champs OPTIONNELS avec des valeurs par défaut
    val texteArabe: String = texte,
    val traduction: String = "",
    val transliteration: String = "",
    val versets: List<Verset> = emptyList()
)

@Serializable
data class Verset(
    val numero: Int,
    val arabe: String,
    val traduction: String,
    val transliteration: String = ""
)
