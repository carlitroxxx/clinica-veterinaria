package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


enum class EstadoAtencion { PENDIENTE, REALIZADA, CANCELADA }

data class ReservaUi(
    val id: Long,
    val hora: String,
    val paciente: String,
    val servicio: String,
    val mascota: String = "—",
    val estado: EstadoAtencion
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeProfesionalScreen() {
    // --- (Lógica de estado se queda igual) ---
    var mostrarDialogRegistrar by remember { mutableStateOf(false) }
    var mostrarDialogDetalle by remember { mutableStateOf(false) }
    val ctx = androidx.compose.ui.platform.LocalContext.current
    val emailSesion = remember { SesionManager.obtenerEmail(ctx) }
    val profesional = remember(emailSesion) {
        emailSesion?.let { Repository.obtenerProfesionalPorEmail(it) }
    }
    val rutProfesional = profesional?.rut

    val hoy = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
    }
    var reservasUi by remember { mutableStateOf<List<ReservaUi>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(rutProfesional, hoy) {
        cargando = true
        reservasUi = if (rutProfesional != null) {
            Repository.obtenerReservasProfesionalEn(rutProfesional, hoy).map { r ->
                val estadoEnum = when (r.estado.trim().lowercase()) {
                    "realizada" -> EstadoAtencion.REALIZADA
                    "cancelada" -> EstadoAtencion.CANCELADA
                    else -> EstadoAtencion.PENDIENTE
                }
                ReservaUi(
                    id = r.id,
                    hora = r.hora,
                    paciente = r.clienteNombre,
                    servicio = r.servicio,
                    estado = estadoEnum
                )
            }
        } else emptyList()
        cargando = false
    }

    val opciones = listOf("TODAS", "PENDIENTE", "REALIZADA", "CANCELADA")
    var filtroSeleccionado by rememberSaveable { mutableStateOf(opciones.first()) }
    var dropdownAbierto by remember { mutableStateOf(false) }


    val reservasFiltradas = remember(filtroSeleccionado, reservasUi) {
        if (filtroSeleccionado == "TODAS") reservasUi
        else reservasUi.filter { it.estado.name == filtroSeleccionado }
    }

    // Variables para los diálogos
    val reservaSel = reservasFiltradas.firstOrNull()
    val pacienteSel = reservaSel?.paciente ?: "—"
    val horaSel = reservaSel?.hora ?: "—"
    val servicioSel = reservaSel?.servicio ?: "—"

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val textFieldColors = TextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        focusedContainerColor = colorFondoCampo,
        unfocusedIndicatorColor = colorPrincipal,
        focusedIndicatorColor = colorPrincipal,
        focusedLabelColor = colorPrincipal,
        unfocusedLabelColor = colorPrincipal.copy(alpha = 0.7f),
        unfocusedTrailingIconColor = colorPrincipal.copy(alpha = 0.7f),
        focusedTrailingIconColor = colorPrincipal
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agenda de hoy ($hoy)") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // <-- Padding aplicado
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = dropdownAbierto,
                onExpandedChange = { dropdownAbierto = !dropdownAbierto },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                TextField(
                    readOnly = true,
                    value = filtroSeleccionado,
                    onValueChange = {},
                    label = { Text("Filtrar por estado") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownAbierto) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    colors = textFieldColors
                )
                ExposedDropdownMenu(
                    expanded = dropdownAbierto,
                    onDismissRequest = { dropdownAbierto = false }
                ) {
                    opciones.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                filtroSeleccionado = opcion
                                dropdownAbierto = false
                            }
                        )
                    }
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                when{
                    cargando -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    reservasFiltradas.isEmpty() ->{
                        Text(
                            "No hay reservas para este filtro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }else -> {
                    reservasFiltradas.forEach { r ->

                        val (chipColor, chipBorder, chipLabelColor) = when (r.estado) {
                            EstadoAtencion.REALIZADA -> Triple(colorPrincipal.copy(alpha = 0.1f), null, colorPrincipal)
                            EstadoAtencion.CANCELADA -> Triple(Color.LightGray.copy(alpha = 0.2f), null, Color.Gray)
                            EstadoAtencion.PENDIENTE -> Triple(colorFondoCampo, BorderStroke(1.dp, colorPrincipal), colorPrincipal)
                        }

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    r.hora,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorPrincipal
                                )
                                Column(
                                    Modifier
                                        .weight(1f)
                                        .padding(end = 8.dp, start = 20.dp)
                                ) {
                                    Text(
                                        "  ${r.paciente}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        " · ${r.servicio}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                AssistChip(
                                    onClick = {},
                                    label = { Text(r.estado.name) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = chipColor,
                                        labelColor = chipLabelColor
                                    ),
                                    border = chipBorder
                                )
                            }

                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { mostrarDialogDetalle = true },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal))
                                ) { Text("Detalle", maxLines = 1, softWrap = false) }

                                Button(
                                    onClick = { mostrarDialogRegistrar = true },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f),

                                    colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                                ) { Text("Registrar", maxLines = 1, softWrap = false) }
                            }
                        }
                    }
                }
                }
            }
        }
    }

    if (mostrarDialogRegistrar && reservaSel != null) {
        RegistrarAtencionFormDialog(
            titulo = "Registrar atención",
            subtitulo = "${reservaSel.paciente} • ${reservaSel.servicio} — ${reservaSel.hora}",
            onDismiss = { mostrarDialogRegistrar = false },
            onGuardar = { form ->
                mostrarDialogRegistrar = false
            }
        )
    }

    if (mostrarDialogDetalle && reservaSel != null) {
        DetalleReservaDialog(
            paciente = reservaSel.paciente,
            hora = reservaSel.hora,
            servicio = reservaSel.servicio,
            onCerrar = { mostrarDialogDetalle = false }
        )
    }
}


@Composable
private fun DetalleReservaDialog(
    paciente: String,
    hora: String,
    servicio: String,
    onCerrar: () -> Unit
) {
    val colorPrincipal = Color(0xFF00AAB0)

    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Detalle de la reserva") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Cliente: $paciente")
                Text("Mascota: ")
                Text("Hora: $hora")
                Text("Servicio: $servicio")
            }
        },
        confirmButton = {
            Button(
                onClick = onCerrar,
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
            ) { Text("Cerrar") }
        }
    )
}