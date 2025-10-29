package com.example.clinicaveterinaria.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
private val LightColorScheme = lightColorScheme(
    primary = TealPrincipal,
    onPrimary = TextoBlanco,
    background = TextoBlanco,
    surface = TextoBlanco,
    onBackground = TextoOscuro,
    onSurface = TextoOscuro,

    // Color para el fondo del TextField
    surfaceContainerLowest = TealClaroFondo,
    // Color para el borde del TextField
    outline = TealPrincipal
)

@Composable
fun ClinicaVeterinariaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}