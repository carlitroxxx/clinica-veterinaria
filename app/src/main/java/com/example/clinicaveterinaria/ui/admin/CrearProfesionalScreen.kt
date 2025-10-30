package com.example.clinicaveterinaria.ui.admin

import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.util.RutUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone


@Composable
fun CrearProfesionalRoute(nav: NavHostController) {
    CrearProfesionalScreen(
        onGuardar = { p ->
            Repository.agregarProfesional(p)
            nav.popBackStack()
        },
        onCancelar = { nav.popBackStack() }
    )
}

// Pantalla de Crear Profesional
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
    var password by rememberSaveable { mutableStateOf("") } //

    val rutOk = RutUtils.rutEsValido(rut)
    val nombresOk = nombres.trim().isNotEmpty()
    val apellidosOk = apellidos.trim().isNotEmpty()
    val generoOk = genero.isNotEmpty()
    val fechaOk = Regex("""\d{4}-\d{2}-\d{2}""").matches(fechaNac)
    val especialidadOk = especialidad.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val telefonoOk = telefono.trim().length in 8..12 && telefono.all { it.isDigit() }
    val passwordOk = password.length in 4..20

    val formOk = rutOk && nombresOk && apellidosOk && generoOk && fechaOk &&
            especialidadOk && emailOk && telefonoOk && passwordOk

    Scaffold { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    Text("Nuevo profesional", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider(Modifier.padding(top = 8.dp))
                }
            }
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
                var showDatePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState()

                Box {
                    OutlinedTextField(
                        value = fechaNac,
                        onValueChange = { /* no editable */ },
                        label = { Text("Fecha de nacimiento") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Outlined.DateRange, contentDescription = "Elegir fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Box(
                        Modifier
                            .matchParentSize()
                            .padding(top = 9.dp, end = 48.dp)
                            .clickable { showDatePicker = true }
                    )
                }

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    fechaNac = formatDateMillis(datePickerState.selectedDateMillis)
                                    showDatePicker = false
                                }
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = true
                        )
                    }
                }
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
            //Contraseña inicial
            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = !passwordOk && password.isNotEmpty(),
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
                                    telefono = telefono.trim(),
                                    password = password.trim(),
                                    bio = ""
                                )
                            )
                        },
                        modifier = Modifier.weight(1f),
                        enabled = formOk
                    ) { Text("Guardar") }
                }
            }
        }
    }
}

// Utilidad: formatear millis a "yyyy-MM-dd" sin desfase de zona
private fun formatDateMillis(millis: Long?): String {
    if (millis == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}
