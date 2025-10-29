package com.example.clinicaveterinaria

import android.os.Build
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val mockReservas = remember {
        mutableStateOf(
            listOf(
                ReservaMock("1", "2025-11-10", "09:00", "Dr. Juan Pérez", "Control General", "Pendiente"),
                ReservaMock("2", "2025-11-12", "10:30", "Dra. Ana López", "Vacunación", "Pendiente"),
                ReservaMock("3", "2025-10-20", "09:30", "Dr. Juan Pérez", "Ecocardiograma", "Realizada"),
                ReservaMock("4", "2025-10-15", "11:00", "Dra. Ana López", "Consulta Urgencia", "Cancelada")
            )
        ).value.toMutableStateList()
    }

    NavHost(navController = navController, startDestination = "agendar") {

        composable("mis_reservas") {
            val onCancelarClick: (String) -> Unit = { idReservaACancelar ->
                val index = mockReservas.indexOfFirst { it.id == idReservaACancelar }
                if (index != -1) {
                    val reservaAntigua = mockReservas[index]
                    val reservaActualizada = reservaAntigua.copy(estado = "Cancelada")
                    mockReservas[index] = reservaActualizada
                }
            }

            MisReservasScreen(
                reservas = mockReservas.sortedByDescending { it.fecha + it.hora },
                onCancelarClick = onCancelarClick,
                onBackClick = { /* navController.popBackStack() */ }
            )
        }

        composable("profesionales") {

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
            ProfesionalesScreen(
                profesionales = mockListaProfesionales,
                onProfesionalClick = { rutDelProfesional ->
                    println("Clic en RUT: $rutDelProfesional")
                    navController.navigate("perfil_profesional")
                }
            )
        }

        composable("perfil_profesional") {

            val mockNombre = "Dr. Juan Pérez"
            val mockEspecialidad = "Cardiología Veterinaria"
            val mockBio = "Amante de los perros con 10 años de experiencia. Egresado de la Universidad de Chile."
            val mockServicios = listOf(
                "Consulta Cardiológica (30 min)",
                "Ecocardiograma (45 min)",
                "Vacunación (15 min)"
            )
            val mockFotoId = R.drawable.perfildoctor1

            PerfilProfesionalScreen(
                nombre = mockNombre,
                especialidad = mockEspecialidad,
                bio = mockBio,
                servicios = mockServicios,
                fotoResId = mockFotoId,
                onAgendarClick = { navController.navigate("agendar") },
                onBackClick = { navController.popBackStack() } // <-- Lógica para "volver"
            )
        }
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
                    mensajeError = "Todos los campos son obligatorios"
                } else if (fecha == "2024-01-01") {
                    mensajeError = "Esa fecha ya está ocupada"
                } else {
                    mensajeExito = "¡Reserva confirmada!"
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