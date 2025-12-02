package com.example.clinicaveterinaria.ui.admin

import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.model.Profesional
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private fun formatDateMillis(millis: Long?): String {
    if (millis == null) return ""
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("UTC")
    return sdf.format(Date(millis))
}

private fun parseDateToMillis(dateStr: String): Long? {
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        sdf.parse(dateStr)?.time
    } catch (_: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModificarProfesionalScreen(
    profesional: Profesional,
    onGuardar: (Profesional) -> Unit,
    onCancelar: () -> Unit,
    onEliminar: (Profesional) -> Unit
) {
    var rut by rememberSaveable { mutableStateOf(profesional.rut) }
    var nombres by rememberSaveable { mutableStateOf(profesional.nombres) }
    var apellidos by rememberSaveable { mutableStateOf(profesional.apellidos) }
    var genero by rememberSaveable { mutableStateOf(profesional.genero) }
    var fechaNac by rememberSaveable { mutableStateOf(profesional.fechaNacimiento) }
    var especialidad by rememberSaveable { mutableStateOf(profesional.especialidad) }
    var email by rememberSaveable { mutableStateOf(profesional.email) }
    var telefono by rememberSaveable { mutableStateOf(profesional.telefono) }
    var password by rememberSaveable { mutableStateOf(profesional.password) }
    var showPass by rememberSaveable { mutableStateOf(false) }

    val nombresOk = nombres.trim().isNotEmpty()
    val apellidosOk = apellidos.trim().isNotEmpty()
    val generoOk = genero.isNotEmpty()
    val fechaOk = Regex("""\d{4}-\d{2}-\d{2}""").matches(fechaNac)
    val especialidadOk = especialidad.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val telefonoOk = telefono.trim().length in 8..12 && telefono.all { it.isDigit() }
    val passwordOk = password.length in 4..20

    val formOk = nombresOk && apellidosOk && generoOk && fechaOk &&
            especialidadOk && emailOk && telefonoOk && passwordOk

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        unfocusedBorderColor = colorPrincipal,
        focusedBorderColor = colorPrincipal,
        focusedLabelColor = colorPrincipal,
        disabledContainerColor = Color(0xFFEEEEEE),
        disabledBorderColor = Color.Gray,
        disabledLabelColor = Color.Gray
    )

    val datePickerColors = DatePickerDefaults.colors(
        containerColor = colorFondoCampo,
        selectedDayContainerColor = colorPrincipal,
        todayDateBorderColor = colorPrincipal,
        todayContentColor = colorPrincipal
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar profesional") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancelar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { inner ->
        var openGenero by remember { mutableStateOf(false) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Clínica",
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.6f)
                )
            }

            item {
                OutlinedTextField(
                    value = rut,
                    onValueChange = {},
                    label = { Text("RUT (no editable)") },
                    singleLine = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                OutlinedTextField(
                    value = nombres, onValueChange = { nombres = it },
                    label = { Text("Nombres") }, singleLine = true,
                    isError = !nombresOk && nombres.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
            item {
                OutlinedTextField(
                    value = apellidos, onValueChange = { apellidos = it },
                    label = { Text("Apellidos") }, singleLine = true,
                    isError = !apellidosOk && apellidos.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            item {
                ExposedDropdownMenuBox(
                    expanded = openGenero,
                    onExpandedChange = { openGenero = it }
                ) {
                    OutlinedTextField(
                        value = genero,
                        onValueChange = {},
                        label = { Text("Género") },
                        readOnly = true,
                        isError = !generoOk && genero.isNotEmpty(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = openGenero) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = fieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = openGenero,
                        onDismissRequest = { openGenero = false }
                    ) {
                        listOf("Masculino", "Femenino", "Otro").forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    genero = it
                                    openGenero = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                var showDatePicker by remember { mutableStateOf(false) }
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = parseDateToMillis(fechaNac)
                )

                Box {
                    OutlinedTextField(
                        value = fechaNac,
                        onValueChange = { /* no editable manual */ },
                        label = { Text("Fecha de nacimiento") },
                        singleLine = true,
                        readOnly = true,
                        isError = !fechaOk && fechaNac.isNotEmpty(),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Outlined.DateRange, contentDescription = "Elegir fecha")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = fieldColors
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
                                },
                                colors = ButtonDefaults.textButtonColors(contentColor = colorPrincipal)
                            ) { Text("Aceptar") }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showDatePicker = false },
                                colors = ButtonDefaults.textButtonColors(contentColor = colorPrincipal)
                            ) { Text("Cancelar") }
                        }
                    ) {
                        DatePicker(
                            state = datePickerState,
                            showModeToggle = true,
                            colors = datePickerColors
                        )
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = especialidad, onValueChange = { especialidad = it },
                    label = { Text("Especialidad") }, singleLine = true,
                    isError = !especialidadOk && especialidad.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
            item {
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    label = { Text("Email") }, singleLine = true,
                    isError = !emailOk && email.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }
            item {
                OutlinedTextField(
                    value = telefono, onValueChange = { telefono = it.filter(Char::isDigit) },
                    label = { Text("Teléfono") }, singleLine = true,
                    isError = !telefonoOk && telefono.isNotEmpty(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = !passwordOk && password.isNotEmpty(),
                    supportingText = {
                        if (!passwordOk && password.isNotEmpty()) Text("4 a 20 caracteres")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal))
                    ) { Text("Cancelar") }

                    OutlinedButton(
                        onClick = { onEliminar(profesional) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(MaterialTheme.colorScheme.error)
                        )
                    ) { Text("Eliminar") }

                    Button(
                        onClick = {
                            onGuardar(
                                Profesional(
                                    rut = rut,
                                    nombres = nombres.trim(),
                                    apellidos = apellidos.trim(),
                                    genero = genero,
                                    fechaNacimiento = fechaNac,
                                    especialidad = especialidad.trim(),
                                    email = email.trim(),
                                    telefono = telefono.trim(),
                                    password = password.trim()
                                )
                            )
                        },
                        enabled = formOk,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                    ) { Text("Guardar") }
                }
            }
        }
    }
}
