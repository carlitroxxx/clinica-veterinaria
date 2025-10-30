package com.example.clinicaveterinaria.ui.screens.paciente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarMascotaScreen(
    nombre: String,
    onNombreChange: (String) -> Unit,
    especie: String,
    onEspecieChange: (String) -> Unit,
    raza: String,
    onRazaChange: (String) -> Unit,
    fechaNacimiento: String,
    onFechaNacimientoChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = Instant.now().toEpochMilli()
    )

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
                            onFechaNacimientoChange(selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE))
                        }
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Mascota") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo Clínica",
                modifier = Modifier
                    .height(100.dp)
                    .fillMaxWidth(0.7f)
            )

            Text(
                text = "Información de tu mascota",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = onNombreChange,
                label = { Text("Nombre de la mascota") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = especie,
                onValueChange = onEspecieChange,
                label = { Text("Especie (ej. Canino, Felino)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = raza,
                onValueChange = onRazaChange,
                label = { Text("Raza (ej. Labrador, Siamés)") },
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            ) {
                OutlinedTextField(
                    value = fechaNacimiento,
                    onValueChange = { /* No hace nada */ },
                    label = { Text("Fecha de Nacimiento") },
                    placeholder = { Text("Selecciona una fecha") },
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

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onGuardarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Mascota")
            }
        }
    }
}