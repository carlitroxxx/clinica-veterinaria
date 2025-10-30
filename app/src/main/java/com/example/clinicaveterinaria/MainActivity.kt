package com.example.clinicaveterinaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.clinicaveterinaria.ui.profesional.PerfilProfesionalScreen
import com.example.clinicaveterinaria.ui.theme.ClinicaVeterinariaTheme

class MainActivity : ComponentActivity() {
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
                        composable("perfilProfesional") { PerfilProfesionalScreen(navController) }
                    }
                }
            }
        }
    }
}
