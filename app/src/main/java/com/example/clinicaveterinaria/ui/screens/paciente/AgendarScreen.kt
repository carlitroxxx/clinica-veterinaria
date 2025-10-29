package com.example.clinicaveterinaria.ui.screens.paciente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image // <-- Import nuevo
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement // <-- Import nuevo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // <-- Import nuevo
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton // <-- Import nuevo
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold // <-- Import nuevo
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar // <-- Import nuevo
import androidx.compose.material3.TopAppBarDefaults // <-- Import nuevo
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment // <-- Import nuevo
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // <-- Import nuevo
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R // <-- Import nuevo (para el logo)
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
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

    // --- Lógica y Estado para Pickers (Esto se queda igual) ---
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )
    var isHoraMenuExpanded by remember { mutableStateOf(false) }
    val horariosDisponibles = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30"
    )

    // --- Composable del Diálogo (Esto se queda igual) ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault()).toLocalDate()
                            onFechaChange(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // --- CAMBIO 1: Usamos Scaffold como estructura principal ---
    Scaffold(
        topBar = {
            // --- CAMBIO 2: Añadimos una barra de título (TopAppBar) ---
            TopAppBar(
                title = { Text("Agendar Cita") }, // <-- Título aquí
                navigationIcon = {
                    // <-- Ícono de "volver" (solo visual por ahora)
                    IconButton(onClick = { /* TODO: navController.popBackStack() */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                // <-- Colores del tema para la barra
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding -> // <-- El padding que nos da el Scaffold

        // --- CAMBIO 3: La Columna ahora tiene mejor espaciado y centrado ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // <-- Usamos el padding del Scaffold
                .padding(16.dp), // <-- Añadimos nuestro padding
            // Centra el logo
            horizontalAlignment = Alignment.CenterHorizontally,
            // Añade 16.dp de espacio entre cada elemento
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- CAMBIO 4: Añadimos el Logo ---
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Clínica",
                modifier = Modifier
                    .height(100.dp) // Tamaño del logo
                    .fillMaxWidth(0.7f) // Que no ocupe toda la pantalla
            )

            // --- Formulario (Misma lógica, pero ahora se ve mejor espaciado) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = fecha,
                    onValueChange = { /* No hace nada */ },
                    label = { Text("Fecha") },
                    placeholder = { Text("Selecciona una fecha") },
                    isError = mensajeError?.contains("fecha") == true,
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Filled.DateRange,
                            contentDescription = "Seleccionar fecha"
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false,
                )
            }

            ExposedDropdownMenuBox(
                expanded = isHoraMenuExpanded,
                onExpandedChange = { isHoraMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = hora,
                    onValueChange = { /* No hace nada */ },
                    readOnly = true,
                    label = { Text("Hora") },
                    placeholder = { Text("Selecciona una hora") },
                    isError = mensajeError?.contains("hora") == true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isHoraMenuExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isHoraMenuExpanded,
                    onDismissRequest = { isHoraMenuExpanded = false }
                ) {
                    horariosDisponibles.forEach { horaSeleccionada ->
                        DropdownMenuItem(
                            text = { Text(horaSeleccionada) },
                            onClick = {
                                onHoraChange(horaSeleccionada)
                                isHoraMenuExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = servicio,
                onValueChange = onServicioChange,
                label = { Text("Servicio o Motivo") }, // <-- Texto mejorado
                isError = mensajeError?.contains("servicio") == true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onConfirmarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }

            // --- Feedback (Se queda igual) ---
            if (mensajeError != null) {
                Text(mensajeError, color = MaterialTheme.colorScheme.error)
            }
            if (mensajeExito != null) {
                Text(mensajeExito, color = Color(0xFF2E7D32)) // Verde éxito
            }
        }
    }
}