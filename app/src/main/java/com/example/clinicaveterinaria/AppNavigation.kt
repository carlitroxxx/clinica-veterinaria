package com.example.clinicaveterinaria

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clinicaveterinaria.ui.theme.MenuBar
import com.example.clinicaveterinaria.ui.screens.login.LoginScreen
import com.example.clinicaveterinaria.ui.screens.paciente.AgendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "agendar") {
        // Ruta para el resto de la app (que contiene su propia barra de menú)
        composable("agendar") { AgendarScreen(navController, 1) }
    }
}

