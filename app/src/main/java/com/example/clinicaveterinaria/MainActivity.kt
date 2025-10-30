package com.example.clinicaveterinaria

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.ui.admin.CrearProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.ListaProfesionalesScreen
import com.example.clinicaveterinaria.ui.admin.ModificarProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.PerfilAdministradorScreen
import com.example.clinicaveterinaria.ui.login.LoginScreen
import com.example.clinicaveterinaria.ui.profesional.HomeProfesionalScreen
import com.example.clinicaveterinaria.ui.profesional.PerfilProfesionalScreen as PerfilProfesionalDoctorScreen
import com.example.clinicaveterinaria.ui.theme.ClinicaVeterinariaTheme

// Cliente (UI)
import com.example.clinicaveterinaria.ui.cliente.ClienteMockData
import com.example.clinicaveterinaria.ui.cliente.ProfesionalesScreen
import com.example.clinicaveterinaria.ui.cliente.PerfilProfesionalScreen as PerfilProfesionalClienteScreen
import com.example.clinicaveterinaria.ui.cliente.MisReservasScreen
import com.example.clinicaveterinaria.ui.cliente.ReservaMock
import com.example.clinicaveterinaria.ui.cliente.AgendarScreen
import com.example.clinicaveterinaria.ui.cliente.CrearClienteRoute
import com.example.clinicaveterinaria.ui.cliente.CrearMascotaRoute

