package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

// Datos que enviarás al guardar
data class AtencionFormData(
    val estado: String,              // "Realizada" | "Cancelada"
    val motivoCancelacion: String?,  // si estado = Cancelada
    val servicio: String,
    val diagnostico: String,
    val indicaciones: String,
    val precio: Int?,
    val metodoPago: String?,         // si aplica
    val proximaCita: String?,        // "YYYY-MM-DD HH:MM" (o lo que uses)
    val enviarResumenDuenio: Boolean
)

@Composable
fun RegistrarAtencionScreen(
    // Identificadores que puedas necesitar para guardar
    idReserva: Int,
    nombrePaciente: String,
    nombreDuenio: String,
    onGuardar: (AtencionFormData) -> Unit,
    onCancelado: () -> Unit = {}
) {
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }

    // Este es tu botón "Registrar" que ya tienes en la card/lista
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
    var estado by rememberSaveable { mutableStateOf("Realizada") } // Realizada | Cancelada
    var servicio by rememberSaveable { mutableStateOf("") }
    var diagnostico by rememberSaveable { mutableStateOf("") }
    var indicaciones by rememberSaveable { mutableStateOf("") }
    var motivoCancelacion by rememberSaveable { mutableStateOf("") }
    var precioTexto by rememberSaveable { mutableStateOf("") } // solo números
    var metodoPago by rememberSaveable { mutableStateOf("Efectivo") } // Efectivo | Tarjeta | Transferencia
    var proximaCita by rememberSaveable { mutableStateOf("") } // texto libre por ahora (YYYY-MM-DD HH:MM)
    var enviarResumen by rememberSaveable { mutableStateOf(true) }

    // Validaciones simples
    val esCancelada = estado == "Cancelada"
    val precioValido = precioTexto.isBlank() || precioTexto.all { it.isDigit() }
    val mostrarErrorPrecio = !precioValido
    val camposObligatoriosOk =
        if (esCancelada) {
            motivoCancelacion.trim().isNotEmpty()
        } else {
            // para realizada pedimos al menos servicio o diagnóstico
            servicio.trim().isNotEmpty() || diagnostico.trim().isNotEmpty()
        }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // Full screen dialog
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
                        },
                        actions = {
                            // Guardar en la appbar (opcional)
                            IconButton(
                                onClick = {
                                    if (camposObligatoriosOk && precioValido) {
                                        onGuardar(
                                            AtencionFormData(
                                                estado = estado,
                                                motivoCancelacion = if (esCancelada) motivoCancelacion.trim() else null,
                                                servicio = servicio.trim(),
                                                diagnostico = diagnostico.trim(),
                                                indicaciones = indicaciones.trim(),
                                                precio = precioTexto.toIntOrNull(),
                                                metodoPago = if (esCancelada) null else metodoPago,
                                                proximaCita = proximaCita.trim().ifBlank { null },
                                                enviarResumenDuenio = enviarResumen
                                            )
                                        )
                                    }
                                },
                                enabled = camposObligatoriosOk && precioValido
                            ) {
                                Icon(Icons.Filled.Save, contentDescription = "Guardar")
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
                            label = { Text("Motivo de cancelación *") },
                            singleLine = false,
                            minLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        OutlinedTextField(
                            value = servicio,
                            onValueChange = { servicio = it },
                            label = { Text("Servicio realizado") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

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

                        // Precio
                        OutlinedTextField(
                            value = precioTexto,
                            onValueChange = { nuevo ->
                                // aceptar solo números
                                if (nuevo.all { it.isDigit() }) precioTexto = nuevo
                                else if (nuevo.isEmpty()) precioTexto = ""
                                // si quieres permitir decimales, cambia la lógica
                            },
                            isError = mostrarErrorPrecio,
                            supportingText = {
                                if (mostrarErrorPrecio) Text("Solo números")
                            },
                            label = { Text("Precio (CLP)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Text("Método de pago", style = MaterialTheme.typography.labelLarge)
                        Spacer(Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Efectivo", "Tarjeta", "Transferencia").forEach { mp ->
                                FilterChip(
                                    selected = metodoPago == mp,
                                    onClick = { metodoPago = mp },
                                    label = { Text(mp) }
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = proximaCita,
                            onValueChange = { proximaCita = it },
                            label = { Text("Próxima cita (opcional)") },
                            placeholder = { Text("YYYY-MM-DD HH:MM") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { enviarResumen = !enviarResumen }
                        ) {
                            Checkbox(
                                checked = enviarResumen,
                                onCheckedChange = { enviarResumen = it }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("Enviar resumen al dueño por correo")
                        }
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
                                if (camposObligatoriosOk && precioValido) {
                                    onGuardar(
                                        AtencionFormData(
                                            estado = estado,
                                            motivoCancelacion = if (esCancelada) motivoCancelacion.trim() else null,
                                            servicio = servicio.trim(),
                                            diagnostico = diagnostico.trim(),
                                            indicaciones = indicaciones.trim(),
                                            precio = precioTexto.toIntOrNull(),
                                            metodoPago = if (esCancelada) null else metodoPago,
                                            proximaCita = proximaCita.trim().ifBlank { null },
                                            enviarResumenDuenio = enviarResumen
                                        )
                                    )
                                }
                            },
                            enabled = camposObligatoriosOk && precioValido,
                            modifier = Modifier.weight(1f)
                        ) { Text("Guardar") }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}