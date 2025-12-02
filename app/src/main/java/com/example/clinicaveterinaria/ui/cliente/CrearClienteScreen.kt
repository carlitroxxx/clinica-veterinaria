package com.example.clinicaveterinaria.ui.cliente

import android.annotation.SuppressLint
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
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
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.util.RutUtils
import kotlinx.coroutines.launch

@Composable
fun CrearClienteRoute(nav: NavHostController) {
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    CrearClienteScreen(
        onGuardar = { c ->
            scope.launch {
                error = null
                // AQUÍ EL CAMBIO: usar agregarCliente
                val res = Repository.agregarCliente(c)
                if (res.ok) {
                    // Cliente creado en el backend → vamos a registrar la mascota
                    nav.navigate("clienteAgregarMascota/${c.rut}") {
                        popUpTo("login") { inclusive = true }
                    }
                } else {
                    error = res.mensaje ?: "No se pudo crear el cliente"
                }
            }
        },
        onCancelar = { nav.popBackStack() },
        errorMessage = error
    )
}

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
    var showPass by rememberSaveable { mutableStateOf(false) }
    var showPass2 by rememberSaveable { mutableStateOf(false) }

    // Validaciones
    val rutOk = RutUtils.rutEsValido(rut)
    val nombresOk = nombres.trim().isNotEmpty()
    val apellidosOk = apellidos.trim().isNotEmpty()
    val emailOk = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val telefonoOk = telefono.trim().length in 8..12 && telefono.all { it.isDigit() }
    val passwordOk = password.length in 4..20
    val passCoincide = password2.isNotEmpty() && password2 == password

    val formOk = rutOk && nombresOk && apellidosOk && emailOk && telefonoOk && passwordOk && passCoincide

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        unfocusedBorderColor = colorPrincipal,
        focusedBorderColor = colorPrincipal,
        focusedLabelColor = colorPrincipal
    )

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Crear cuenta de cliente") },
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
        },
        contentWindowInsets = WindowInsets(0)
    ){ innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .imePadding(),
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
                    onValueChange = { rut = it },
                    label = { Text("RUT") },
                    singleLine = true,
                    isError = rut.isNotEmpty() && !rutOk,
                    supportingText = {
                        if (rut.isNotEmpty() && !rutOk) Text("RUT no válido")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass = !showPass }) {
                            Icon(
                                imageVector = if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    isError = password.isNotEmpty() && !passwordOk,
                    supportingText = {
                        if (password.isNotEmpty() && !passwordOk) Text("4 a 20 caracteres")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            item {
                OutlinedTextField(
                    value = password2,
                    onValueChange = { password2 = it },
                    label = { Text("Confirmar contraseña") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = if (showPass2) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showPass2 = !showPass2 }) {
                            Icon(
                                imageVector = if (showPass2) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPass2) "Ocultar" else "Mostrar"
                            )
                        }
                    },
                    isError = password2.isNotEmpty() && !passCoincide,
                    supportingText = {
                        if (password2.isNotEmpty() && !passCoincide) Text("Las contraseñas no coinciden")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
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
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(
                            colorPrincipal
                        )
                        )
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
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                    ) { Text("Crear cuenta") }
                }
            }

        }
    }
}