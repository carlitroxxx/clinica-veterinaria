package com.example.clinicaveterinaria.ui.cliente

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository

//Modelo de formulario
data class MascotaForm(
    val clienteRut: String,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val sexo: String?,
    val fechaNacimiento: String?
)

@Composable
fun CrearMascotaRoute(
    nav: NavHostController,
    clienteRut: String,
    onGuardarMascota: (MascotaForm) -> Unit = { }
) {
    CrearMascotaScreen(
        clienteRut = clienteRut,
        onGuardar = { form ->
            val res = Repository.agregarMascota(form)
            if (res.ok) {
                //flujo simple: volver al home del cliente (o a Mis Reservas si prefieres)
                nav.navigate("clienteProfesionales") {
                    popUpTo("clienteProfesionales") { inclusive = true }
                }
            } else {
                // alternativa minimalista: quedarte aquí y (si quieres) mostrar un error con Snackbar externo
                // por simplicidad, solo volvemos:
                // nav.popBackStack()
            }
        },
        onCancelar = { nav.popBackStack() }
    )
}


//Pantalla principal
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMascotaScreen(
    clienteRut: String,
    onGuardar: (MascotaForm) -> Unit,
    onCancelar: () -> Unit
) {
    var rut by rememberSaveable { mutableStateOf(clienteRut) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    var fechaNac by rememberSaveable { mutableStateOf("") }

    val especies = listOf("Perro", "Gato", "Ave", "Conejo", "Reptil", "Otro")
    val sexos = listOf("Macho", "Hembra", "Desconocido")

    // Validaciones mínimas
    val nombreOk = nombre.trim().isNotEmpty()
    val especieOk = especie.trim().isNotEmpty()
    val fechaOk = fechaNac.isBlank() || Regex("""^\d{4}-\d{2}-\d{2}$""").matches(fechaNac)
    val formOk = nombreOk && especieOk && fechaOk

    // Para dropdowns
    var expEspecie by rememberSaveable { mutableStateOf(false) }
    var expSexo by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        contentWindowInsets = WindowInsets(0)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth()) {
                    Text("Registrar Mascota", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider()
                }
            }

            // RUT del cliente (solo lectura)
            item {
                OutlinedTextField(
                    value = rut,
                    onValueChange = { /* readOnly visual */ },
                    label = { Text("RUT Cliente") },
                    singleLine = true,
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Nombre
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la mascota") },
                    singleLine = true,
                    isError = nombre.isNotEmpty() && !nombreOk,
                    supportingText = {
                        if (nombre.isNotEmpty() && !nombreOk) Text("Ingresa el nombre")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Especie
            item {
                ExposedDropdownMenuBox(
                    expanded = expEspecie,
                    onExpandedChange = { expEspecie = !expEspecie }
                ) {
                    OutlinedTextField(
                        value = especie,
                        onValueChange = { especie = it },
                        label = { Text("Especie") },
                        singleLine = true,
                        readOnly = true,
                        isError = especie.isNotEmpty() && !especieOk,
                        supportingText = {
                            if (especie.isNotEmpty() && !especieOk) Text("Selecciona una especie")
                        },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expEspecie) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expEspecie,
                        onDismissRequest = { expEspecie = false }
                    ) {
                        especies.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    especie = opt
                                    expEspecie = false
                                }
                            )
                        }
                    }
                }
            }

            // Raza (opcional)
            item {
                OutlinedTextField(
                    value = raza,
                    onValueChange = { raza = it },
                    label = { Text("Raza (opcional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Sexo
            item {
                ExposedDropdownMenuBox(
                    expanded = expSexo,
                    onExpandedChange = { expSexo = !expSexo }
                ) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = { sexo = it },
                        label = { Text("Sexo (opcional)") },
                        singleLine = true,
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expSexo) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expSexo,
                        onDismissRequest = { expSexo = false }
                    ) {
                        sexos.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    sexo = opt
                                    expSexo = false
                                }
                            )
                        }
                    }
                }
            }

            // Fecha de nacimiento (opcional, formato AAAA-MM-DD)
            item {
                OutlinedTextField(
                    value = fechaNac,
                    onValueChange = { fechaNac = it },
                    label = { Text("Fecha de nacimiento (opcional)") },
                    placeholder = { Text("AAAA-MM-DD") },
                    singleLine = true,
                    isError = fechaNac.isNotEmpty() && !fechaOk,
                    supportingText = {
                        if (fechaNac.isNotEmpty() && !fechaOk) Text("Usa el formato AAAA-MM-DD")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Botones
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Button(
                        onClick = {
                            onGuardar(
                                MascotaForm(
                                    clienteRut = rut.trim(),
                                    nombre = nombre.trim(),
                                    especie = especie.trim(),
                                    raza = raza.trim().ifEmpty { null },
                                    sexo = sexo.trim().ifEmpty { null },
                                    fechaNacimiento = fechaNac.trim().ifEmpty { null }
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
