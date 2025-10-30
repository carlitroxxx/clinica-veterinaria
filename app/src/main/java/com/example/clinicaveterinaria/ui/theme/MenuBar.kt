package com.example.clinicaveterinaria.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MenuBar() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            NavigationBar {
                //Profesionales
                NavigationBarItem(
                    selected = navController.currentBackStackEntry?.destination?.route == "profesionales",
                    onClick = { navController.navigate("profesionales") },
                    label = { Text("Profesionales") },
                    icon = { Icon(Icons.Filled.Person, "Profesionales") }
                )

                //Mis Reservas
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
                    }
                    else if (fecha == "2024-01-01") {
                        mensajeError = "Esa fecha ya está ocupada (Prueba FE)"
                    }
                    else {
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
                    onConfirmarClick = onConfirmarClick,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun MisReservasScreen(navController: NavHostController) {
    TODO("Not yet implemented")
}