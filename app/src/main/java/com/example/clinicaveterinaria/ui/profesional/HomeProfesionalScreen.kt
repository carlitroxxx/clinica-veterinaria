package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomeProfesionalScreen() {
    // Estados para mostrar/ocultar los popups
    var mostrarDialogRegistrar by remember { mutableStateOf(false) }
    var mostrarDialogDetalle by remember { mutableStateOf(false) }

    // Datos fijos, despues hay que cargar los datos
    val pacienteSel = "Juan Pérez"
    val horaSel = "09:30"
    val servicioSel = "Consulta General"

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Agenda de hoy Dom 26 oct 2025",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
        // agregar funcion para fecha actual y tambien icon

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card {
                // hacer mensaje de falla al cargar con boton reintentar
                // hacer bucle con lenght de horas en el dia, se debe repetir el row
                // en caso de que no hayan reservas mostrar text "No tienes reservas hoy"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column(Modifier.weight(0.55f).padding(horizontal = 8.dp)) {
                        Text(
                            pacienteSel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            "$horaSel · $servicioSel",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(0.30f)
                            .wrapContentWidth(Alignment.End),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilledTonalButton(
                            onClick = { mostrarDialogRegistrar = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.widthIn(min = 84.dp)
                        ) { Text("Registrar", maxLines = 1, softWrap = false) }

                        OutlinedButton(
                            onClick = { mostrarDialogDetalle = true },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.widthIn(min = 84.dp)
                        ) { Text("Detalle", maxLines = 1, softWrap = false) }
                    }
                }
            }
        }
    }

    // POPUP: Registrar atención
    if (mostrarDialogRegistrar) {
        RegistrarAtencionDialog(
            paciente = pacienteSel,
            hora = horaSel,
            servicio = servicioSel,
            onCancelar = { mostrarDialogRegistrar = false },
            onConfirmar = {
                // TODO: Lógica de registro (guardar en BD/local, actualizar estado, etc.)
                mostrarDialogRegistrar = false
            }
        )
    }

    // POPUP: Ver detalle
    if (mostrarDialogDetalle) {
        DetalleReservaDialog(
            paciente = pacienteSel,
            hora = horaSel,
            servicio = servicioSel,
            onCerrar = { mostrarDialogDetalle = false }
        )
    }
}


@Composable
private fun RegistrarAtencionDialog(
    paciente: String,
    hora: String,
    servicio: String,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Registrar atención") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Paciente: $paciente")
                Text("Hora: $hora")
                Text("Servicio: $servicio")
                // TODO: agregar Inputs (diagnóstico, observaciones, etc.)
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = onConfirmar) { Text("Confirmar") }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DetalleReservaDialog(
    paciente: String,
    hora: String,
    servicio: String,
    onCerrar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Detalle de la reserva") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Paciente: $paciente")
                Text("Hora: $hora")
                Text("Servicio: $servicio")
            }
        },
        confirmButton = {
            FilledTonalButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}
