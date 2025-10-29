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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.ui.screens.paciente.AgendarScreen
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilProfesionalScreen
import com.example.clinicaveterinaria.ui.screens.paciente.ProfesionalesScreen

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
            composable("agendar/{profesionalId}") { backStackEntry ->
                // (Ignoramos el profesionalId por ahora, ya que es solo frontend)

                // --- ¡AQUÍ ESTÁ LA MAGIA! ---
                // 1. Creamos el "estado" aquí mismo, en lugar de en un ViewModel.
                //    Usamos 'remember' para que los valores no se borren al escribir.
                var fecha by remember { mutableStateOf("") }
                var hora by remember { mutableStateOf("") }
                var servicio by remember { mutableStateOf("") }
                var mensajeError by remember { mutableStateOf<String?>(null) }
                var mensajeExito by remember { mutableStateOf<String?>(null) }

                // 2. Definimos la lógica del botón "Confirmar" aquí mismo.
                //    Esta es una simulación de la lógica (solo frontend).
                val onConfirmarClick: () -> Unit = {
                    mensajeError = null
                    mensajeExito = null

                    // Simulación de validación (RF-A7)
                    if (fecha.isBlank() || hora.isBlank() || servicio.isBlank()) {
                        mensajeError = "Todos los campos son obligatorios (Prueba FE)"
                    }
                    // Simulación de regla de negocio (RF-A8)
                    else if (fecha == "2024-01-01") {
                        mensajeError = "Esa fecha ya está ocupada (Prueba FE)"
                    }
                    // Simulación de éxito (RF-A9)
                    else {
                        mensajeExito = "¡Reserva confirmada! (Prueba FE)"
                    }
                }

                // 3. Llamamos a tu pantalla "tonta" (Frontend) y le pasamos todo.
                AgendarScreen(
                    fecha = fecha,
                    onFechaChange = { fecha = it }, // Cuando la UI avisa, actualizamos el estado
                    hora = hora,
                    onHoraChange = { hora = it },
                    servicio = servicio,
                    onServicioChange = { servicio = it },
                    mensajeError = mensajeError,
                    mensajeExito = mensajeExito,
                    onConfirmarClick = onConfirmarClick // Le pasamos la lógica del botón
                )
            }
        }
    }
}

@Composable
fun MisReservasScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}