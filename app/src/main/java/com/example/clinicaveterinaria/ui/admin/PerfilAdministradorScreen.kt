package com.example.clinicaveterinaria.ui.admin

import android.text.format.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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


    val totalProfesionales = Repository.profesionales.size

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // ---------- Avatar + título ----------
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo empresa",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color(0xFFE0F7F7))
                .padding(16.dp)
        )
        Text("Administrador", fontSize = 22.sp, color = Color(0xFF00AAB0), fontWeight = FontWeight.SemiBold)

        // ---------- Chip de rol ----------
        AssistChip(
            onClick = { },
            label = { Text("Rol: Admin") },
            enabled = false
        )

        // ---------- Email (solo lectura) ----------
        OutlinedTextField(
            value = email,
            onValueChange = {},
            label = { Text("Correo") },
            enabled = false,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = Color(0xFF00AAB0),
                disabledLabelColor = Color(0xFF00AAB0),
                disabledTextColor = Color.Black
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF2FBFB)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Column(
                    Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text("Cantidad de Profesionales", style = MaterialTheme.typography.labelMedium, color = Color(0xFF007D82))
                    Text(totalProfesionales
.toString(), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(18.dp))

        // ---------- Cerrar sesión ----------
        Button(
            onClick = {
                SesionManager.cerrarSesion(context)
                nav.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AAB0),
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) { Text("Cerrar sesión", fontSize = 18.sp) }
    }
}

