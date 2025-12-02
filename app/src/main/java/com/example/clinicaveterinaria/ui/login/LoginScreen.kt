package com.example.clinicaveterinaria.ui.login

import android.content.Context
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(nav: NavHostController, context: Context) {
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .height(220.dp)
                .padding(bottom = 32.dp)
        )

        // CORREO
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

        // CONTRASEÑA
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

        // BOTÓN INGRESAR
        Button(
            onClick = {
                scope.launch {
                    val emailTrim = correo.trim()
                    val passTrim = contrasena.trim()
                    mensaje = null

                    if (emailTrim.isEmpty() || passTrim.isEmpty()) {
                        mensaje = "Ingresa correo y contraseña"
                        return@launch
                    }

                    cargando = true
                    try {
                        // 1) Intentar login como CLIENTE (vía backend)
                        val cliente = Repository.obtenerClientePorEmail(emailTrim)
                        if (cliente != null && cliente.contrasena == passTrim) {
                            SesionManager.iniciarSesion(
                                context = context,
                                email = cliente.email,
                                tipo = "cliente",
                                token = null  // Por ahora sin JWT
                            )
                            nav.navigate("clienteProfesionales") {
                                popUpTo("login") { inclusive = true }
                            }
                            cargando = false
                            return@launch
                        }

                        // 2) Admin hardcodeado
                        if (emailTrim == "admin@correo.cl" && passTrim == "1234") {
                            SesionManager.iniciarSesion(
                                context = context,
                                email = emailTrim,
                                tipo = "admin",
                                token = null
                            )
                            nav.navigate("adminHome") {
                                popUpTo("login") { inclusive = true }
                            }
                            cargando = false
                            return@launch
                        }

                        // 3) Intentar login como PROFESIONAL (usando Repository.validarProfesional)
                        val esProfesional = Repository.validarProfesional(emailTrim, passTrim)
                        if (esProfesional) {
                            SesionManager.iniciarSesion(
                                context = context,
                                email = emailTrim,
                                tipo = "profesional",
                                token = null
                            )
                            nav.navigate("profesionalHome") {
                                popUpTo("login") { inclusive = true }
                            }
                            cargando = false
                            return@launch
                        }

                        // 4) Si nada resultó → credenciales inválidas
                        mensaje = "Credenciales inválidas ❌"
                    } catch (e: Exception) {
                        mensaje = "Error al conectar con el servidor"
                    } finally {
                        cargando = false
                    }
                }
            },
            enabled = !cargando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AAB0),
                contentColor = Color.White
            )
        ) {
            if (cargando) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    color = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                Text("Ingresar", fontSize = 18.sp)
            }
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
