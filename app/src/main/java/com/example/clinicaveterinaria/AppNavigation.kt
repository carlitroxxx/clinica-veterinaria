package com.example.clinicaveterinaria

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // <-- Importante
import androidx.compose.runtime.mutableStateOf // <-- Importante
import androidx.compose.runtime.remember // <-- Importante
import androidx.compose.runtime.setValue // <-- Importante
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// No necesitas MenuBar ni LoginScreen para esta prueba
import com.example.clinicaveterinaria.ui.screens.paciente.AgendarScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "agendar") {

        // Esta es tu ruta "agendar" que ahora sí funcionará
        composable("agendar") {

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