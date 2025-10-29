package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PerfilProfesionalScreen(navController: NavController, profesionalId: Int) {
    val profesional = mockProfesionales.find { it.id == profesionalId } ?: mockProfesionales.first()
    val servicios = listOf("Consulta General (30 min)", "Vacunación (15 min)")

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(profesional.nombre, style = MaterialTheme.typography.headlineLarge)
        Text(profesional.especialidad, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(profesional.bio, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Servicios:", style = MaterialTheme.typography.titleMedium)
        servicios.forEach { servicio ->
            Text(servicio, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(32.dp))

        //Botón "Agendar"
        Button(onClick = {
            navController.navigate("agendar/${profesional.id}")
        }) {
            Text("Agendar")
        }
    }
}