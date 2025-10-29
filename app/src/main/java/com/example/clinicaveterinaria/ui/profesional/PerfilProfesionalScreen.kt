package com.example.clinicaveterinaria.ui.profesional


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.saveable.rememberSaveable
data class PerfilUi(
    val nombre: String,
    val email: String,
    val telefono: String,
    val especialidad: String? = null,
    val bio: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilProfesionalScreen(
    initial: PerfilUi,
    onBack: () -> Unit = {},
    onGuardar: (PerfilUi) -> Unit = { },
    onCambiarPassword: (String) -> Unit = { _ -> },
    onLogout: () -> Unit = {}
) {
    var editando by rememberSaveable { mutableStateOf(false) }

    // Copias editables (estado local)
    var nombre by rememberSaveable { mutableStateOf(initial.nombre) }
    var email by rememberSaveable { mutableStateOf(initial.email) }
    var telefono by rememberSaveable { mutableStateOf(initial.telefono) }
    var especialidad by rememberSaveable { mutableStateOf(initial.especialidad ?: "") }
    var bio by rememberSaveable { mutableStateOf(initial.bio ?: "") }

    // Para cambiar contraseña (diálogo)
    var showPassDialog by remember { mutableStateOf(false) }
    var nuevaPass by rememberSaveable { mutableStateOf("") }

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
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (!editando) {
                        IconButton(onClick = { editando = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Editar")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues) // ✅ Usa el PaddingValues del Scaffold
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
                text = nombre.ifBlank { "Nombre no definido" },
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
                    CampoPerfil(
                        label = "Nombre",
                        value = nombre,
                        onValueChange = { nombre = it },
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
                    CampoPerfil(
                        label = "Bio",
                        value = bio,
                        onValueChange = { bio = it },
                        enabled = editando,
                        singleLine = false,
                        minLines = 3,
                        placeholder = "Cuéntanos algo sobre ti"
                    )
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
                            // Deshacer cambios locales
                            nombre = initial.nombre
                            email = initial.email
                            telefono = initial.telefono
                            especialidad = initial.especialidad ?: ""
                            bio = initial.bio ?: ""
                            editando = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancelar") }

                    Spacer(Modifier.width(12.dp))

                    Button(
                        onClick = {
                            onGuardar(
                                PerfilUi(
                                    nombre = nombre,
                                    email = email,
                                    telefono = telefono,
                                    especialidad = especialidad.ifBlank { null },
                                    bio = bio.ifBlank { null }
                                )
                            )
                            editando = false
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) { Text("Guardar") }
                }
            } else {
                Column(Modifier.fillMaxWidth()) {
                    AccionPerfil(
                        texto = "Cambiar contraseña",
                        icon = Icons.Default.Edit
                    ) { showPassDialog = true }

                    Spacer(Modifier.height(8.dp))

                    AccionPerfil(
                        texto = "Cerrar sesión",
                        icon = Icons.Default.Logout
                    ) { onLogout() }
                }
            }
        }
    }

    if (showPassDialog) {
        AlertDialog(
            onDismissRequest = { showPassDialog = false },
            title = { Text("Cambiar contraseña") },
            text = {
                OutlinedTextField(
                    value = nuevaPass,
                    onValueChange = { nuevaPass = it },
                    label = { Text("Nueva contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onCambiarPassword(nuevaPass)
                    nuevaPass = ""
                    showPassDialog = false
                }) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showPassDialog = false }) { Text("Cancelar") }
            }
        )
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

@Composable
private fun AccionPerfil(
    texto: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Text(texto, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PerfilProfesionalScreenPreview() {
    MaterialTheme {
        PerfilProfesionalScreen(
            initial = PerfilUi(
                nombre = "Dra. Camila Soto",
                email = "camila.soto@clinicavet.cl",
                telefono = "+56 9 1234 5678",
                especialidad = "Medicina Felina",
                bio = "Apasionada por la salud felina y la medicina preventiva."
            ),
            onGuardar = { /* mock */ },
            onCambiarPassword = { /* mock */ },
            onLogout = { /* mock */ }
        )
    }
}