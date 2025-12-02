package com.example.clinicaveterinaria

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional as ProfesionalRemote
import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.ui.admin.CrearProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.ListaProfesionalesScreen
import com.example.clinicaveterinaria.ui.admin.ModificarProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.PerfilAdministradorScreen
import com.example.clinicaveterinaria.ui.cliente.AgendarScreen
import com.example.clinicaveterinaria.ui.cliente.CrearClienteRoute
import com.example.clinicaveterinaria.ui.cliente.CrearMascotaRoute
import com.example.clinicaveterinaria.ui.cliente.MisReservasScreen
import com.example.clinicaveterinaria.ui.cliente.PerfilClienteScreen
import com.example.clinicaveterinaria.ui.cliente.ProfesionalesScreen
import com.example.clinicaveterinaria.ui.cliente.ProfesionalUi
import com.example.clinicaveterinaria.ui.cliente.ReservaMock
import com.example.clinicaveterinaria.ui.login.LoginScreen
import com.example.clinicaveterinaria.ui.profesional.HomeProfesionalScreen
import com.example.clinicaveterinaria.ui.profesional.PerfilProfesionalScreen as PerfilProfesionalDoctorScreen
import com.example.clinicaveterinaria.ui.theme.ClinicaVeterinariaTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ClinicaVeterinariaTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route
                val context = LocalContext.current
                val scope = rememberCoroutineScope()

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

                        // Registro de cliente desde el login
                        composable("crearCliente") {
                            CrearClienteRoute(nav = navController)
                        }

                        // ---------- ADMIN ----------
                        composable("adminHome") {
                            ListaProfesionalesScreen(navController)
                        }

                        composable("perfilAdmin") {
                            PerfilAdministradorScreen(navController)
                        }

                        composable("crearProfesional") {
                            CrearProfesionalScreen(
                                onGuardar = { prof ->
                                    scope.launch {
                                        try {
                                            Repository.agregarProfesional(prof)
                                            navController.popBackStack()
                                        } catch (_: Exception) {
                                            // Podrías mostrar un snackbar si quieres
                                        }
                                    }
                                },
                                onCancelar = { navController.popBackStack() }
                            )
                        }

                        composable(
                            "modificarProfesional/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rut = backStackEntry.arguments?.getString("rut") ?: ""

                            var profesional by remember(rut) { mutableStateOf<ProfesionalRemote?>(null) }
                            var cargando by remember(rut) { mutableStateOf(true) }
                            var error by remember(rut) { mutableStateOf<String?>(null) }

                            LaunchedEffect(rut) {
                                cargando = true
                                error = null
                                try {
                                    profesional = Repository.obtenerProfesional(rut)
                                } catch (e: Exception) {
                                    error = "Error al cargar profesional"
                                } finally {
                                    cargando = false
                                }
                            }

                            when {
                                cargando -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                error != null -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(error ?: "Error", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                                profesional != null -> {
                                    ModificarProfesionalScreen(
                                        profesional = profesional!!,
                                        onGuardar = { actualizado ->
                                            scope.launch {
                                                try {
                                                    Repository.actualizarProfesional(actualizado)
                                                    navController.popBackStack()
                                                } catch (_: Exception) { }
                                            }
                                        },
                                        onCancelar = { navController.popBackStack() },
                                        onEliminar = { p ->
                                            scope.launch {
                                                try {
                                                    Repository.eliminarProfesional(p.rut)
                                                    navController.popBackStack()
                                                } catch (_: Exception) { }
                                            }
                                        }
                                    )
                                }
                                else -> {
                                    Text("Profesional no encontrado", color = Color.Red)
                                }
                            }
                        }

                        // ---------- PROFESIONAL ----------
                        composable("profesionalHome") {
                            HomeProfesionalScreen()
                        }

                        composable("perfilProfesional") {
                            PerfilProfesionalDoctorScreen(navController)
                        }

                        // ---------- CLIENTE ----------

                        // Lista de profesionales para el cliente
                        composable("clienteProfesionales") {
                            var profesionales by remember { mutableStateOf<List<ProfesionalUi>>(emptyList()) }

                            LaunchedEffect(Unit) {
                                val desdeApi: List<ProfesionalRemote> = try {
                                    Repository.obtenerProfesionales()
                                } catch (_: Exception) {
                                    emptyList()
                                }

                                profesionales = desdeApi.map { p ->
                                    ProfesionalUi(
                                        rut = p.rut,
                                        nombres = p.nombres,
                                        apellidos = p.apellidos,
                                        genero = p.genero,
                                        fechaNacimiento = p.fechaNacimiento,
                                        especialidad = p.especialidad,
                                        email = p.email,
                                        telefono = p.telefono
                                    )
                                }
                            }

                            ProfesionalesScreen(
                                profesionales = profesionales,
                                onProfesionalClick = { rut ->
                                    val rutSeg = android.net.Uri.encode(rut)
                                    navController.navigate("clientePerfilProfesional/$rutSeg")
                                }
                            )
                        }

                        // Perfil de un profesional visto por el cliente
                        composable(
                            "clientePerfilProfesional/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutProfesional = backStackEntry.arguments?.getString("rut") ?: ""
                            val ctx = LocalContext.current

                            val emailSesion = SesionManager.obtenerEmail(ctx)

                            var rutCliente by remember { mutableStateOf<String?>(null) }
                            var profesional by remember { mutableStateOf<ProfesionalRemote?>(null) }

                            LaunchedEffect(emailSesion) {
                                if (emailSesion != null) {
                                    try {
                                        rutCliente = Repository.obtenerClientePorEmail(emailSesion)?.rut
                                    } catch (_: Exception) { }
                                }
                            }

                            LaunchedEffect(rutProfesional) {
                                try {
                                    profesional = Repository.obtenerProfesional(rutProfesional)
                                } catch (_: Exception) { }
                            }

                            val nombre = profesional?.let { "${it.nombres} ${it.apellidos}" } ?: "Profesional"
                            val especialidad = profesional?.especialidad ?: "Especialidad"

                            com.example.clinicaveterinaria.ui.cliente.PerfilProfesionalScreen(
                                nombre = nombre,
                                especialidad = especialidad,
                                bio = "",
                                servicios = listOf("Consulta $especialidad", "Control / Evaluación"),
                                fotoResId = R.drawable.logo,
                                onAgendarClick = {
                                    val rutCli = rutCliente
                                    if (rutCli.isNullOrBlank()) {
                                        navController.navigate("login") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } else {
                                        scope.launch {
                                            val tieneMascota = try {
                                                Repository.clienteTieneMascota(rutCli)
                                            } catch (_: Exception) {
                                                false
                                            }

                                            if (tieneMascota) {
                                                navController.navigate("clienteAgendar/$rutProfesional")
                                            } else {
                                                navController.navigate("clienteAgregarMascota/$rutCli")
                                            }
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Agendar reserva
                        composable(
                            "clienteAgendar/{rut}",
                            arguments = listOf(navArgument("rut") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val rutProfesional = backStackEntry.arguments?.getString("rut") ?: ""
                            val ctx = LocalContext.current
                            val emailSesion = SesionManager.obtenerEmail(ctx)

                            var clienteRut by remember { mutableStateOf<String?>(null) }
                            var prof by remember { mutableStateOf<ProfesionalRemote?>(null) }

                            LaunchedEffect(emailSesion) {
                                if (emailSesion != null) {
                                    try {
                                        clienteRut = Repository.obtenerClientePorEmail(emailSesion)?.rut
                                    } catch (_: Exception) { }
                                }
                            }

                            LaunchedEffect(rutProfesional) {
                                try {
                                    prof = Repository.obtenerProfesional(rutProfesional)
                                } catch (_: Exception) { }
                            }

                            val nombreProf = prof?.let { "${it.nombres} ${it.apellidos}" } ?: "Profesional"
                            val especialidad = prof?.especialidad ?: ""
                            val fotoResId = R.drawable.logo

                            var fecha by rememberSaveable { mutableStateOf("") }
                            var hora by rememberSaveable { mutableStateOf("") }
                            var servicio by rememberSaveable { mutableStateOf("") }
                            var ok by remember { mutableStateOf<String?>(null) }
                            var error by remember { mutableStateOf<String?>(null) }

                            var horarios by remember { mutableStateOf<List<String>>(emptyList()) }

                            LaunchedEffect(fecha, rutProfesional) {
                                horarios = try {
                                    if (fecha.isBlank()) emptyList()
                                    else Repository.horasDisponibles(rutProfesional, fecha)
                                } catch (_: Exception) {
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
                                    val rutCli = clienteRut
                                    when {
                                        fecha.isBlank() || hora.isBlank() || servicio.isBlank() -> {
                                            ok = null
                                            error = "Completa fecha, hora y servicio"
                                        }
                                        rutCli.isNullOrBlank() -> {
                                            ok = null
                                            error = "No se pudo identificar al cliente (sesión)"
                                        }
                                        else -> {
                                            scope.launch {
                                                val res = try {
                                                    Repository.agregarReserva(rutCli, rutProfesional, fecha, hora, servicio)
                                                } catch (e: Exception) {
                                                    null
                                                }

                                                if (res != null && res.ok) {
                                                    error = null
                                                    ok = "Reserva creada ✔"
                                                    navController.navigate("clienteMisReservas")
                                                } else {
                                                    ok = null
                                                    error = res?.mensaje ?: "Error al crear la reserva"
                                                }
                                            }
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

                        // Mis reservas del cliente
                        composable("clienteMisReservas") {
                            val ctx = LocalContext.current
                            val emailSesion = SesionManager.obtenerEmail(ctx)

                            var rutCliente by remember { mutableStateOf<String?>(null) }
                            var reservasRepo by remember { mutableStateOf<List<Reserva>>(emptyList()) }

                            LaunchedEffect(emailSesion) {
                                if (emailSesion != null) {
                                    try {
                                        rutCliente = Repository.obtenerClientePorEmail(emailSesion)?.rut
                                    } catch (_: Exception) { }
                                }
                            }

                            LaunchedEffect(rutCliente) {
                                val rutCli = rutCliente
                                if (!rutCli.isNullOrBlank()) {
                                    reservasRepo = try {
                                        Repository.obtenerReservasCliente(rutCli)
                                    } catch (_: Exception) {
                                        emptyList()
                                    }
                                }
                            }

                            val reservasUi = remember(reservasRepo) {
                                reservasRepo.map { r ->
                                    ReservaMock(
                                        id = r.id.toString(),
                                        fecha = r.fecha,
                                        hora = r.hora,
                                        profesional = r.profesionalRut, // simple: mostramos RUT
                                        servicio = r.servicio,
                                        estado = r.estado
                                    )
                                }
                            }

                            MisReservasScreen(
                                reservas = reservasUi,
                                onCancelarClick = { idStr ->
                                    val rutCli = rutCliente
                                    val id = idStr.toLongOrNull()
                                    if (id != null && !rutCli.isNullOrBlank()) {
                                        scope.launch {
                                            val res = try {
                                                Repository.cancelarReserva(id)
                                            } catch (_: Exception) {
                                                null
                                            }
                                            if (res != null && res.ok) {
                                                reservasRepo = try {
                                                    Repository.obtenerReservasCliente(rutCli)
                                                } catch (_: Exception) {
                                                    emptyList()
                                                }
                                            }
                                        }
                                    }
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }

                        // Perfil cliente
                        composable("clientePerfil") {
                            val ctx = LocalContext.current
                            val emailSesion = SesionManager.obtenerEmail(ctx)

                            var cliente by remember { mutableStateOf<Cliente?>(null) }
                            var cargando by remember { mutableStateOf(true) }

                            LaunchedEffect(emailSesion) {
                                cargando = true
                                try {
                                    cliente = if (emailSesion != null) {
                                        Repository.obtenerClientePorEmail(emailSesion)
                                    } else null
                                } catch (_: Exception) {
                                    cliente = null
                                } finally {
                                    cargando = false
                                }
                            }

                            when {
                                cargando -> {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                                cliente != null -> {
                                    PerfilClienteScreen(
                                        mockNombre = "${cliente!!.nombres} ${cliente!!.apellidos}",
                                        mockEmail = cliente!!.email,
                                        mockTelefono = cliente!!.telefono,
                                        onChangePasswordClick = { /* TODO si quieres */ },
                                        onLogoutClick = {
                                            SesionManager.cerrarSesion(ctx)
                                            navController.navigate("login") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                                else -> {
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
                        }

                        // Registrar mascota después de crear cliente
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
