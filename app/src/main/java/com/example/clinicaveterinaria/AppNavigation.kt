package com.example.clinicaveterinaria

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clinicaveterinaria.ui.screens.paciente.AgendarScreen
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilProfesionalScreen
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.ui.screens.paciente.Profesional
import com.example.clinicaveterinaria.ui.screens.paciente.ProfesionalesScreen
import com.example.clinicaveterinaria.ui.screens.paciente.MisReservasScreen
import com.example.clinicaveterinaria.ui.screens.paciente.ReservaMock

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // --- ¡AQUÍ ESTÁ LA LÓGICA FRONTEND! (RF-A14) ---
    // 1. Creamos una lista "mock" de reservas.
    // Usamos 'remember' y 'toMutableStateList' para que la UI
    // se actualice si "cancelamos" (borramos o editamos) un item.
    val mockReservas = remember {
        mutableStateOf(
            listOf(
                ReservaMock("1", "2025-11-10", "09:00", "Dr. Juan Pérez", "Control General", "Pendiente"),
                ReservaMock("2", "2025-11-12", "10:30", "Dra. Ana López", "Vacunación", "Pendiente"),
                ReservaMock("3", "2025-10-20", "09:30", "Dr. Juan Pérez", "Ecocardiograma", "Realizada"),
                ReservaMock("4", "2025-10-15", "11:00", "Dra. Ana López", "Consulta Urgencia", "Cancelada")
            )
        ).value.toMutableStateList() // <-- Importante para que la lista se pueda modificar
    }

    // 2. Cambia el inicio a "mis_reservas" para probarla
    NavHost(navController = navController, startDestination = "mis_reservas") {

        // --- RUTA PARA MOSTRAR "MIS RESERVAS" (NUEVA) ---
        composable("mis_reservas") {

            // Lógica de "cancelar" (RF-A11)
            val onCancelarClick: (String) -> Unit = { idReservaACancelar ->
                // Buscamos la reserva en la lista
                val index = mockReservas.indexOfFirst { it.id == idReservaACancelar }
                if (index != -1) {
                    val reservaAntigua = mockReservas[index]
                    // Creamos la reserva actualizada (estado "Cancelada")
                    val reservaActualizada = reservaAntigua.copy(estado = "Cancelada")
                    // Reemplazamos el item en la lista
                    mockReservas[index] = reservaActualizada
                }
            }

            MisReservasScreen(
                // Ordenamos la lista por fecha y hora para RF-A10
                reservas = mockReservas.sortedByDescending { it.fecha + it.hora },
                onCancelarClick = onCancelarClick,
                onBackClick = { /* navController.popBackStack() */ }
            )
        }

        composable("profesionales") {

            // 1. Lista "mock" (falsa) con tu modelo de datos
            val mockListaProfesionales = listOf(
                Profesional(
                    rut = "11.111.111-1",
                    nombres = "Juan",
                    apellidos = "Pérez",
                    genero = "Masculino",
                    fechaNacimiento = "1980-01-01",
                    especialidad = "Cardiología",
                    email = "juan.perez@vet.cl",
                    telefono = "+56911111111"
                ),
                Profesional(
                    rut = "22.222.222-2",
                    nombres = "Ana",
                    apellidos = "López",
                    genero = "Femenino",
                    fechaNacimiento = "1990-05-20",
                    especialidad = "Medicina General",
                    email = "ana.lopez@vet.cl",
                    telefono = "+56922222222"
                )
            )

            // 2. Llamamos a la pantalla "tonta" (Frontend)
            ProfesionalesScreen(
                profesionales = mockListaProfesionales,
                onProfesionalClick = { rutDelProfesional ->
                    // Navega al perfil. (El rut no lo usamos en la ruta,
                    // pero así es como lo pasarías al ViewModel)
                    println("Clic en RUT: $rutDelProfesional")
                    navController.navigate("perfil_profesional")
                }
            )
        }

        composable("perfil_profesional") {

            // 1. Datos "mock" (falsos)
            val mockNombre = "Dr. Juan Pérez"
            val mockEspecialidad = "Cardiología Veterinaria"
            val mockBio = "Amante de los perros con 10 años de experiencia. Egresado de la Universidad de Chile."
            val mockServicios = listOf(
                "Consulta Cardiológica (30 min)",
                "Ecocardiograma (45 min)",
                "Vacunación (15 min)"
            )
            // IMPORTANTE: Asegúrate de que 'perfildoctor1.png' esté en 'res/drawable'
            val mockFotoId = R.drawable.perfildoctor1

            // 2. Llamamos a la pantalla "tonta" (Frontend)
            PerfilProfesionalScreen(
                nombre = mockNombre,
                especialidad = mockEspecialidad,
                bio = mockBio,
                servicios = mockServicios,
                fotoResId = mockFotoId, // <-- Pasamos la foto
                onAgendarClick = { navController.navigate("agendar") },
                onBackClick = { navController.popBackStack() } // <-- Lógica para "volver"
            )
        }

        // --- RUTA "AGENDAR" (La dejamos para que funcione el botón) ---
        composable("agendar") {
            var fecha by remember { mutableStateOf("") }
            var hora by remember { mutableStateOf("") }
            var servicio by remember { mutableStateOf("") }
            var mensajeError by remember { mutableStateOf<String?>(null) }
            var mensajeExito by remember { mutableStateOf<String?>(null) }

            val onConfirmarClick: () -> Unit = {
                mensajeError = null
                mensajeExito = null
                if (fecha.isBlank() || hora.isBlank() || servicio.isBlank()) {
                    mensajeError = "Todos los campos son obligatorios (Prueba FE)"
                } else if (fecha == "2024-01-01") {
                    mensajeError = "Esa fecha ya está ocupada (Prueba FE)"
                } else {
                    mensajeExito = "¡Reserva confirmada! (Prueba FE)"
                }
            }

            AgendarScreen(
                fecha = fecha,
                onFechaChange = { fecha = it },
                hora = hora,
                onHoraChange = { hora = it },
                servicio = servicio,
                onServicioChange = { servicio = it },
                mensajeError = mensajeError,
                mensajeExito = mensajeExito,
                onConfirmarClick = onConfirmarClick
            )
        }
    }
}