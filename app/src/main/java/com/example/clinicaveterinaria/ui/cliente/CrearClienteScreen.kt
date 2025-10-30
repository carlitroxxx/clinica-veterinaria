package com.example.clinicaveterinaria.ui.cliente

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.util.RutUtils


@Composable
fun CrearClienteRoute(nav: NavHostController) {
    val ctx = androidx.compose.ui.platform.LocalContext.current
    var error by remember { mutableStateOf<String?>(null) }

    CrearClienteScreen(
        onGuardar = { c ->
            val res = Repository.agregarCliente(c)
            if (res.ok) {
                SesionManager.iniciarSesion(ctx, c.email, "cliente")
                nav.navigate("clienteAgregarMascota/${c.rut}") {
                    popUpTo("login") { inclusive = true }
                }
            } else {
                error = res.mensaje ?: "No se pudo guardar"
            }
        },
        onCancelar = { nav.popBackStack() },
        errorMessage = error           // 👈 pasar a la pantalla
    )
}


// Pantalla de Crear Cliente
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearClienteScreen(
    onGuardar: (Cliente) -> Unit,
    onCancelar: () -> Unit,
    errorMessage: String? = null
) {
    var rut by rememberSaveable { mutableStateOf("") }
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var password2 by rememberSaveable { mutableStateOf("") }

    // Validaciones
    val rutOk = RutUtils.rutEsValido(rut)
    val nombresOk = nombres.trim().isNotEmpty()
    val apellidosOk = apellidos.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val telefonoOk = telefono.trim().length in 8..12 && telefono.all { it.isDigit() }
    val passwordOk = password.length in 4..20
    val passCoincide = password2.isNotEmpty() && password2 == password

    val formOk = rutOk && nombresOk && apellidosOk && emailOk && telefonoOk && passwordOk && passCoincide

    Scaffold (contentWindowInsets = WindowInsets(0)){
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 8.dp)
        ) {
            item {
                Column(Modifier.fillMaxWidth().padding(top = 5.dp)) {
                    Text("Crear cuenta de cliente", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(6.dp))
                    HorizontalDivider()
                }
            }

            item {
                OutlinedTextField(
                    value = rut,
                    onValueChange = { rut = it },
                    label = { Text("RUT") },
                    singleLine = true,
                    isError = rut.isNotEmpty() && !rutOk,
                    supportingText = {
                        if (rut.isNotEmpty() && !rutOk) Text("RUT no válido")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = nombres,
                    onValueChange = { nombres = it },
                    label = { Text("Nombres") },
                    singleLine = true,
                    isError = nombres.isNotEmpty() && !nombresOk,
                    supportingText = {
                        if (nombres.isNotEmpty() && !nombresOk) Text("Ingresa tus nombres")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = apellidos,
                    onValueChange = { apellidos = it },
                    label = { Text("Apellidos") },
                    singleLine = true,
                    isError = apellidos.isNotEmpty() && !apellidosOk,
                    supportingText = {
                        if (apellidos.isNotEmpty() && !apellidosOk) Text("Ingresa tus apellidos")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    isError = email.isNotEmpty() && !emailOk,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    supportingText = {
                        if (email.isNotEmpty() && !emailOk) Text("Email no válido")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it.filter(Char::isDigit) },
                    label = { Text("Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    isError = telefono.isNotEmpty() && !telefonoOk,
                    supportingText = {
                        if (telefono.isNotEmpty() && !telefonoOk) Text("Usa de 8 a 12 dígitos")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = password.isNotEmpty() && !passwordOk,
                    supportingText = {
                        if (password.isNotEmpty() && !passwordOk) Text("4 a 20 caracteres")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    isError = password2.isNotEmpty() && !passCoincide,
                    supportingText = {
                        if (password2.isNotEmpty() && !passCoincide) Text("Las contraseñas no coinciden")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
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
                                Cliente(
                                    rut = rut.trim(),
                                    nombres = nombres.trim(),
                                    apellidos = apellidos.trim(),
                                    email = email.trim(),
                                    telefono = telefono.trim(),
                                    contrasena = password.trim()
                                )
                            )
                        },
                        enabled = formOk,
                        modifier = Modifier.weight(1f)
                    ) { Text("Crear cuenta") }
                }
            }

        }
    }
}
