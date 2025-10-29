package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun AgendarScreen(
    fecha: String,
    onFechaChange: (String) -> Unit,
    hora: String,
    onHoraChange: (String) -> Unit,
    servicio: String,
    onServicioChange: (String) -> Unit,
    mensajeError: String?,
    mensajeExito: String?,
    onConfirmarClick: () -> Unit
) {


    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Agendar Cita", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // --- Formulario ---
        // El valor viene del parámetro 'fecha'
        // onValueChange llama a la función 'onFechaChange'
        OutlinedTextField(
            value = fecha,
            onValueChange = onFechaChange,
            label = { Text("Fecha (AAAA-MM-DD)") },
            isError = mensajeError?.contains("fecha") == true
        )
        OutlinedTextField(
            value = hora,
            onValueChange = onHoraChange,
            label = { Text("Hora (HH:MM)") },
            isError = mensajeError?.contains("hora") == true
        )
        OutlinedTextField(
            value = servicio,
            onValueChange = onServicioChange,
            label = { Text("Servicio") },
            isError = mensajeError?.contains("servicio") == true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- Botón de Acción ---
        // Toda la lógica de validación y de "qué hacer"
        // se movió al ViewModel. El botón solo notifica el clic.
        Button(onClick = onConfirmarClick) {
            Text("Confirmar Reserva")
        }

        // --- Feedback (Mensajes) ---
        // Esto es frontend, solo muestra los mensajes que recibe.
        if (mensajeError != null) {
            Text(mensajeError, color = MaterialTheme.colorScheme.error)
        }
        if (mensajeExito != null) {
            Text(mensajeExito, color = Color(0xFF2E7D32)) // Verde éxito
        }
    }
}