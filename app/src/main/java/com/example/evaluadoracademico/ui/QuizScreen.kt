package com.example.evaluadoracademico.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.evaluadoracademico.R
import com.example.evaluadoracademico.model.Question
import com.example.evaluadoracademico.model.QuizCategories
import com.example.evaluadoracademico.model.QuizUiState
import com.example.evaluadoracademico.ui.theme.ErrorRed
import com.example.evaluadoracademico.ui.theme.EvaluadorAcademicoTheme
import com.example.evaluadoracademico.ui.theme.SuccessGreen
import com.example.evaluadoracademico.ui.theme.WarningAmber
import com.example.evaluadoracademico.ui.theme.errorColor
import com.example.evaluadoracademico.ui.theme.successColor
import com.example.evaluadoracademico.ui.theme.warningColor
import com.example.evaluadoracademico.viewmodel.QuizViewModel
import kotlin.math.roundToInt

/**
 * Punto de entrada de la pantalla. Es el único composable que conoce el ViewModel:
 * recoge el estado con collectAsStateWithLifecycle() y propaga eventos mediante lambdas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(viewModel: QuizViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    QuizContent(
        state = state,
        onNameChanged = viewModel::onNameChanged,
        onCategorySelected = viewModel::onCategorySelected,
        onQuestionCountChanged = viewModel::onQuestionCountChanged,
        onStrictOptionToggled = viewModel::onStrictOptionToggled,
        onStartQuiz = viewModel::startQuiz,
        onAnswerSelected = viewModel::selectAnswer,
        onSubmitAnswer = viewModel::submitAnswer,
        onNextQuestion = viewModel::nextQuestion,
        onRestartQuiz = viewModel::restartQuiz
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizContent(
    state: QuizUiState,
    onNameChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onQuestionCountChanged: (Int) -> Unit,
    onStrictOptionToggled: (Boolean) -> Unit,
    onStartQuiz: () -> Unit,
    onAnswerSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    onRestartQuiz: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.app_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.30f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 640.dp)
                        .fillMaxWidth()
                ) {
                    when {
                        !state.isQuizStarted -> {
                            ConfigSection(
                                studentName = state.studentName,
                                selectedCategory = state.selectedCategory,
                                questionCount = state.questionCount,
                                isStrictOption = state.isStrictOption,
                                onNameChanged = onNameChanged,
                                onCategorySelected = onCategorySelected,
                                onQuestionCountChanged = onQuestionCountChanged,
                                onStrictOptionToggled = onStrictOptionToggled,
                                onStartQuiz = onStartQuiz
                            )
                        }

                        state.isQuizFinished -> {
                            ResultSection(
                                state = state,
                                onRestartQuiz = onRestartQuiz
                            )
                        }

                        else -> {
                            val question = state.questions.getOrNull(state.currentQuestionIndex)
                            if (question != null) {
                                QuizSection(
                                    question = question,
                                    currentQuestionIndex = state.currentQuestionIndex,
                                    totalQuestions = state.questions.size,
                                    score = state.score,
                                    selectedAnswerIndex = state.selectedAnswerIndex,
                                    answerSubmitted = state.answerSubmitted,
                                    isCurrentAnswerCorrect = state.isCurrentAnswerCorrect,
                                    isStrictOption = state.isStrictOption,
                                    onAnswerSelected = onAnswerSelected,
                                    onSubmitAnswer = onSubmitAnswer,
                                    onNextQuestion = onNextQuestion
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Configuración
// ---------------------------------------------------------------------------

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ConfigSection(
    studentName: String,
    selectedCategory: String,
    questionCount: Int,
    isStrictOption: Boolean,
    onNameChanged: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onQuestionCountChanged: (Int) -> Unit,
    onStrictOptionToggled: (Boolean) -> Unit,
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val warning = warningColor()
    val warningContainer = warning.copy(
        alpha = if (isSystemInDarkTheme()) 0.20f else 0.12f
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HeroHeader()

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                SectionHeading(
                    icon = Icons.Filled.Person,
                    title = stringResource(R.string.config_title),
                    subtitle = stringResource(R.string.config_subtitle)
                )

                OutlinedTextField(
                    value = studentName,
                    onValueChange = onNameChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(R.string.label_student_name)) },
                    placeholder = { Text(stringResource(R.string.placeholder_student_name)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null
                        )
                    },
                    supportingText = {
                        Text(stringResource(R.string.supporting_student_name))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = stringResource(R.string.label_subject),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizCategories.ALL.forEach { category ->
                            val selected = selectedCategory == category
                            FilterChip(
                                selected = selected,
                                onClick = { onCategorySelected(category) },
                                label = { Text(category) },
                                leadingIcon = if (selected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(FilterChipDefaults.IconSize)
                                        )
                                    }
                                } else {
                                    null
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimary
                                )
                            )
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_question_count),
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = pluralStringResource(
                                    R.plurals.question_count_value,
                                    questionCount,
                                    questionCount
                                ),
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 6.dp
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Slider(
                        value = questionCount.toFloat(),
                        onValueChange = { value ->
                            onQuestionCountChanged(value.roundToInt())
                        },
                        valueRange = 3f..10f,
                        steps = 6,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.question_range_min),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = stringResource(R.string.question_range_max),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isStrictOption) {
                            warningContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = if (isStrictOption) {
                                Icons.Filled.Warning
                            } else {
                                Icons.Filled.Info
                            },
                            contentDescription = null,
                            tint = if (isStrictOption) {
                                warning
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.label_strict_mode),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = stringResource(R.string.strict_mode_description),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = isStrictOption,
                            onCheckedChange = onStrictOptionToggled,
                            colors = SwitchDefaults.colors(
                                checkedTrackColor = WarningAmber,
                                checkedThumbColor = Color.White
                            )
                        )
                    }
                }

                if (studentName.isBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = warning
                        )
                        Text(
                            text = stringResource(R.string.name_required),
                            style = MaterialTheme.typography.bodySmall,
                            color = warning
                        )
                    }
                }

                Button(
                    onClick = onStartQuiz,
                    enabled = studentName.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.action_start_quiz),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroHeader() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = 6.dp,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(34.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.hero_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.app_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SectionHeading(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(44.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Examen
// ---------------------------------------------------------------------------

@Composable
fun QuizSection(
    question: Question,
    currentQuestionIndex: Int,
    totalQuestions: Int,
    score: Int,
    selectedAnswerIndex: Int?,
    answerSubmitted: Boolean,
    isCurrentAnswerCorrect: Boolean,
    isStrictOption: Boolean,
    onAnswerSelected: (Int) -> Unit,
    onSubmitAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progress by animateFloatAsState(
        targetValue = if (totalQuestions > 0) {
            (currentQuestionIndex + 1).toFloat() / totalQuestions
        } else {
            0f
        },
        label = "quiz_progress"
    )
    val isLastQuestion = currentQuestionIndex == totalQuestions - 1
    val actionLabel = when {
        answerSubmitted && isLastQuestion -> R.string.action_show_results
        answerSubmitted -> R.string.action_next_question
        else -> R.string.action_submit_answer
    }
    val actionIcon = when {
        answerSubmitted && isLastQuestion -> Icons.Filled.CheckCircle
        answerSubmitted -> Icons.AutoMirrored.Filled.ArrowForward
        else -> Icons.AutoMirrored.Filled.Send
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(
                                R.string.quiz_progress,
                                currentQuestionIndex + 1,
                                totalQuestions
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = question.category,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text = stringResource(R.string.quiz_score, score),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                )
            }
        }

        if (isStrictOption) {
            StatusBanner(
                title = stringResource(R.string.strict_banner_title),
                message = stringResource(R.string.strict_banner_message)
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = question.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Text(
                    text = question.text,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    question.options.forEachIndexed { index, option ->
                        val isSelected = selectedAnswerIndex == index
                        val locked = answerSubmitted ||
                            (isStrictOption && selectedAnswerIndex != null)

                        AnswerOption(
                            index = index,
                            text = option,
                            isSelected = isSelected,
                            isCorrectOption = question.correctAnswerIndex == index,
                            answerSubmitted = answerSubmitted,
                            enabled = !locked,
                            onClick = { onAnswerSelected(index) }
                        )
                    }
                }
            }
        }

        if (answerSubmitted) {
            ExplanationCard(
                question = question,
                isCorrect = isCurrentAnswerCorrect
            )
        }

        Button(
            onClick = if (answerSubmitted) onNextQuestion else onSubmitAnswer,
            enabled = if (answerSubmitted) true else selectedAnswerIndex != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = actionIcon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(actionLabel),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AnswerOption(
    index: Int,
    text: String,
    isSelected: Boolean,
    isCorrectOption: Boolean,
    answerSubmitted: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val isRevealedCorrect = answerSubmitted && isCorrectOption
    val isRevealedWrong = answerSubmitted && isSelected && !isCorrectOption

    val targetContainerColor = when {
        isRevealedCorrect -> SuccessGreen
        isRevealedWrong -> ErrorRed
        isSelected -> MaterialTheme.colorScheme.primary
        answerSubmitted -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
        else -> MaterialTheme.colorScheme.surface
    }
    val containerColor by animateColorAsState(
        targetValue = targetContainerColor,
        label = "answer_container"
    )

    val contentColor = when {
        isRevealedCorrect || isRevealedWrong -> Color.White
        isSelected -> MaterialTheme.colorScheme.onPrimary
        answerSubmitted -> MaterialTheme.colorScheme.onSurfaceVariant
        else -> MaterialTheme.colorScheme.onSurface
    }

    val borderColor = when {
        isRevealedCorrect || isRevealedWrong -> Color.Transparent
        answerSubmitted -> Color.Transparent
        isSelected -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.5.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor,
            disabledContentColor = contentColor
        ),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = contentColor.copy(alpha = 0.18f)
        ) {
            Text(
                text = ('A' + index).toString(),
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Start
        )
        if (isRevealedCorrect) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null
            )
        } else if (isRevealedWrong) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun ExplanationCard(
    question: Question,
    isCorrect: Boolean
) {
    val accent = if (isCorrect) successColor() else errorColor()
    val container = accent.copy(
        alpha = if (isSystemInDarkTheme()) 0.18f else 0.10f
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isCorrect) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.Warning
                    },
                    contentDescription = null,
                    tint = accent
                )
                Text(
                    text = stringResource(
                        if (isCorrect) {
                            R.string.answer_correct_title
                        } else {
                            R.string.answer_incorrect_title
                        }
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = accent
                )
            }

            if (!isCorrect) {
                Text(
                    text = stringResource(
                        R.string.answer_correct_was,
                        question.options[question.correctAnswerIndex]
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = stringResource(R.string.explanation_title),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = accent
            )
            Text(
                text = question.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StatusBanner(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val warning = warningColor()
    val container = warning.copy(
        alpha = if (isSystemInDarkTheme()) 0.20f else 0.12f
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        border = BorderStroke(1.dp, warning.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = warning
            )
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = warning
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Resultados
// ---------------------------------------------------------------------------

@Composable
fun ResultSection(
    state: QuizUiState,
    onRestartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = if (state.isPassed) successColor() else errorColor()
    val accentContainer = accent.copy(
        alpha = if (isSystemInDarkTheme()) 0.20f else 0.12f
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentContainer,
                    modifier = Modifier.size(72.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (state.isPassed) {
                                Icons.Filled.Star
                            } else {
                                Icons.Filled.Warning
                            },
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                Text(
                    text = stringResource(R.string.results_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.results_student, state.studentName),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = state.formattedGrade,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.result_grade_out_of),
                        modifier = Modifier.padding(bottom = 10.dp),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = accentContainer,
                    border = BorderStroke(1.dp, accent.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = state.performanceStatus,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent,
                        textAlign = TextAlign.Center
                    )
                }

                Text(
                    text = pluralStringResource(
                        R.plurals.result_questions_evaluated,
                        state.questions.size,
                        state.questions.size
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.result_accuracy_label),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = state.formattedAccuracy,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                }

                LinearProgressIndicator(
                    progress = { state.accuracyPercentage / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp),
                    color = accent,
                    trackColor = accent.copy(alpha = 0.15f)
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ResultStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.result_score_label),
                        value = stringResource(
                            R.string.result_score_value,
                            state.score,
                            state.questions.size
                        )
                    )
                    ResultStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.result_status_label),
                        value = stringResource(
                            if (state.isPassed) {
                                R.string.result_status_approved
                            } else {
                                R.string.result_status_failed
                            }
                        ),
                        valueColor = accent
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = accentContainer),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.30f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector = if (state.isPassed) {
                        Icons.Filled.CheckCircle
                    } else {
                        Icons.Filled.Info
                    },
                    contentDescription = null,
                    tint = accent
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(
                            if (state.isPassed) {
                                R.string.result_approved_message
                            } else {
                                R.string.result_failed_message
                            }
                        ),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = accent
                    )
                    Text(
                        text = stringResource(R.string.result_footer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Button(
            onClick = onRestartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.action_restart_quiz),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun ResultStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

private val previewQuestion = Question(
    id = 1,
    category = QuizCategories.KOTLIN,
    text = "¿Qué palabra clave declara una variable de solo lectura en Kotlin?",
    options = listOf("var", "val", "lateinit", "const"),
    correctAnswerIndex = 1,
    explanation = "val declara una referencia inmutable: una vez asignado, su valor no puede reasignarse."
)

@Preview(showBackground = true, name = "Configuración")
@Composable
private fun ConfigSectionPreview() {
    EvaluadorAcademicoTheme {
        ConfigSection(
            studentName = "Ana Martínez",
            selectedCategory = QuizCategories.KOTLIN,
            questionCount = 5,
            isStrictOption = true,
            onNameChanged = {},
            onCategorySelected = {},
            onQuestionCountChanged = {},
            onStrictOptionToggled = {},
            onStartQuiz = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, name = "Pregunta respondida")
@Composable
private fun QuizSectionPreview() {
    EvaluadorAcademicoTheme {
        QuizSection(
            question = previewQuestion,
            currentQuestionIndex = 2,
            totalQuestions = 10,
            score = 2,
            selectedAnswerIndex = 0,
            answerSubmitted = true,
            isCurrentAnswerCorrect = false,
            isStrictOption = true,
            onAnswerSelected = {},
            onSubmitAnswer = {},
            onNextQuestion = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Preview(showBackground = true, name = "Resultados")
@Composable
private fun ResultSectionPreview() {
    EvaluadorAcademicoTheme {
        ResultSection(
            state = QuizUiState(
                studentName = "Ana Martínez",
                isQuizStarted = true,
                isQuizFinished = true,
                questions = List(10) { previewQuestion },
                score = 9,
                finalGrade = 9.0,
                accuracyPercentage = 90,
                performanceStatus = QuizViewModel.STATUS_EXCELLENT,
                formattedGrade = "9.0",
                formattedAccuracy = "90%",
                isPassed = true
            ),
            onRestartQuiz = {},
            modifier = Modifier.padding(20.dp)
        )
    }
}
