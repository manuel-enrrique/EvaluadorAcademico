package com.example.evaluadoracademico.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Paleta clara
// ---------------------------------------------------------------------------
val PrimaryLight = Color(0xFF1F4E8C)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFD6E3FF)
val OnPrimaryContainerLight = Color(0xFF001B3D)
val SecondaryLight = Color(0xFF006A60)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFF9EF2E4)
val OnSecondaryContainerLight = Color(0xFF00201C)
val TertiaryLight = Color(0xFF8A5100)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFDDB3)
val OnTertiaryContainerLight = Color(0xFF2C1700)
val BackgroundLight = Color(0xFFF8F9FC)
val OnBackgroundLight = Color(0xFF191C20)
val SurfaceLight = Color(0xFFF8F9FC)
val OnSurfaceLight = Color(0xFF191C20)
val SurfaceVariantLight = Color(0xFFDFE2EB)
val OnSurfaceVariantLight = Color(0xFF43474E)
val OutlineLight = Color(0xFF73777F)
val ErrorLight = Color(0xFFBA1A1A)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFFDAD6)
val OnErrorContainerLight = Color(0xFF410002)

// ---------------------------------------------------------------------------
// Paleta oscura
// ---------------------------------------------------------------------------
val PrimaryDark = Color(0xFFA9C7FF)
val OnPrimaryDark = Color(0xFF00325B)
val PrimaryContainerDark = Color(0xFF00497F)
val OnPrimaryContainerDark = Color(0xFFD6E3FF)
val SecondaryDark = Color(0xFF82D5C8)
val OnSecondaryDark = Color(0xFF003731)
val SecondaryContainerDark = Color(0xFF005048)
val OnSecondaryContainerDark = Color(0xFF9EF2E4)
val TertiaryDark = Color(0xFFFFB960)
val OnTertiaryDark = Color(0xFF4A2800)
val TertiaryContainerDark = Color(0xFF6A3C00)
val OnTertiaryContainerDark = Color(0xFFFFDDB3)
val BackgroundDark = Color(0xFF111418)
val OnBackgroundDark = Color(0xFFE1E2E8)
val SurfaceDark = Color(0xFF111418)
val OnSurfaceDark = Color(0xFFE1E2E8)
val SurfaceVariantDark = Color(0xFF43474E)
val OnSurfaceVariantDark = Color(0xFFC3C7CF)
val OutlineDark = Color(0xFF8D9199)
val ErrorDark = Color(0xFFFFB4AB)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000A)
val OnErrorContainerDark = Color(0xFFFFDAD6)

// ---------------------------------------------------------------------------
// Colores semánticos de la evaluación
// ---------------------------------------------------------------------------
val SuccessGreen = Color(0xFF2E7D32)
val SuccessGreenContrast = Color(0xFF81C784)
val ErrorRed = Color(0xFFC62828)
val ErrorRedContrast = Color(0xFFEF9A9A)
val WarningAmber = Color(0xFFF9A825)
val WarningAmberContrast = Color(0xFFFFD54F)
val WarningAmberDark = Color(0xFF8A5300)

@Composable
fun successColor(): Color =
    if (isSystemInDarkTheme()) SuccessGreenContrast else SuccessGreen

@Composable
fun errorColor(): Color =
    if (isSystemInDarkTheme()) ErrorRedContrast else ErrorRed

@Composable
fun warningColor(): Color =
    if (isSystemInDarkTheme()) WarningAmberContrast else WarningAmberDark
