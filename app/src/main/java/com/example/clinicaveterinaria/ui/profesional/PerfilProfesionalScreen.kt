package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Profesional
import kotlinx.coroutines.launch

data class PerfilUi(
    val nombres: String,
    val apellidos: String,
    val email: String,
    val telefono: String,
    val especialidad: String? = null,
    val password: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilProfesionalScreen(nav: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val emailSesion = remember { SesionManager.obtenerEmail(context) }
    val tipoSesion = remember { SesionManager.obtenerTipo(context) }

    LaunchedEffect(emailSesion, tipoSesion) {
        if (emailSesion == null || tipoSesion != "profesional") {
            nav.navigate("login") { popUpTo(0) { inclusive = true } }
        }
    }

    var profesional by remember { mutableStateOf<Profesional?>(null) }
    var cargando by remember { mutableStateOf(true) }
    var errorCarga by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(emailSesion) {
        if (emailSesion.isNullOrBlank()) {
            cargando = false
            return@LaunchedEffect
        }
        cargando = true
        errorCarga = null
        profesional = Repository.obtenerProfesionalPorEmail(emailSesion)
        if (profesional == null) {
            errorCarga = "No se pudo cargar el perfil del profesional."
        }
        cargando = false
    }

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        unfocusedBorderColor = colorPrincipal,
        focusedBorderColor = colorPrincipal,
        focusedLabelColor = colorPrincipal,
        disabledContainerColor = colorFondoCampo,
        disabledBorderColor = colorPrincipal.copy(alpha = 0.75f),
        disabledLabelColor = colorPrincipal.copy(alpha = 0.75f),
        disabledTextColor = MaterialTheme.colorScheme.onSurface
    )

    var editando by rememberSaveable { mutableStateOf(false) }
    var nombres by rememberSaveable { mutableStateOf("") }
    var apellidos by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var especialidad by rememberSaveable { mutableStateOf("") }

    var pass by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }
    var showPass by rememberSaveable { mutableStateOf(false) }

    var mensajeErrorUpdate by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profesional?.rut) {
        profesional?.let { p ->
            nombres = p.nombres
            apellidos = p.apellidos
            email = p.email
            telefono = p.telefono
            especialidad = p.especialidad
            pass = ""
            pass2 = ""
            editando = false
            mensajeErrorUpdate = null
        }
    }

    val passTouched = pass.isNotEmpty() || pass2.isNotEmpty()
    val passLenOk = (!passTouched) || (pass.length >= 4 && pass2.length >= 4)
    val passMatchOk = (!passTouched) || (pass == pass2)
    val passValid = passLenOk && passMatchOk

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (editando) "Editar perfil" else "Mi Perfil",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { nav.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!editando && !cargando && profesional != null) {
                        TextButton(
                            onClick = { editando = true },
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) { Text("Editar") }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->

        when {
            cargando -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorPrincipal)
                }
            }

            profesional == null -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = errorCarga ?: "No se encontró información del profesional.",
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            SesionManager.cerrarSesion(context)
                            nav.navigate("login") { popUpTo(0) { inclusive = true } }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                    ) {
                        Text("Volver a iniciar sesión")
                    }
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(8.dp))

                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(48.dp))
                            .background(colorFondoCampo),
                        tint = colorPrincipal
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "${nombres.ifBlank { "Nombre" }} ${apellidos.ifBlank { "Apellido" }}".trim(),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        text = email.ifBlank { "email@ejemplo.com" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = colorFondoCampo)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            CampoPerfil(
                                label = "Nombres",
                                value = nombres,
                                onValueChange = { nombres = it },
                                enabled = editando,
                                colors = fieldColors
                            )
                            CampoPerfil(
                                label = "Apellidos",
                                value = apellidos,
                                onValueChange = { apellidos = it },
                                enabled = editando,
                                colors = fieldColors
                            )
                            CampoPerfil(
                                label = "Email",
                                value = email,
                                onValueChange = { email = it },
                                enabled = editando,
                                keyboardType = KeyboardType.Email,
                                colors = fieldColors
                            )
                            CampoPerfil(
                                label = "Teléfono",
                                value = telefono,
                                onValueChange = { telefono = it },
                                enabled = editando,
                                keyboardType = KeyboardType.Phone,
                                colors = fieldColors
                            )
                            CampoPerfil(
                                label = "Especialidad",
                                value = especialidad,
                                onValueChange = { especialidad = it },
                                enabled = editando,
                                placeholder = "Opcional",
                                colors = fieldColors
                            )

                            if (editando) {
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = pass,
                                    onValueChange = { pass = it },
                                    label = { Text("Contraseña") },
                                    singleLine = true,
                                    visualTransformation = if (showPass) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                                    trailingIcon = {
                                        IconButton(onClick = { showPass = !showPass }) {
                                            Icon(
                                                imageVector = if (showPass)
                                                    Icons.Filled.VisibilityOff
                                                else Icons.Filled.Visibility,
                                                contentDescription = if (showPass) "Ocultar" else "Mostrar"
                                            )
                                        }
                                    },
                                    isError = !passLenOk,
                                    supportingText = {
                                        if (!passLenOk) Text("Mínimo 4 caracteres")
                                        else Text("Deja vacío para no cambiar")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = fieldColors
                                )
                                Spacer(Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = pass2,
                                    onValueChange = { pass2 = it },
                                    label = { Text("Confirmar contraseña") },
                                    singleLine = true,
                                    visualTransformation = if (showPass) VisualTransformation.None
                                    else PasswordVisualTransformation(),
                                    isError = !passMatchOk,
                                    supportingText = {
                                        if (!passMatchOk) Text("Las contraseñas no coinciden")
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = fieldColors
                                )
                            }
                        }
                    }

                    mensajeErrorUpdate?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (editando) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = {
                                    profesional?.let { p ->
                                        nombres = p.nombres
                                        apellidos = p.apellidos
                                        email = p.email
                                        telefono = p.telefono
                                        especialidad = p.especialidad
                                    }
                                    pass = ""
                                    pass2 = ""
                                    mensajeErrorUpdate = null
                                    editando = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal))
                            ) { Text("Cancelar") }

                            Spacer(Modifier.width(12.dp))

                            Button(
                                onClick = {
                                    val profActual = profesional ?: return@Button

                                    val actualizado = profActual.copy(
                                        nombres = nombres.trim(),
                                        apellidos = apellidos.trim(),
                                        email = email.trim(),
                                        telefono = telefono.trim(),
                                        especialidad = especialidad.trim().ifEmpty { profActual.especialidad },
                                        password = if (passTouched) pass else profActual.password
                                    )

                                    scope.launch {
                                        val res = Repository.actualizarProfesional(actualizado)
                                        if (res.ok && res.data != null) {
                                            profesional = res.data
                                            mensajeErrorUpdate = null

                                            val tokenActual = SesionManager.obtenerToken(context)
                                            if (tokenActual != null &&
                                                !actualizado.email.equals(emailSesion ?: "", ignoreCase = true)
                                            ) {
                                                SesionManager.iniciarSesion(
                                                    context,
                                                    actualizado.email,
                                                    "profesional",
                                                    tokenActual
                                                )
                                            }

                                            pass = ""
                                            pass2 = ""
                                            editando = false
                                        } else {
                                            mensajeErrorUpdate = res.mensaje ?: "No se pudo actualizar el perfil."
                                        }
                                    }
                                },
                                enabled = passValid,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                            ) {
                                Text("Guardar")
                            }
                        }
                    } else {
                        Column(Modifier.fillMaxWidth()) {
                            Spacer(Modifier.height(8.dp))
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                onClick = {
                                    SesionManager.cerrarSesion(context)
                                    nav.navigate("login") { popUpTo(0) { inclusive = true } }
                                }
                            ) {
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Filled.Logout,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Text(
                                        "Cerrar sesión",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CampoPerfil(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
) {
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        singleLine = singleLine,
        minLines = minLines,
        modifier = Modifier.fillMaxWidth(),
        placeholder = placeholder?.let { { Text(it) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = colors
    )
}
