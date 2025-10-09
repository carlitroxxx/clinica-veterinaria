package com.example.clinicaveterinaria.ui.theme

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
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
import com.example.clinicaveterinaria.ui.HomeScreen

@Composable

fun MenuBar(){
    val navController = rememberNavController()
    Scaffold (
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    //ruta princiapl
                    selected = navController.currentBackStackEntry?.destination?.route=="home",
                    onClick = {navController.navigate("home")},
                    label = {Text("inicio")},
                    icon = { Icon(Icons.Filled.Home, "Inicio") }
                )
            }
        }
    ){ innerPadding ->
        NavHost(
            navController = navController,
            startDestination= "home",
            modifier= Modifier.padding(innerPadding)
        ){
            composable("home"){ HomeScreen() }
        }

    }
}