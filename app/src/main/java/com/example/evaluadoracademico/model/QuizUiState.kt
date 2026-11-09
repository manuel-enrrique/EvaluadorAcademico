package com.example.evaluadoracademico.model

/**
 * Categorías disponibles para la evaluación.
 * Los valores se usan como identificadores de filtro y como etiquetas en la UI.
 */
object QuizCategories {
    const val KOTLIN = "Kotlin"
    const val REDES = "Redes"
    const val BASES_DE_DATOS = "Bases de Datos"

    val ALL: List<String> = listOf(KOTLIN, REDES, BASES_DE_DATOS)
}

/**
 * Estado inmutable que alimenta toda la pantalla. Se expone desde el ViewModel
 * mediante un StateFlow y la UI lo consume con collectAsStateWithLifecycle().
 *
 * Los campos "formatted..." se calculan en el ViewModel para que ningún
 * formateo de métricas se realice en los composables.
 */
data class QuizUiState(
    // Parámetros de entrada
    val studentName: String = "",
    val selectedCategory: String = QuizCategories.KOTLIN,
    val questionCount: Int = 5,
    val isStrictOption: Boolean = false,

    // Estado del flujo del examen
    val isQuizStarted: Boolean = false,
    val isQuizFinished: Boolean = false,
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int? = null,
    val score: Int = 0,
    val answerSubmitted: Boolean = false,

    // Métricas calculadas en el ViewModel
    val finalGrade: Double = 0.0,
    val accuracyPercentage: Int = 0,
    val performanceStatus: String = "",

    // Valores ya formateados y banderas derivadas por el ViewModel
    val formattedGrade: String = "0.0",
    val formattedAccuracy: String = "0%",
    val isPassed: Boolean = false,
    val isCurrentAnswerCorrect: Boolean = false
)
