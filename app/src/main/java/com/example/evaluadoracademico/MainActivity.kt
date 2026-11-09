package com.example.evaluadoracademico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.evaluadoracademico.ui.QuizScreen
import com.example.evaluadoracademico.ui.theme.EvaluadorAcademicoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EvaluadorAcademicoTheme {
                QuizScreen()
            }
        }
    }
}
