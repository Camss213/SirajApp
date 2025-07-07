package com.example.siraj.model

import kotlinx.serialization.Serializable



data class QuranVerse(
    val id: Int,
    val sourate: String,
    val texte: String,
    val audio: String,
    val texteArabe: String = texte,
    val traduction: String = "",
    val transliteration: String = "",
    val versets: List<Verset> = emptyList()
)

data class Verset(
    val numero: Int,
    val arabe: String,
    val traduction: String,
    val transliteration: String = "",
    val audio: String
)


