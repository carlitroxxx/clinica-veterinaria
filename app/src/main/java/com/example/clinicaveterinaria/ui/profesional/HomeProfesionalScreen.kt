package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    // Estados para mostrar/ocultar los popups
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


    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Agenda de hoy $hoy",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleLarge
        )
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
                    .fillMaxWidth()
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            when{
                cargando -> {
                    Text("Cargando reservas...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                reservasFiltradas.isEmpty() ->{
                    Text(
                        "No hay reservas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }else -> {
                    reservasFiltradas.forEach { r ->
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    r.hora,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Column(
                                    Modifier
                                        .weight(0.55f)
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
                                    label = { Text(r.estado.name) }
                                )
                            }

                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { mostrarDialogDetalle = true },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.widthIn(min = 120.dp),
                                    shape = RoundedCornerShape(8.dp)
                                ) { Text("Detalle", maxLines = 1, softWrap = false) }

                                OutlinedButton(
                                    onClick = { mostrarDialogRegistrar = true /* aquí puedes pasar r.id */ },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.widthIn(min = 120.dp)
                                ) { Text("Registrar atención", maxLines = 1, softWrap = false) }
                            }
                        }
                    }
                }

            }

        }
    }

    // POPUP: Registrar atención
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

    // POPUP: Ver detalle
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
            FilledTonalButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}
