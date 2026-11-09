package com.example.evaluadoracademico.viewmodel

import androidx.lifecycle.ViewModel
import com.example.evaluadoracademico.model.Question
import com.example.evaluadoracademico.model.QuizCategories
import com.example.evaluadoracademico.model.QuizUiState
import java.util.Locale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel que concentra toda la lógica de negocio en memoria:
 * filtrado, mezcla, validación, cálculo de métricas y formateo.
 */
class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.update { state -> state.copy(studentName = name) }
    }

    fun onCategorySelected(category: String) {
        if (category !in QuizCategories.ALL) return
        _uiState.update { state -> state.copy(selectedCategory = category) }
    }

    fun onQuestionCountChanged(questionCount: Int) {
        _uiState.update { state ->
            state.copy(
                questionCount = questionCount.coerceIn(MIN_QUESTIONS, MAX_QUESTIONS)
            )
        }
    }

    fun onStrictOptionToggled(enabled: Boolean) {
        _uiState.update { state -> state.copy(isStrictOption = enabled) }
    }

    fun startQuiz() {
        _uiState.update { state ->
            val trimmedName = state.studentName.trim()
            if (trimmedName.isBlank()) return@update state

            val filteredQuestions = questionBank.filter { question ->
                question.category == state.selectedCategory
            }
            if (filteredQuestions.isEmpty()) return@update state

            val sessionQuestions = filteredQuestions
                .shuffled()
                .take(state.questionCount)

            state.copy(
                studentName = trimmedName,
                isQuizStarted = true,
                isQuizFinished = false,
                questions = sessionQuestions,
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                score = 0,
                answerSubmitted = false,
                finalGrade = 0.0,
                accuracyPercentage = 0,
                performanceStatus = "",
                formattedGrade = "0.0",
                formattedAccuracy = "0%",
                isPassed = false,
                isCurrentAnswerCorrect = false
            )
        }
    }

    fun selectAnswer(answerIndex: Int) {
        _uiState.update { state ->
            val question = state.questions.getOrNull(state.currentQuestionIndex)
                ?: return@update state

            if (answerIndex !in question.options.indices) return@update state
            if (state.answerSubmitted) return@update state

            // Modo estricto: la primera opción elegida queda bloqueada.
            if (state.isStrictOption && state.selectedAnswerIndex != null) {
                return@update state
            }

            state.copy(selectedAnswerIndex = answerIndex)
        }
    }

    fun submitAnswer() {
        _uiState.update { state ->
            val question = state.questions.getOrNull(state.currentQuestionIndex)
                ?: return@update state
            val selectedAnswer = state.selectedAnswerIndex ?: return@update state
            if (state.answerSubmitted) return@update state

            val isCorrect = selectedAnswer == question.correctAnswerIndex

            state.copy(
                score = state.score + if (isCorrect) 1 else 0,
                answerSubmitted = true,
                isCurrentAnswerCorrect = isCorrect
            )
        }
    }

    fun nextQuestion() {
        _uiState.update { state ->
            if (!state.answerSubmitted || state.isQuizFinished) return@update state

            if (state.currentQuestionIndex < state.questions.lastIndex) {
                state.copy(
                    currentQuestionIndex = state.currentQuestionIndex + 1,
                    selectedAnswerIndex = null,
                    answerSubmitted = false,
                    isCurrentAnswerCorrect = false
                )
            } else {
                finishQuiz(state)
            }
        }
    }

    fun restartQuiz() {
        _uiState.update { state ->
            state.copy(
                isQuizStarted = false,
                isQuizFinished = false,
                questions = emptyList(),
                currentQuestionIndex = 0,
                selectedAnswerIndex = null,
                score = 0,
                answerSubmitted = false,
                finalGrade = 0.0,
                accuracyPercentage = 0,
                performanceStatus = "",
                formattedGrade = "0.0",
                formattedAccuracy = "0%",
                isPassed = false,
                isCurrentAnswerCorrect = false
            )
        }
    }

    /**
     * Calcula y formatea todas las métricas finales dentro del ViewModel.
     */
    private fun finishQuiz(state: QuizUiState): QuizUiState {
        val totalQuestions = state.questions.size
        if (totalQuestions == 0) return state

        val finalGrade = (state.score.toDouble() / totalQuestions) * 10.0
        val accuracyPercentage =
            ((state.score.toDouble() / totalQuestions) * 100).toInt()

        val performanceStatus = when {
            finalGrade >= EXCELLENT_GRADE -> STATUS_EXCELLENT
            finalGrade >= PASSING_GRADE -> STATUS_SATISFACTORY
            else -> STATUS_NEEDS_REINFORCEMENT
        }

        return state.copy(
            isQuizFinished = true,
            finalGrade = finalGrade,
            accuracyPercentage = accuracyPercentage,
            performanceStatus = performanceStatus,
            formattedGrade = String.format(Locale.US, "%.1f", finalGrade),
            formattedAccuracy = "$accuracyPercentage%",
            isPassed = finalGrade >= PASSING_GRADE
        )
    }

    /**
     * Banco mock en memoria: 10 preguntas por cada una de las tres categorías.
     */
    private val questionBank: List<Question> = listOf(
        // ---------------------------- Kotlin ----------------------------
        Question(
            id = 1,
            category = QuizCategories.KOTLIN,
            text = "¿Qué palabra clave declara una variable de solo lectura en Kotlin?",
            options = listOf("var", "val", "lateinit", "const"),
            correctAnswerIndex = 1,
            explanation = "val declara una referencia inmutable: una vez asignado, su valor no puede reasignarse."
        ),
        Question(
            id = 2,
            category = QuizCategories.KOTLIN,
            text = "¿Qué tipo representa una función que no devuelve un valor útil?",
            options = listOf("Int", "Unit", "Nothing", "String"),
            correctAnswerIndex = 1,
            explanation = "Unit es el equivalente de void en Kotlin: la función se ejecuta pero no entrega un valor."
        ),
        Question(
            id = 3,
            category = QuizCategories.KOTLIN,
            text = "¿Qué operador permite realizar una llamada segura sobre un valor nulo?",
            options = listOf("!!", "?.", "?:", "as?"),
            correctAnswerIndex = 1,
            explanation = "El operador ?. ejecuta la llamada solamente si el receptor no es nulo; en caso contrario devuelve null."
        ),
        Question(
            id = 4,
            category = QuizCategories.KOTLIN,
            text = "¿Qué palabra clave permite comprobar el tipo de un objeto?",
            options = listOf("is", "as", "in", "when"),
            correctAnswerIndex = 0,
            explanation = "is comprueba el tipo en tiempo de ejecución y habilita el smart cast dentro del bloque."
        ),
        Question(
            id = 5,
            category = QuizCategories.KOTLIN,
            text = "¿Cuál es la forma correcta de declarar una función en Kotlin?",
            options = listOf(
                "fun suma(a: Int, b: Int): Int",
                "function suma(a, b)",
                "def suma(a: Int, b: Int)",
                "void suma(a, b)"
            ),
            correctAnswerIndex = 0,
            explanation = "Kotlin usa la palabra fun, coloca el tipo después del nombre y declara el retorno con dos puntos."
        ),
        Question(
            id = 6,
            category = QuizCategories.KOTLIN,
            text = "¿Cuál de estas colecciones es de solo lectura?",
            options = listOf("MutableList", "List", "ArrayList", "MutableSet"),
            correctAnswerIndex = 1,
            explanation = "List expone únicamente operaciones de lectura, mientras que MutableList permite agregar o quitar elementos."
        ),
        Question(
            id = 7,
            category = QuizCategories.KOTLIN,
            text = "¿Qué símbolo se utiliza para indicar herencia entre clases?",
            options = listOf("extends", ":", "implements", "super"),
            correctAnswerIndex = 1,
            explanation = "En Kotlin la herencia se declara con dos puntos: la clase hija indica la clase padre después de :."
        ),
        Question(
            id = 8,
            category = QuizCategories.KOTLIN,
            text = "¿Qué operador devuelve un valor por defecto cuando el receptor es nulo?",
            options = listOf("?.", "!!", "?:", "is"),
            correctAnswerIndex = 2,
            explanation = "El operador Elvis (?:) entrega el valor de la derecha cuando la expresión de la izquierda es null."
        ),
        Question(
            id = 9,
            category = QuizCategories.KOTLIN,
            text = "¿Qué tipo se utiliza para almacenar números enteros de 32 bits?",
            options = listOf("Int", "Double", "Boolean", "Char"),
            correctAnswerIndex = 0,
            explanation = "Int representa enteros de 32 bits; Double se usa para decimales y Boolean para verdadero o falso."
        ),
        Question(
            id = 10,
            category = QuizCategories.KOTLIN,
            text = "¿Qué característica permite añadir funciones a una clase sin modificarla?",
            options = listOf(
                "Herencia",
                "Funciones de extensión",
                "Interfaces",
                "Clases abstractas"
            ),
            correctAnswerIndex = 1,
            explanation = "Las funciones de extensión agregan comportamiento a un tipo existente sin editar su código fuente."
        ),

        // ---------------------------- Redes ----------------------------
        Question(
            id = 11,
            category = QuizCategories.REDES,
            text = "¿Qué protocolo traduce nombres de dominio a direcciones IP?",
            options = listOf("DHCP", "DNS", "FTP", "ARP"),
            correctAnswerIndex = 1,
            explanation = "DNS resuelve nombres como ejemplo.com a la dirección IP asociada al servidor."
        ),
        Question(
            id = 12,
            category = QuizCategories.REDES,
            text = "¿Cuántas capas tiene el modelo OSI?",
            options = listOf("4", "5", "7", "8"),
            correctAnswerIndex = 2,
            explanation = "El modelo OSI se divide en siete capas, desde Física hasta Aplicación."
        ),
        Question(
            id = 13,
            category = QuizCategories.REDES,
            text = "¿Qué dispositivo trabaja principalmente en la capa 2 del modelo OSI?",
            options = listOf("Router", "Switch", "Hub", "Repetidor"),
            correctAnswerIndex = 1,
            explanation = "El switch toma decisiones usando direcciones MAC, por lo que opera en la capa de enlace de datos."
        ),
        Question(
            id = 14,
            category = QuizCategories.REDES,
            text = "¿Cuál de estas direcciones IPv4 pertenece a un rango privado?",
            options = listOf("8.8.8.8", "192.168.1.10", "172.32.0.1", "11.0.0.1"),
            correctAnswerIndex = 1,
            explanation = "192.168.0.0/16 forma parte de los rangos privados definidos para redes locales."
        ),
        Question(
            id = 15,
            category = QuizCategories.REDES,
            text = "¿Qué protocolo ofrece entrega confiable y orientada a conexión?",
            options = listOf("UDP", "TCP", "ICMP", "ARP"),
            correctAnswerIndex = 1,
            explanation = "TCP establece una conexión, confirma la recepción y reordena los segmentos para garantizar la entrega."
        ),
        Question(
            id = 16,
            category = QuizCategories.REDES,
            text = "¿Qué significa una máscara de subred /24?",
            options = listOf(
                "24 hosts disponibles",
                "24 redes disponibles",
                "24 bits para la red",
                "24 bits para los hosts"
            ),
            correctAnswerIndex = 2,
            explanation = "Con /24 se reservan 24 bits para la parte de red y quedan 8 bits para los hosts."
        ),
        Question(
            id = 17,
            category = QuizCategories.REDES,
            text = "¿Qué protocolo asigna direcciones IP de forma automática?",
            options = listOf("DNS", "DHCP", "SMTP", "SNMP"),
            correctAnswerIndex = 1,
            explanation = "DHCP entrega configuración de red automáticamente, incluyendo dirección IP, máscara y puerta de enlace."
        ),
        Question(
            id = 18,
            category = QuizCategories.REDES,
            text = "¿Qué capa del modelo OSI se encarga del enrutamiento?",
            options = listOf(
                "Enlace de datos",
                "Red",
                "Transporte",
                "Aplicación"
            ),
            correctAnswerIndex = 1,
            explanation = "La capa de Red se ocupa del direccionamiento lógico y del enrutamiento entre redes distintas."
        ),
        Question(
            id = 19,
            category = QuizCategories.REDES,
            text = "¿Qué puerto utiliza HTTPS por defecto?",
            options = listOf("21", "80", "443", "25"),
            correctAnswerIndex = 2,
            explanation = "HTTPS usa el puerto 443 por defecto para el tráfico web cifrado con TLS."
        ),
        Question(
            id = 20,
            category = QuizCategories.REDES,
            text = "¿Qué dispositivo conecta redes diferentes y decide la ruta de los paquetes?",
            options = listOf("Switch", "Hub", "Router", "Módem"),
            correctAnswerIndex = 2,
            explanation = "El router analiza la dirección de destino y elige la ruta más adecuada para llegar a otra red."
        ),

        // ------------------------- Bases de Datos -------------------------
        Question(
            id = 21,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué instrucción SQL permite recuperar datos?",
            options = listOf("INSERT", "UPDATE", "SELECT", "DELETE"),
            correctAnswerIndex = 2,
            explanation = "SELECT consulta y devuelve filas de una o varias tablas según las condiciones indicadas."
        ),
        Question(
            id = 22,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué clave identifica de forma única cada fila de una tabla?",
            options = listOf(
                "Clave foránea",
                "Clave primaria",
                "Campo nulo",
                "Índice no único"
            ),
            correctAnswerIndex = 1,
            explanation = "La clave primaria no permite valores nulos ni duplicados, por lo que identifica cada fila de manera única."
        ),
        Question(
            id = 23,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué comando elimina una tabla junto con su estructura?",
            options = listOf("DELETE", "TRUNCATE", "DROP TABLE", "ALTER TABLE"),
            correctAnswerIndex = 2,
            explanation = "DROP TABLE borra la definición de la tabla y todos sus datos; DELETE y TRUNCATE solo eliminan filas."
        ),
        Question(
            id = 24,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué propiedad ACID garantiza que una transacción se aplique completa o no se aplique?",
            options = listOf("Atomicidad", "Consistencia", "Aislamiento", "Durabilidad"),
            correctAnswerIndex = 0,
            explanation = "La atomicidad trata la transacción como una unidad indivisible: todas sus operaciones se confirman o se revierten."
        ),
        Question(
            id = 25,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué tipo de relación se representa con una tabla intermedia?",
            options = listOf(
                "Uno a uno",
                "Uno a muchos",
                "Muchos a muchos",
                "Cero a uno"
            ),
            correctAnswerIndex = 2,
            explanation = "Una tabla intermedia almacena las claves de ambas entidades y resuelve una relación muchos a muchos."
        ),
        Question(
            id = 26,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué cláusula filtra filas individuales en una consulta SQL?",
            options = listOf("ORDER BY", "GROUP BY", "WHERE", "HAVING"),
            correctAnswerIndex = 2,
            explanation = "WHERE aplica condiciones antes de agrupar; HAVING filtra grupos ya agregados."
        ),
        Question(
            id = 27,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué instrucción agrega nuevas filas a una tabla?",
            options = listOf("INSERT INTO", "UPDATE", "SELECT", "CREATE"),
            correctAnswerIndex = 0,
            explanation = "INSERT INTO incorpora uno o varios registros nuevos a la tabla indicada."
        ),
        Question(
            id = 28,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué es una clave foránea?",
            options = listOf(
                "Un campo que identifica la fila",
                "Una referencia a la clave primaria de otra tabla",
                "Un índice único",
                "Un tipo de dato numérico"
            ),
            correctAnswerIndex = 1,
            explanation = "La clave foránea referencia la clave primaria de otra tabla y garantiza la integridad referencial."
        ),
        Question(
            id = 29,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué comando modifica datos existentes en una tabla?",
            options = listOf("UPDATE", "SELECT", "DROP", "CREATE"),
            correctAnswerIndex = 0,
            explanation = "UPDATE cambia los valores de las filas que cumplen la condición indicada en la cláusula WHERE."
        ),
        Question(
            id = 30,
            category = QuizCategories.BASES_DE_DATOS,
            text = "¿Qué forma normal elimina las dependencias parciales de una clave compuesta?",
            options = listOf(
                "Primera forma normal",
                "Segunda forma normal",
                "Tercera forma normal",
                "Forma normal de Boyce-Codd"
            ),
            correctAnswerIndex = 1,
            explanation = "La segunda forma normal exige cumplir la primera y eliminar las dependencias parciales respecto de una clave compuesta."
        )
    )

    companion object {
        const val MIN_QUESTIONS = 3
        const val MAX_QUESTIONS = 10

        const val EXCELLENT_GRADE = 8.0
        const val PASSING_GRADE = 6.0

        const val STATUS_EXCELLENT = "Excelente / Dominio Total"
        const val STATUS_SATISFACTORY = "Satisfactorio / Aprobado"
        const val STATUS_NEEDS_REINFORCEMENT = "Requiere Reforzamiento"
    }
}
