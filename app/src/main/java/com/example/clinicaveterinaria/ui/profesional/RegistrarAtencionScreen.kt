package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

data class AtencionFormData(
    val estado: String,
    val motivoCancelacion: String?,
    val diagnostico: String,
    val indicaciones: String,
)

@Composable
fun RegistrarAtencionScreen(

    idReserva: Int,
    nombrePaciente: String,
    nombreDuenio: String,
    onGuardar: (AtencionFormData) -> Unit,
    onCancelado: () -> Unit = {}
) {
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }

    Button(
        onClick = { mostrarFormulario = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Registrar atención")
    }

    if (mostrarFormulario) {
        RegistrarAtencionFormDialog(
            titulo = "Registrar atención",
            subtitulo = "$nombrePaciente • $nombreDuenio",
            onDismiss = {
                mostrarFormulario = false
                onCancelado()
            },
            onGuardar = { data ->
                mostrarFormulario = false
                onGuardar(data)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarAtencionFormDialog(
    titulo: String,
    subtitulo: String? = null,
    onDismiss: () -> Unit,
    onGuardar: (AtencionFormData) -> Unit
) {
    // Estado del formulario
    var estado by rememberSaveable { mutableStateOf("Realizada") }
    var diagnostico by rememberSaveable { mutableStateOf("") }
    var indicaciones by rememberSaveable { mutableStateOf("") }
    var motivoCancelacion by rememberSaveable { mutableStateOf("") }

    // Validaciones simples
    val esCancelada = estado == "Cancelada"


    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(titulo) },
                        navigationIcon = {
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                            }
                        }
                    )
                }
            ) { inner ->
                Column(
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                ) {
                    if (!subtitulo.isNullOrBlank()) {
                        Text(
                            text = subtitulo,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(8.dp))
                    }

                    // ESTADO
                    Text("Estado", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterChip(
                            selected = estado == "Realizada",
                            onClick = { estado = "Realizada" },
                            label = { Text("Realizada") }
                        )
                        FilterChip(
                            selected = estado == "Cancelada",
                            onClick = { estado = "Cancelada" },
                            label = { Text("Cancelada") }
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Si cancelada, pedir motivo. Si realizada, pedir info clínica/pago.
                    if (esCancelada) {
                        OutlinedTextField(
                            value = motivoCancelacion,
                            onValueChange = { motivoCancelacion = it },
                            label = { Text("Motivo de cancelación...") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = diagnostico,
                            onValueChange = { diagnostico = it },
                            label = { Text("Diagnóstico") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = indicaciones,
                            onValueChange = { indicaciones = it },
                            label = { Text("Indicaciones / Prescripción") },
                            singleLine = false,
                            minLines = 3,
                            modifier = Modifier.fillMaxWidth()
                        )


                        Spacer(Modifier.height(8.dp))
                    }

                    Spacer(Modifier.height(20.dp))

                    // Botones inferiores
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f)
                        ) { Text("Cancelar") }

                        Button(
                            onClick = {
                                onGuardar(
                                    AtencionFormData(
                                        estado = estado,
                                        motivoCancelacion = if (esCancelada) motivoCancelacion.trim() else null,
                                        diagnostico = diagnostico.trim(),
                                        indicaciones = indicaciones.trim()
                                    )
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Confirmar") }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}