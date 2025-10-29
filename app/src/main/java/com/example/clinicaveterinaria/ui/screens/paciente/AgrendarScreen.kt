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
import androidx.navigation.NavController

@Composable
fun AgendarScreen(navController: NavController, profesionalId: Int) {
    var fecha by remember { mutableStateOf("") }
    var hora by remember { mutableStateOf("") }
    var servicio by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf<String?>(null) }
    var mensajeExito by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Agendar Cita", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        //Formulario
        OutlinedTextField(
            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (AAAA-MM-DD)") },
            isError = mensajeError?.contains("fecha") == true
        )
        OutlinedTextField(
            value = hora,
            onValueChange = { hora = it },
            label = { Text("Hora (HH:MM)") },
            isError = mensajeError?.contains("hora") == true
        )
        OutlinedTextField(
            value = servicio,
            onValueChange = { servicio = it },
            label = { Text("Servicio") },
            isError = mensajeError?.contains("servicio") == true
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Validación básica
        Button(onClick = {
            //Limpiar mensajes
            mensajeError = null
            mensajeExito = null

            // 2. Validación (Campos obligatorios)
            if (fecha.isBlank() || hora.isBlank() || servicio.isBlank()) {
                mensajeError = "Todos los campos son obligatorios"
                return@Button
            }

            // 3. Validación (Lógica de negocio)


            // Simulación de éxito
            println("Reserva Creada: $fecha, $hora, $servicio")

            //Feedback
            mensajeExito = "¡Reserva confirmada con éxito!"

            // (Aquí también irá el Recurso Nativo del Calendario)
        }) {
            Text("Confirmar Reserva")
        }

        //Feedback
        if (mensajeError != null) {
            Text(mensajeError!!, color = MaterialTheme.colorScheme.error)
        }
        if (mensajeExito != null) {
            Text(mensajeExito!!, color = Color(0xFF2E7D32)) // Verde éxito
        }
    }
}