// Perfil cliente (lee sesión + BD)
import com.example.clinicaveterinaria.ui.screens.paciente.PerfilClienteScreen

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

                // Estado de sesión reactivo
                var sesionActiva by remember { mutableStateOf(SesionManager.haySesionActiva(context)) }
                var tipoSesion by remember { mutableStateOf(SesionManager.obtenerTipo(context)) }

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

                    val startDestination = remember {
                        if (SesionManager.haySesionActiva(context)) {
                            when (SesionManager.obtenerTipo(context)) {
                                "admin" -> "adminHome"
                                "profesional" -> "profesionalHome"
                                "cliente" -> "clienteProfesionales"
                                else -> "login"
                            }
                        } else "login"
                    }

                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {

                        // ---------- LOGIN ----------
                        composable("login") {
                            LoginScreen(navController, context)
                        }
                        // Ruta para registro de cliente desde el login
                        composable("crearCliente") {
                            CrearClienteRoute(nav = navController)
                        }

                        // ---------- ADMIN ----------
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
                                Text("Profesional no encontrado", color = Color.Red)
                            }
                        }

                        // ---------- PROFESIONAL ----------
                        composable("profesionalHome") { HomeProfesionalScreen() }
                        composable("perfilProfesional") { PerfilProfesionalDoctorScreen(navController) }

                        // ---------- CLIENTE ----------
                        // Lista de profesionales (si prefieres BD, reemplaza por Repository.obtenerProfesionales())
                        // en MainActivity.kt, dentro del NavHost { ... }
                        // en MainActivity.kt, dentro del NavHost { ... }
                        composable("clienteProfesionales") {
                            // 1) Estado UI
                            var uiProfesionales by remember { mutableStateOf<List<com.example.clinicaveterinaria.ui.cliente.Profesional>>(emptyList()) }

                            // 2) Cargar desde BD una sola vez
                            LaunchedEffect(Unit) {
                                // Ojo: este Profesional de abajo es el de tu capa de datos/modelo,
                                // puede estar en package com.example.clinicaveterinaria.model o .data
                                val desdeDb: List<com.example.clinicaveterinaria.model.Profesional> =
                                    try { Repository.obtenerProfesionales() } catch (_: Exception) { emptyList() }

                                // 3) Mapear al data class que espera tu ProfesionalesScreen (ui.cliente.Profesional)
                                uiProfesionales = desdeDb.map { p ->
                                    com.example.clinicaveterinaria.ui.cliente.Profesional(
                                        rut = p.rut,
                                        nombres = p.nombres,
                                        apellidos = p.apellidos,
                                        // Normaliza género para elegir bien el drawable en tu screen
                                        genero = when (p.genero.trim().lowercase()) {
                                            "f", "femenino" -> "Femenino"
                                            "m", "masculino" -> "Masculino"
                                            else -> p.genero
                                        },
                                        fechaNacimiento = p.fechaNacimiento,
                                        especialidad = p.especialidad,
                                        email = p.email,
                                        telefono = p.telefono
                                    )
                                }
                            }

                            // 4) Render: si la lista viene vacía, igual se ve el logo (tu screen ya lo muestra)
                            com.example.clinicaveterinaria.ui.cliente.ProfesionalesScreen(
                                profesionales = uiProfesionales,
                                onProfesionalClick = { rut ->
                                    // Por si el rut lleva puntos/guiones
                                    val rutSeg = android.net.Uri.encode(rut)
                                    navController.navigate("clientePerfilProfesional/$rutSeg")
                                }
                            )
                        }



                        // Perfil del profesional (vista cliente)
                        composable(
                            "clientePerfilProfesional/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rut = backStackEntry.arguments?.getString("rut") ?: ""

                            // 1) Traemos el profesional desde la BD
                            val profesional = remember(rut) { Repository.obtenerProfesional(rut) }

                            // 2) Derivados para la UI
                            val nombre = profesional?.let { "${it.nombres} ${it.apellidos}" } ?: "Profesional"
                            val especialidad = profesional?.especialidad ?: "Especialidad"
                            val generoNormalizado = profesional?.genero?.trim()?.lowercase() ?: ""
                            // Usa drawables que EXISTAN; si no tienes perfildoctor(a), deja logo
                            val fotoRes = when (generoNormalizado) {
                                "f", "femenino" -> R.drawable.perfildoctora1
                                "m", "masculino" -> R.drawable.perfildoctor1
                                else -> R.drawable.logo
                            }

                            // 3) (Opcional) Servicios del profesional: si no tienes tabla/relación, muestra un set básico
                            val servicios = remember(profesional?.especialidad) {
                                buildList {
                                    // si quieres algo más real, reemplaza por Repository.obtenerServiciosDelProfesional(rut)
                                    add("Consulta ${profesional?.especialidad ?: "General"}")
                                    add("Control / Evaluación")
                                }
                            }

                            // 4) Render de la pantalla
                            if (profesional != null) {
                                // Tu versión de la pantalla cliente
                                com.example.clinicaveterinaria.ui.cliente.PerfilProfesionalScreen(
                                    nombre = nombre,
                                    especialidad = especialidad,
                                    bio = "",                          // si ya no usas bio, deja vacío
                                    servicios = servicios,
                                    fotoResId = fotoRes,
                                    onAgendarClick = {
                                        val rutSeg = android.net.Uri.encode(rut)
                                        navController.navigate("clienteAgendar/$rutSeg")
                                    },
                                    onBackClick = { navController.popBackStack() }
                                )
                            } else {
                                // Fallback simple si el rut no existe
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("Profesional no encontrado", color = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.height(12.dp))
                                    OutlinedButton(onClick = { navController.popBackStack() }) {
                                        Text("Volver")
                                    }
                                }
                            }
                        }


                        // Agendar (mínimo funcional; guarda en BD)
                        composable(
                            "clienteAgendar/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutProfesional = backStackEntry.arguments?.getString("rut") ?: ""
                            val ctx = LocalContext.current

                            // Datos cliente desde sesión
                            val emailSesion = SesionManager.obtenerEmail(ctx)
                            val clienteRut = remember(emailSesion) { emailSesion?.let { Repository.obtenerClientePorEmail(it)?.rut } }

                            // Datos profesional (con fallback para evitar drawables faltantes)
                            val prof = remember(rutProfesional) { Repository.obtenerProfesional(rutProfesional) }
                            val nombreProf = prof?.let { "${it.nombres} ${it.apellidos}" } ?: "Profesional"
                            val especialidad = prof?.especialidad ?: ""
                            // 👇 Usa un drawable que EXISTE seguro (logo). Si luego agregas imágenes de perfil, cámbialo.
                            val fotoResId = R.drawable.logo

                            var fecha by rememberSaveable { mutableStateOf("") }
                            var hora by rememberSaveable { mutableStateOf("") }
                            var servicio by rememberSaveable { mutableStateOf("") }
                            var ok by remember { mutableStateOf<String?>(null) }
                            var error by remember { mutableStateOf<String?>(null) }

                            // Horarios libres desde repo, con try/catch para que NUNCA crashee
                            var horarios by remember { mutableStateOf<List<String>>(emptyList()) }
                            LaunchedEffect(fecha, rutProfesional) {
                                horarios = try {
                                    if (fecha.isBlank()) emptyList()
                                    else Repository.horasDisponibles(rutProfesional, fecha)
                                } catch (e: Exception) {
                                    // Si algo falla (ej. parse de fecha), no crasheamos
                                    emptyList()
                                }
                                if (hora.isNotBlank() && hora !in horarios) hora = ""
                            }

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
                                        ok = null; error = "Completa fecha, hora y servicio"
                                    } else if (clienteRut.isNullOrBlank()) {
                                        ok = null; error = "No se pudo identificar al cliente (sesión)"
                                    } else {
                                        val res = Repository.agregarReserva(clienteRut, rutProfesional, fecha, hora, servicio)
                                        if (res.ok) {
                                            error = null; ok = "Reserva creada ✔"
                                            navController.navigate("clienteMisReservas")
                                        } else {
                                            ok = null; error = res.mensaje ?: "Error al crear la reserva"
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() },
                                profesionalNombre = nombreProf,
                                profesionalEspecialidad = especialidad,
                                profesionalFotoResId = fotoResId,
                                horariosDisponibles = horarios
                            )
                        }



                        // Mis Reservas (usa tu MisReservasScreen)
                        composable("clienteMisReservas") {
                            val ctx = LocalContext.current
                            val emailSesion = SesionManager.obtenerEmail(ctx)
                            val rutCliente = remember(emailSesion) {
                                emailSesion?.let { Repository.obtenerClientePorEmail(it)?.rut }
                            }

                            var reservasRepo by remember { mutableStateOf<List<Reserva>>(emptyList()) }

                            LaunchedEffect(rutCliente) {
                                rutCliente?.let { reservasRepo = Repository.obtenerReservasCliente(it) }
                            }

                            val reservasUi = remember(reservasRepo) {
                                reservasRepo.map { r ->
                                    val prof = Repository.obtenerProfesional(r.profesionalRut)
                                    val nombreProf = prof?.let { "${it.nombres} ${it.apellidos}" } ?: r.profesionalRut
                                    ReservaMock(
                                        id = r.id.toString(),
                                        fecha = r.fecha,
                                        hora = r.hora,
                                        profesional = nombreProf,
                                        servicio = r.servicio,
                                        estado = r.estado
                                    )
                                }
                            }

                            MisReservasScreen(
                                reservas = reservasUi,
                                onCancelarClick = { idStr ->
                                    val id = idStr.toLongOrNull()
                                    if (id != null && rutCliente != null) {
                                        val res = Repository.cancelarReserva(id)
                                        if (res.ok) reservasRepo = Repository.obtenerReservasCliente(rutCliente)
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Perfil de cliente (lee sesión + BD)
                        composable("clientePerfil") {
                            val ctx = LocalContext.current
                            val emailSesion = SesionManager.obtenerEmail(ctx)
                            val cliente = remember(emailSesion) {
                                emailSesion?.let { Repository.obtenerClientePorEmail(it) }
                            }

                            if (cliente != null) {
                                com.example.clinicaveterinaria.ui.screens.paciente.PerfilClienteScreen(
                                    mockNombre = "${cliente.nombres} ${cliente.apellidos}",
                                    mockEmail = cliente.email,
                                    mockTelefono = cliente.telefono,
                                    onChangePasswordClick = { /* TODO */ },
                                    onLogoutClick = {
                                        SesionManager.cerrarSesion(ctx)
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            } else {
                                // Fallback simple si no hay sesión o no se encontró el cliente
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("No se pudo cargar el perfil", color = MaterialTheme.colorScheme.error)
                                    Spacer(Modifier.height(12.dp))
                                    OutlinedButton(onClick = {
                                        SesionManager.cerrarSesion(ctx)
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }) { Text("Volver a iniciar sesión") }
                                }
                            }
                        }


                        // Agregar mascota (recibe rutCliente)
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
                    }
                }
            }
        }
    }
}
