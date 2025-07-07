package com.example.siraj.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Chapter(
    val number: Int,
    val name: String,
    val englishName: String,
    val englishNameTranslation: String,
)

@Serializable
data class Translation(
    val language: String,
    val name: String
)
