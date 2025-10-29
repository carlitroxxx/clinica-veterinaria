package com.example.clinicaveterinaria.ui.admin

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.data.Profesional

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProfesionalScreen(
    onGuardar: (Profesional) -> Unit,
    onCancelar: () -> Unit
) {
    var rut by rememberSaveable { mutableStateOf("") }
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var genero by rememberSaveable { mutableStateOf("") }
    var fechaNac by rememberSaveable { mutableStateOf("") } // AAAA-MM-DD
    var especialidad by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }

    val rutOk = rut.trim().isNotEmpty()
    val nombresOk = nombres.trim().isNotEmpty()
    val apellidosOk = apellidos.trim().isNotEmpty()
    val generoOk = genero.isNotEmpty()
    val fechaOk = Regex("""\d{4}-\d{2}-\d{2}""").matches(fechaNac)
    val especialidadOk = especialidad.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val telefonoOk = telefono.trim().length in 8..12 && telefono.all { it.isDigit() }

    val formOk = rutOk && nombresOk && apellidosOk && generoOk && fechaOk &&
            especialidadOk && emailOk && telefonoOk

    Scaffold(topBar = { TopAppBar(title = { Text("Nuevo profesional") }) }) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(inner) // 👈 elimina el espacio reservado por el Scaffold
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                OutlinedTextField(
                    value = rut, onValueChange = { rut = it },
                    label = { Text("RUT") }, singleLine = true,
                    isError = !rutOk && rut.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = nombres, onValueChange = { nombres = it },
                    label = { Text("Nombres") }, singleLine = true,
                    isError = !nombresOk && nombres.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = apellidos, onValueChange = { apellidos = it },
                    label = { Text("Apellidos") }, singleLine = true,
                    isError = !apellidosOk && apellidos.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                var openGenero by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(expanded = openGenero, onExpandedChange = { openGenero = it }) {
                    OutlinedTextField(
                        value = genero,
                        onValueChange = {},
                        label = { Text("Género") },
                        readOnly = true,
                        isError = !generoOk && genero.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openGenero) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = openGenero, onDismissRequest = { openGenero = false }) {
                        listOf("Masculino", "Femenino", "Otro").forEach {
                            DropdownMenuItem(text = { Text(it) }, onClick = { genero = it; openGenero = false })
                        }
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = fechaNac, onValueChange = { fechaNac = it },
                    label = { Text("Fecha de nacimiento (AAAA-MM-DD)") }, singleLine = true,
                    isError = !fechaOk && fechaNac.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = especialidad, onValueChange = { especialidad = it },
                    label = { Text("Especialidad") }, singleLine = true,
                    isError = !especialidadOk && especialidad.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") }, singleLine = true,
                    isError = !emailOk && email.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it.filter(Char::isDigit) },
                    label = { Text("Teléfono") }, singleLine = true,
                    isError = !telefonoOk && telefono.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(onClick = onCancelar, modifier = Modifier.weight(1f)) { Text("Cancelar") }
                    Button(
                        onClick = {
                            onGuardar(
                                Profesional(
                                    rut = rut.trim(),
                                    nombres = nombres.trim(),
                                    apellidos = apellidos.trim(),
                                    genero = genero,
                                    fechaNacimiento = fechaNac,
                                    especialidad = especialidad.trim(),
                                    email = email.trim(),
                                    telefono = telefono.trim()
                                )
                            )
                        },
                        enabled = formOk,
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }
                }
            }
        }
    }

}
