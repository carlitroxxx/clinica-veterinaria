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
@Composable
fun PerfilProfesionalScreen(
    nombre: String,
    especialidad: String,
    bio: String,
    servicios: List<String>,
    onAgendarClick: () -> Unit
) {

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Muestra los datos recibidos
        Text(nombre, style = MaterialTheme.typography.headlineLarge)
        Text(especialidad, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(bio, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Servicios:", style = MaterialTheme.typography.titleMedium)

        // Muestra la lista de servicios recibida
        servicios.forEach { servicio ->
            Text(servicio, style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(32.dp))

        // Botón "Agendar"
        Button(onClick = onAgendarClick) {
            Text("Agendar")
        }
    }
}