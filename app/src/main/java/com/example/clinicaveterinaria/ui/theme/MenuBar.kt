package com.example.clinicaveterinaria.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import com.example.clinicaveterinaria.ui.screens.paciente.MisReservasScreen
import com.example.clinicaveterinaria.ui.screens.paciente.ProfesionalesScreen

@Composable

@Composable
fun MenuBar() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Item 1: Profesionales (RF-A2)
                NavigationBarItem(
                    selected = navController.currentBackStackEntry?.destination?.route == "profesionales",
                    onClick = { navController.navigate("profesionales") },
                    label = { Text("Profesionales") },
                    icon = { Icon(Icons.Filled.Person, "Profesionales") }
                )

                // Item 2: Mis Reservas (RF-A10)
                NavigationBarItem(
                    selected = navController.currentBackStackEntry?.destination?.route == "mis_reservas",
                    onClick = { navController.navigate("mis_reservas") },
                    label = { Text("Mis Reservas") },
                    icon = { Icon(Icons.Filled.List, "Mis Reservas") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "profesionales",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Pantallas del Menú
            composable("profesionales") {
                ProfesionalesScreen(navController = navController)
            }
            composable("mis_reservas") {
                MisReservasScreen(navController = navController)
            }

            // Pantallas *sin* Menú (RF-A12)
            composable("perfil_profesional/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: 0
                PerfilProfesionalScreen(navController = navController, profesionalId = id)
            }
            composable("agendar/{profesionalId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("profesionalId")?.toIntOrNull() ?: 0
                AgendarScreen(navController = navController, profesionalId = id)
            }
        }
    }
}