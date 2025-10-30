package com.example.clinicaveterinaria

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.ui.admin.CrearProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.ListaProfesionalesScreen
import com.example.clinicaveterinaria.ui.admin.ModificarProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.PerfilAdministradorScreen
import com.example.clinicaveterinaria.ui.login.LoginScreen
import com.example.clinicaveterinaria.ui.profesional.HomeProfesionalScreen
import com.example.clinicaveterinaria.ui.theme.ClinicaVeterinariaTheme
import com.example.clinicaveterinaria.ui.cliente.*
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilClienteScreen
import com.example.clinicaveterinaria.ui.cliente.ReservaMock
import com.example.clinicaveterinaria.ui.profesional.PerfilProfesionalScreen as PerfilProfesionalDoctorScreen
import com.example.clinicaveterinaria.ui.cliente.PerfilProfesionalScreen as PerfilProfesionalClienteScreen

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Repository.init(this)
        enableEdgeToEdge()

        setContent {
            ClinicaVeterinariaTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val context = LocalContext.current

                Repository.init(context)

                // 🔹 Estado reactivo de sesión
                var sesionActiva by remember { mutableStateOf(SesionManager.haySesionActiva(context)) }
                var tipoSesion by remember { mutableStateOf(SesionManager.obtenerTipo(context)) }

                // 🔹 Observa cambios de ruta: actualiza si cerraste sesión
                LaunchedEffect(currentRoute) {
                    sesionActiva = SesionManager.haySesionActiva(context)
                    tipoSesion = SesionManager.obtenerTipo(context)
                }

                Scaffold(
                    bottomBar = {
                        if (sesionActiva) {
                            NavigationBar {
                                when (tipoSesion) {
                                    "admin" -> {
                                        NavigationBarItem(
                                            selected = currentRoute == "adminHome",
                                            onClick = { navController.navigate("adminHome") },
                                            label = { Text("Profesionales") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute == "perfilAdmin",
                                            onClick = { navController.navigate("perfilAdmin") },
                                            label = { Text("Perfil") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                    }
                                    "profesional" -> {
                                        NavigationBarItem(
                                            selected = currentRoute == "profesionalHome",
                                            onClick = { navController.navigate("profesionalHome") },
                                            label = { Text("Inicio") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )

                                        NavigationBarItem(
                                            selected = currentRoute == "perfilProfesional",
                                            onClick = { navController.navigate("perfilProfesional") },
                                            label = { Text("Perfil") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                    }
                                    "cliente" -> {
                                        NavigationBarItem(
                                            selected = currentRoute == "clienteProfesionales",
                                            onClick = { navController.navigate("clienteProfesionales") },
                                            label = { Text("Inicio") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute == "clienteMisReservas",
                                            onClick = { navController.navigate("clienteMisReservas") },
                                            label = { Text("Reservas") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                        NavigationBarItem(
                                            selected = currentRoute == "clientePerfil",
                                            onClick = { navController.navigate("clientePerfil") },
                                            label = { Text("Perfil") },
                                            icon = { Icon(Icons.Filled.Home, null) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                ) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = if (SesionManager.haySesionActiva(context)) {
                            when (SesionManager.obtenerTipo(context)) {
                                "admin" -> "adminHome"
                                "profesional" -> "profesionalHome"
                                "cliente" -> "clienteProfesionales"
                                else -> "login"
                            }
                        } else "login",
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // --- Login ---
                        composable("login") {
                            LoginScreen(navController, context)
                        }

                        // --- Admin ---
                        composable("adminHome") { ListaProfesionalesScreen(navController) }
                        composable("perfilAdmin") { PerfilAdministradorScreen(navController) }
                        composable("crearProfesional") {
                            CrearProfesionalScreen(
                                onGuardar = { prof ->
                                    Repository.agregarProfesional(prof)
                                    navController.popBackStack()
                                },
                                onCancelar = { navController.popBackStack() }
                            )
                        }
                        composable(
                            "clienteAgregarMascota/{rutCliente}",
                            arguments = listOf(navArgument("rutCliente") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutCliente = backStackEntry.arguments?.getString("rutCliente") ?: ""

                            CrearMascotaRoute(
                                nav = navController,
                                clienteRut = rutCliente,
                                onGuardarMascota = { form ->
                                    // SOLO FRONT por ahora.
                                    // Aquí luego guardarías en BD; de momento, puedes mostrar un snackbar o navegar:
                                    // navController.popBackStack()
                                }
                            )
                        }

                        composable(
                            "modificarProfesional/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rut = backStackEntry.arguments?.getString("rut") ?: ""
                            val prof = Repository.obtenerProfesional(rut)

                            if (prof != null) {
                                ModificarProfesionalScreen(
                                    profesional = prof,
                                    onGuardar = { actualizado ->
                                        Repository.actualizarProfesional(actualizado)
                                        navController.popBackStack()
                                    },
                                    onCancelar = { navController.popBackStack() },
                                    onEliminar = { p ->
                                        Repository.eliminarProfesional(p.rut)
                                        navController.popBackStack()
                                    }
                                )
                            } else {
                                Text(
                                    text = "Profesional no encontrado",
                                    color = Color.Red
                                )
                            }
                        }

                        // --- Profesional ---
                        composable("profesionalHome") { HomeProfesionalScreen() }
                        composable("perfilProfesional") { PerfilProfesionalDoctorScreen(navController) }

                        // --- Cliente ---
                        composable("clienteProfesionales") {
                            val profesionales = remember { ClienteMockData.profesionales }
                            ProfesionalesScreen(
                                profesionales = profesionales,
                                onProfesionalClick = { rut ->
                                    navController.navigate("clientePerfilProfesional/$rut")
                                }
                            )
                        }

                        composable(
                            "clientePerfilProfesional/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rut = backStackEntry.arguments?.getString("rut") ?: ""
                            val p = remember { ClienteMockData.buscarPorRut(rut) }

                            if (p != null) {
                                val nombre = "${p.nombres} ${p.apellidos}"
                                val especialidad = p.especialidad
                                val bio = ClienteMockData.bioDe(p)
                                val fotoRes = ClienteMockData.fotoDe(p)
                                val servicios = ClienteMockData.serviciosDe(p)

                                PerfilProfesionalClienteScreen(
                                    nombre = nombre,
                                    especialidad = especialidad,
                                    bio = bio,
                                    servicios = servicios,
                                    fotoResId = fotoRes,
                                    onAgendarClick = {
                                        navController.navigate("clienteAgendar/${p.rut}")
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            } else {
                                Text("Profesional no encontrado", color = Color.Red)
                            }
                        }

                        composable(
                            "clienteAgendar/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rut = backStackEntry.arguments?.getString("rut") ?: ""
                            var fecha by rememberSaveable { mutableStateOf("") }
                            var hora by rememberSaveable { mutableStateOf("") }
                            var servicio by rememberSaveable { mutableStateOf("") }
                            var ok by remember { mutableStateOf<String?>(null) }
                            var error by remember { mutableStateOf<String?>(null) }

                            AgendarScreen(
                                fecha = fecha,
                                onFechaChange = { fecha = it },
                                hora = hora,
                                onHoraChange = { hora = it },
                                servicio = servicio,
                                onServicioChange = { servicio = it },
                                mensajeError = error,
                                mensajeExito = ok,
                                onConfirmarClick = {
                                    if (fecha.isBlank() || hora.isBlank() || servicio.isBlank()) {
                                        ok = null
                                        error = "Completa fecha, hora y servicio"
                                    } else {
                                        error = null
                                        ok = "Reserva creada ✔"
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                        // --- Cliente: crear cuenta ---
                        composable("crearCliente") {
                            CrearClienteRoute(nav = navController)
                        }
                        composable(
                            "clienteAgregarMascota/{rutCliente}",
                            arguments = listOf(navArgument("rutCliente") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutCliente = backStackEntry.arguments?.getString("rutCliente") ?: ""

                            CrearMascotaRoute(
                                nav = navController,
                                clienteRut = rutCliente
                            )
                        }

                        composable("clienteMisReservas") {
                            val reservas = remember {
                                mutableStateListOf<ReservaMock>().apply {
                                    addAll(
                                        listOf(
                                            ReservaMock(
                                                id = "R-001",
                                                fecha = "2025-10-31",
                                                hora = "10:30",
                                                profesional = "Dra. Ana Pérez",
                                                servicio = "Consulta General",
                                                estado = "Pendiente"
                                            ),
                                            ReservaMock(
                                                id = "R-002",
                                                fecha = "2025-10-28",
                                                hora = "16:00",
                                                profesional = "Dr. José Soto",
                                                servicio = "Vacunación",
                                                estado = "Realizada"
                                            )
                                        )
                                    )
                                }
                            }

                            // ✅ Deja SOLO una llamada (esta)
                            MisReservasScreen(
                                reservas = reservas,
                                onCancelarClick = { reserva: ReservaMock ->
                                    reservas.remove(reserva)
                                } as (String) -> Unit,
                                onBackClick = { navController.popBackStack() }
                            )
                        }



                        composable("clientePerfil") {
                            PerfilClienteScreen(
                                mockNombre = "Carlos Cliente",
                                mockEmail = "cliente@correo.cl",
                                mockTelefono = "+56 9 1234 5678",
                                onChangePasswordClick = { /* sólo front */ },
                                onLogoutClick = {
                                    SesionManager.cerrarSesion(context)
                                    navController.navigate("login") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("clienteAgregarMascota") {
                            var nombre by rememberSaveable { mutableStateOf("") }
                            var especie by rememberSaveable { mutableStateOf("") }
                            var raza by rememberSaveable { mutableStateOf("") }
                            var fechaNac by rememberSaveable { mutableStateOf("") }

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
                                    // Solo front por ahora
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}