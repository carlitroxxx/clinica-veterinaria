package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilClienteScreen(
    mockNombre: String,
    mockEmail: String,
    mockTelefono: String,
    onChangePasswordClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    var isEditing by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf(mockNombre) }
    var email by remember { mutableStateOf(mockEmail) }
    var telefono by remember { mutableStateOf(mockTelefono) }

    fun cancelEditing() {
        nombre = mockNombre
        email = mockEmail
        telefono = mockTelefono
        isEditing = false
    }

    fun saveEditing() {
        isEditing = false
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
        disabledLeadingIconColor = colorPrincipal.copy(alpha = 0.75f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { saveEditing() }) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Guardar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        IconButton(onClick = { cancelEditing() }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Cancelar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
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
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(colorFondoCampo),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar de Perfil",
                    tint = colorPrincipal,
                    modifier = Modifier.size(80.dp)
                )
            }
            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = email,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre Completo") },
                    readOnly = !isEditing,
                    leadingIcon = { Icon(Icons.Default.Person, "Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    readOnly = !isEditing,
                    leadingIcon = { Icon(Icons.Default.Email, "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    label = { Text("Teléfono") },
                    readOnly = !isEditing,
                    leadingIcon = { Icon(Icons.Default.Phone, "Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider() // Separador
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onChangePasswordClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal))
            ) {
                Icon(Icons.Default.Lock, contentDescription = "Contraseña", modifier = Modifier.padding(end = 8.dp))
                Text("Cambiar contraseña")
            }

            Button(
                onClick = onLogoutClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión", modifier = Modifier.padding(end = 8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}