package com.example.evaluadoracademico.model

/**
 * Representa una pregunta de opción múltiple dentro del banco en memoria.
 */
data class Question(
    val id: Int,
    val category: String,
    val text: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String
)
