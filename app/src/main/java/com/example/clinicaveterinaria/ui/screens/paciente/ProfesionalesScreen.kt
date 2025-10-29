package com.example.clinicaveterinaria.ui.screens.paciente

// ... (imports de layout, lazycolumn, etc.)
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Este es tu modelo de datos
data class Profesional (
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val genero: String,
    val fechaNacimiento: String,
    val especialidad: String,
    val email: String,
    val telefono: String
)
@Composable
fun ProfesionalesScreen(
    profesionales: List<Profesional>, // <-- Usa tu nuevo data class
    onProfesionalClick: (profesionalRut: String) -> Unit // <-- Pasa un String (el RUT)
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        items(profesionales) { profesional ->
            ProfesionalCard(
                profesional = profesional,
                onClick = {
                    // Llama a la función con el RUT del profesional
                    onProfesionalClick(profesional.rut)
                }
            )
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
            // CAMBIO: Muestra nombres y apellidos
            Text(
                text = "${profesional.nombres} ${profesional.apellidos}",
                style = MaterialTheme.typography.titleMedium
            )
            // Esto se mantiene, ya que 'especialidad' existe en el nuevo modelo
            Text(
                text = profesional.especialidad,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}