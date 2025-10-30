package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager
import com.example.clinicaveterinaria.model.Profesional

// ---------- UI DATA (si quieres seguir usando esta clase en callbacks externos) ----------
data class PerfilUi(
    val nombres: String,
    val apellidos: String,
    val email: String,
    val telefono: String,
    val especialidad: String? = null,
    val password: String? = null // null = no cambiar
)

// ======================================================================
//  PERFIL PROFESIONAL: TODO EN UN ARCHIVO (Route + Screen + Callbacks)
//  Con campos separados: Nombres y Apellidos
// ======================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilProfesionalScreen(nav: NavHostController) {
    // -------- Sesión / carga de datos --------
    val context = LocalContext.current
    val emailSesion = remember { SesionManager.obtenerEmail(context) }
    val tipoSesion = remember { SesionManager.obtenerTipo(context) }

    // Sin sesión o no profesional → login
    LaunchedEffect(emailSesion, tipoSesion) {
        if (emailSesion == null || tipoSesion != "profesional") {
            nav.navigate("login") { popUpTo(0) { inclusive = true } }
        }
    }

    // Busca profesional por email de sesión (en memoria)
    val prof: Profesional = remember(Repository.profesionales, emailSesion) {
        Repository.profesionales.firstOrNull { it.email.equals(emailSesion ?: "", ignoreCase = true) }
    } ?: run {
        LaunchedEffect(Unit) {
            SesionManager.cerrarSesion(context)
            nav.navigate("login") { popUpTo(0) { inclusive = true } }
        }
        return
    }

    // -------- Estado de edición --------
    var editando by rememberSaveable { mutableStateOf(false) }

    // Copias editables (estado local) — ahora separados
    var nombres by rememberSaveable { mutableStateOf(prof.nombres) }
    var apellidos by rememberSaveable { mutableStateOf(prof.apellidos) }
    var email by rememberSaveable { mutableStateOf(prof.email) }
    var telefono by rememberSaveable { mutableStateOf(prof.telefono) }
    var especialidad by rememberSaveable { mutableStateOf(prof.especialidad) }

    // Contraseña (solo si se edita)
    var pass by rememberSaveable { mutableStateOf("") }
    var pass2 by rememberSaveable { mutableStateOf("") }

    // Reglas: si escribes algo en una, ambas deben cumplir (>= 4 y coincidir)
    val passTouched = pass.isNotEmpty() || pass2.isNotEmpty()
    val passLenOk = (!passTouched) || (pass.length >= 4 && pass2.length >= 4)
    val passMatchOk = (!passTouched) || (pass == pass2)
    val passValid = passLenOk && passMatchOk

    // --------- UI principal (mismo diseño) ---------
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (editando) "Editar perfil" else "Perfil",
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
                    if (!editando) {
                        TextButton(onClick = { editando = true }) { Text("Editar") }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            // Avatar
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(48.dp))
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

            // Card de información
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    // Nombres y Apellidos separados
                    CampoPerfil(
                        label = "Nombres",
                        value = nombres,
                        onValueChange = { nombres = it },
                        enabled = editando
                    )
                    CampoPerfil(
                        label = "Apellidos",
                        value = apellidos,
                        onValueChange = { apellidos = it },
                        enabled = editando
                    )
                    CampoPerfil(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it },
                        enabled = editando
                    )
                    CampoPerfil(
                        label = "Teléfono",
                        value = telefono,
                        onValueChange = { telefono = it },
                        enabled = editando
                    )
                    CampoPerfil(
                        label = "Especialidad",
                        value = especialidad,
                        onValueChange = { especialidad = it },
                        enabled = editando,
                        placeholder = "Opcional"
                    )

                    // 🔐 Contraseña y Confirmar contraseña (solo en modo editar)
                    if (editando) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pass,
                            onValueChange = { pass = it },
                            label = { Text("Contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = !passLenOk,
                            supportingText = {
                                if (!passLenOk) Text("Mínimo 4 caracteres")
                                else Text("Deja vacío para no cambiar")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            value = pass2,
                            onValueChange = { pass2 = it },
                            label = { Text("Confirmar contraseña") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            isError = !passMatchOk,
                            supportingText = {
                                if (!passMatchOk) Text("Las contraseñas no coinciden")
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botones de acción
            if (editando) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = {
                            // Restablecer a los valores del profesional cargado
                            nombres = prof.nombres
                            apellidos = prof.apellidos
                            email = prof.email
                            telefono = prof.telefono
                            especialidad = prof.especialidad
                            pass = ""
                            pass2 = ""
                            editando = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = {
                            // Persistir cambios: actualizar Profesional en BD
                            val actualizado: Profesional = prof.copy(
                                nombres = nombres.trim(),
                                apellidos = apellidos.trim(),
                                email = email.trim(),
                                telefono = telefono.trim(),
                                especialidad = especialidad.trim().ifEmpty { prof.especialidad },
                                password = if (passTouched) pass else prof.password
                            )
                            Repository.actualizarProfesional(actualizado)

                            // Si cambió el email, refrescamos sesión
                            if (!actualizado.email.equals(emailSesion ?: "", ignoreCase = true)) {
                                SesionManager.iniciarSesion(context, actualizado.email, "profesional")
                            }

                            pass = ""
                            pass2 = ""
                            editando = false
                        },
                        enabled = passValid,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }
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
                            Icon(Icons.Filled.Logout, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text("Cerrar sesión", style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }
}

// ---------- Helpers de UI ----------
@Composable
private fun CampoPerfil(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    singleLine: Boolean = true,
    minLines: Int = 1,
    placeholder: String? = null
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
        placeholder = placeholder?.let { { Text(it) } }
    )
}
