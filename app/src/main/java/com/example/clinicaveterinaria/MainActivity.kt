package com.example.clinicaveterinaria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.ui.admin.CrearProfesionalScreen
import com.example.clinicaveterinaria.ui.admin.ListaProfesionalesScreen
import com.example.clinicaveterinaria.ui.admin.ModificarProfesionalScreen
import com.example.clinicaveterinaria.ui.profesional.HomeProfesionalScreen
import com.example.clinicaveterinaria.ui.profesional.LoginScreen
import com.example.clinicaveterinaria.ui.profesional.PerfilProfesionalScreenPreview
import com.example.clinicaveterinaria.ui.theme.ClinicaVeterinariaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.clinicaveterinaria.data.Repository.init(this)
        enableEdgeToEdge()
        setContent {
            ClinicaVeterinariaTheme {
                val navController = rememberNavController()
                val backStack by navController.currentBackStackEntryAsState()
                val currentRoute = backStack?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = currentRoute == "home",
                                onClick = { navController.navigate("home") },
                                label = { Text("Inicio") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "form",
                                onClick = { navController.navigate("form") },
                                label = { Text("Profesionales") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "Form") }
                            )
                            NavigationBarItem(
                                selected = currentRoute == "perfilProfesional",
                                onClick = { navController.navigate("perfilProfesional") },
                                label = { Text("perfilProfesional") },
                                icon = { Icon(Icons.Filled.Home, contentDescription = "perfilProfesional") }
                            )
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "home",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("home") { HomeProfesionalScreen() }
                        composable("form") { ListaProfesionalesScreen(navController) }
                        composable("login") { LoginScreen() }
                        composable("perfilProfesional"){ PerfilProfesionalScreenPreview() }
                        // Crear
                        composable("crearProfesional") {
                            CrearProfesionalScreen(
                                onGuardar = { prof ->
                                    Repository.agregarProfesional(prof)
                                    navController.popBackStack()
                                },
                                onCancelar = { navController.popBackStack() }
                            )
                        }

                        // Editar por RUT
                        composable(
                            route = "modificarProfesional/{rut}",
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
                    }
                }
            }
        }
    }
}
