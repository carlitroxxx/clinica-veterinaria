package com.example.clinicaveterinaria

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clinicaveterinaria.ui.screens.paciente.AgendarScreen
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilProfesionalScreen
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.ui.screens.paciente.AgregarMascotaScreen
import com.example.clinicaveterinaria.ui.screens.paciente.Profesional
import com.example.clinicaveterinaria.ui.screens.paciente.ProfesionalesScreen
import com.example.clinicaveterinaria.ui.screens.paciente.MisReservasScreen
import com.example.clinicaveterinaria.ui.screens.paciente.ReservaMock
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilClienteScreen

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

    NavHost(navController = navController, startDestination = "agregar_mascota") {

        composable("agregar_mascota") {

            var nombre by remember { mutableStateOf("") }
            var especie by remember { mutableStateOf("") }
            var raza by remember { mutableStateOf("") }
            var fechaNac by remember { mutableStateOf("") }

            AgregarMascotaScreen(
                nombre = nombre,
                onNombreChange = { nombre = it },
                especie = especie,
                onEspecieChange = { especie = it },
                raza = raza,
                onRazaChange = { raza = it },
                fechaNacimiento = fechaNac,
                onFechaNacimientoChange = { fechaNac = it },
                onGuardarClick = {
                    println("Guardando mascota: $nombre, $especie, $raza, $fechaNac")
                    navController.popBackStack()
                },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable("perfil_cliente") {
            PerfilClienteScreen(
                mockNombre = "Martín Salazar",
                mockEmail = "martin.salazar@cliente.cl",
                mockTelefono = "+56 9 1234 5678",
                onChangePasswordClick = {
                    println("Clic en Cambiar Contraseña")
                },
                onLogoutClick = {
                    println("Clic en Cerrar Sesión")
                }
            )
        }
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
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("profesionales") {
            ProfesionalesScreen(
                profesionales = mockListaProfesionales,
                onProfesionalClick = { rutDelProfesional ->
                    navController.navigate("perfil_profesional/$rutDelProfesional")
                }
            )
        }

        composable(
            route = "perfil_profesional/{rut}",
            arguments = listOf(navArgument("rut") { type = NavType.StringType })
        ) { backStackEntry ->
            val rut = backStackEntry.arguments?.getString("rut")
            val profesional = mockListaProfesionales.find { it.rut == rut } ?: mockListaProfesionales.first()
            val fotoId = when (profesional.genero) {
                "Femenino" -> R.drawable.perfildoctora1
                "Masculino" -> R.drawable.perfildoctor1
                else -> R.drawable.logo
            }

            PerfilProfesionalScreen(
                nombre = "${profesional.nombres} ${profesional.apellidos}",
                especialidad = profesional.especialidad,
                bio = "Biografía de ${profesional.nombres} (Email: ${profesional.email})",
                servicios = listOf(
                    "Consulta ${profesional.especialidad}",
                    "Vacunación",
                    "Control"
                ),
                fotoResId = fotoId,
                onAgendarClick = { navController.navigate("agendar") },
                onBackClick = { navController.popBackStack() }
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
                } else {
                    mensajeExito = "¡Reserva confirmada!"
                    mockReservas.add(
                        0,
                        ReservaMock(
                            id = (mockReservas.size + 1).toString(),
                            fecha = fecha,
                            hora = hora,
                            profesional = "Dr. Juan Pérez",
                            servicio = servicio,
                            estado = "Pendiente"
                        )
                    )
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
                onConfirmarClick = onConfirmarClick,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}