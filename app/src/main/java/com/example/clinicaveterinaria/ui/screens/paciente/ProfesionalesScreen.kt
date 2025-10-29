package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.clinicaveterinaria.data.local.Profesional

// Datos de prueba
val mockProfesionales = listOf(
    Profesional(1, "Dr. Juan Pérez", "Cardiología", "Amante de los perros...", null),
    Profesional(2, "Dra. Ana López", "Medicina General", "Especialista en gatos...", null)
)

@Composable
fun ProfesionalesScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(mockProfesionales) { profesional ->
            ProfesionalCard(profesional = profesional, onClick = {
                // RF-A3: Al tocar uno, voy al perfil
                navController.navigate("perfil_profesional/${profesional.id}")
            })
        }
    }
}

@Composable
fun ProfesionalCard(profesional: Profesional, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(profesional.nombre, style = MaterialTheme.typography.titleMedium)
            Text(profesional.especialidad, style = MaterialTheme.typography.bodySmall)
        }
    }
}

