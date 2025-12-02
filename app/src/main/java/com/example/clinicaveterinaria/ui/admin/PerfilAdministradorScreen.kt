package com.example.clinicaveterinaria.ui.admin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilAdministradorScreen(nav: NavHostController) {
    val context = LocalContext.current
    val email = SesionManager.obtenerEmail(context) ?: "admin@correo.cl"

    // AHORA: se obtiene desde el backend (Repository.obtenerProfesionales())
    var totalProfesionales by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        try {
            val lista = Repository.obtenerProfesionales()
            totalProfesionales = lista.size
        } catch (_: Exception) {
            totalProfesionales = 0
        }
    }

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoClaro = Color(0xFFF7FCFC)
    val colorAvatarBg = Color(0xFFE0F7F7)
    val colorTextoCard = Color(0xFF007D82)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil Administrador") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = {
                        SesionManager.cerrarSesion(context)
                        nav.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo empresa",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(colorAvatarBg)
                    .padding(16.dp)
            )

            Text(
                "Administrador",
                fontSize = 22.sp,
                color = colorPrincipal,
                fontWeight = FontWeight.SemiBold
            )

            AssistChip(
                onClick = { },
                label = { Text("Rol: Admin") },
                colors = AssistChipDefaults.assistChipColors(
                    labelColor = colorPrincipal,
                    containerColor = colorFondoClaro
                ),
                border = BorderStroke(1.dp, colorPrincipal)
            )

            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Correo") },
                enabled = false,
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = colorPrincipal,
                    disabledLabelColor = colorPrincipal,
                    disabledTextColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = colorFondoClaro),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            "Cantidad de Profesionales",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorTextoCard
                        )
                        Text(
                            totalProfesionales.toString(),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}
