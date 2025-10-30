package com.example.clinicaveterinaria.ui.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.SesionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavHostController, context: android.content.Context) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ----------- LOGO -----------
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 32.dp)
        )

        // ----------- CORREO -----------
        OutlinedTextField(
            value = correo,
            onValueChange = { correo = it },
            label = { Text("Correo electrónico") },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Email
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7FCFC),
                unfocusedBorderColor = Color(0xFF00AAB0),
                focusedBorderColor = Color(0xFF00AAB0),
                focusedLabelColor = Color(0xFF00AAB0)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ----------- CONTRASEÑA -----------
        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7FCFC),
                unfocusedBorderColor = Color(0xFF00AAB0),
                focusedBorderColor = Color(0xFF00AAB0),
                focusedLabelColor = Color(0xFF00AAB0)
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ----------- BOTÓN INGRESAR -----------
        Button(
            onClick = {
                val emailTrim = correo.trim()
                val passTrim = contrasena.trim()
                mensaje = null

                when {
                    emailTrim == "cliente@correo.cl" && passTrim == "1234" -> {
                        SesionManager.iniciarSesion(context, emailTrim, "cliente")
                        nav.navigate("clienteProfesionales") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                    // ---- admin fijo ----
                    emailTrim == "admin@correo.cl" && passTrim == "1234" -> {
                        SesionManager.iniciarSesion(context, emailTrim, "admin")
                        nav.navigate("adminHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                    // ---- profesional ----
                    Repository.profesionales.any { it.email == emailTrim && it.password == passTrim } -> {
                        SesionManager.iniciarSesion(context, emailTrim, "profesional")
                        nav.navigate("profesionalHome") {
                            popUpTo("login") { inclusive = true }
                        }
                    }

                    else -> mensaje = "Credenciales inválidas ❌"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AAB0),
                contentColor = Color.White
            )
        ) {
            Text("Ingresar", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = { nav.navigate("crearCliente") },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(6.dp),
            border = BorderStroke(1.dp, Color(0xFF00AAB0)),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = Color(0xFF00AAB0)
            )
        ) {
            Text("Registrarme", fontSize = 16.sp)
        }



        Spacer(modifier = Modifier.height(20.dp))

        // ----------- MENSAJE DE VALIDACIÓN -----------
        mensaje?.let {
            Text(
                text = it,
                fontSize = 17.sp,
                color = if (it.contains("✅") || it.contains("Bienvenido")) Color(0xFF2E7D32)
                else Color(0xFFC62828)
            )
        }
    }
}
