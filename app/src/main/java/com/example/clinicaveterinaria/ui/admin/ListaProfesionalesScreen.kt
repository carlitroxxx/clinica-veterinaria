package com.example.clinicaveterinaria.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ListaProfesionalesScreen() {
    // Estados para popups
    var mostrarDialogEliminar by remember { mutableStateOf(false) }
    var mostrarDialogAgregar by remember { mutableStateOf(false) }

    // Datos fijos, despues hay que cargar los datos
    val profesionales = remember {
        listOf(
            "Carlos Moil" to "Consulta General",
            "Ana Rojas" to "Odontología",
            "Juan Pérez" to "Cirugía",
            "Ana Rojas" to "Odontología",
            "Ana Rojas" to "Odontología"
        )
    }
    var seleccionado by remember { mutableStateOf(profesionales.first()) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        // Evita rellenos automáticos que generan espacio “extra”
        contentWindowInsets = WindowInsets(0),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarDialogAgregar = true },
                modifier = Modifier.padding(16.dp) // sin navigationBarsPadding para no dejar “hoyo”
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Agregar profesional")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Text(
                "Profesionales Registrados",
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp),
                style = MaterialTheme.typography.titleLarge
            )
            // agregar funcion para fecha actual y tambien icon

            // Lista con “cajones”: espacio blanco entre cards
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp) // espacio entre “cajones”
            ) {
                // hacer mensaje de falla al cargar con boton reintentar
                // hacer bucle con lenght de profesionales, se debe repetir el row
                // en caso de que no hayan profesionales mostrar text "No hay profesionales registrados"
                if (profesionales.isEmpty()) {
                    item {
                        Text(
                            "No hay profesionales registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(
                        items = profesionales,
                        // opcional: si tienes un id único úsalo como key = { it.id }
                    ) { (nombre, servicio) ->
                        Card {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp, horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(Modifier.weight(0.15f), contentAlignment = Alignment.CenterStart) {
                                    Icon(Icons.Filled.Person, contentDescription = null, modifier = Modifier.size(24.dp))
                                }
                                Column(Modifier.weight(0.55f).padding(horizontal = 8.dp)) {
                                    Text(nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        "· $servicio",
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
                                        onClick = { /* TODO: Navegar a editar / abrir dialog edición */ },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.widthIn(min = 84.dp)
                                    ) { Text("Modificar", maxLines = 1, softWrap = false) }

                                    OutlinedButton(
                                        onClick = { mostrarDialogEliminar = true /* y setear seleccionado */ },
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                        modifier = Modifier.widthIn(min = 84.dp)
                                    ) { Text("Eliminar", maxLines = 1, softWrap = false) }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    if (mostrarDialogEliminar) {
        EliminarProfesionalDialog(
            paciente = seleccionado.first,
            servicio = seleccionado.second,
            onCancelar = { mostrarDialogEliminar = false },
            onConfirmar = {
                // TODO: Eliminar en BD/estado y refrescar lista
                mostrarDialogEliminar = false
            }
        )
    }
    if (mostrarDialogAgregar) {
        AgregarProfesionalDialog(
            onCancelar = { mostrarDialogAgregar = false },
            onConfirmar = {
                // TODO: Crear en BD/estado y refrescar lista
                mostrarDialogAgregar = false
            }
        )
    }
}

@Composable
private fun EliminarProfesionalDialog(
    paciente: String,
    servicio: String,
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Eliminar Profesional") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Profesional: $paciente")
                Text("Servicio: $servicio")
            }
        },
        confirmButton = { FilledTonalButton(onClick = onConfirmar) { Text("Confirmar") } },
        dismissButton = { OutlinedButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}

@Composable
private fun AgregarProfesionalDialog(
    onCancelar: () -> Unit,
    onConfirmar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Agregar Profesional") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // TODO: Inputs para nombres, apellidos, rut, genero, fechaNacimiento, especialidad, email, telefono
                Text("Completa los datos del nuevo profesional.")
            }
        },
        confirmButton = { FilledTonalButton(onClick = onConfirmar) { Text("Guardar") } },
        dismissButton = { OutlinedButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}
