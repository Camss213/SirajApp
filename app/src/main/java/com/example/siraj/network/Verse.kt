package com.example.siraj.network

import kotlinx.serialization.Serializable

@Serializable
data class Verse(
    val number: Int,
    val text: String
)

