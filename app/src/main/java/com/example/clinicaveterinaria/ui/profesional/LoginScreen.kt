package com.example.clinicaveterinaria.ui.profesional

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicaveterinaria.R

@Composable
fun LoginScreen(){
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.fillMaxSize().padding(2.dp).background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        //
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.height(300.dp).padding(bottom = 32.dp)
        )
        //
        TextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7FCFC),
                unfocusedBorderColor = Color(0xFF00AAB0),
                focusedBorderColor = Color(0xFF00AAB0),
                focusedLabelColor = Color(0xFF00AAB0)
            ),
            value = usuario,
            onValueChange = {usuario = it},
            singleLine = true,
            label = { Text("Usuario")}
        )

        //
        Spacer(modifier = Modifier.height(20.dp))
        //
        TextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFF7FCFC),
                unfocusedBorderColor = Color(0xFF00AAB0),
                focusedBorderColor = Color(0xFF00AAB0),
                focusedLabelColor = Color(0xFF00AAB0)
            ),
            value = contrasena,
            onValueChange = {contrasena = it},
            label = { Text("Contraseña")},
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )
        //
        Spacer(modifier = Modifier.height(20.dp))
        //
        Button(onClick = {
            //credencial admin
            if (usuario == "admin" && contrasena == "1234") { // Validación simple
                mensaje = "Bienvenido, $usuario ✅"
            } else {
                mensaje = "Usuario o contraseña incorrecta ❌"
            }
        },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF00AAB0),
                contentColor = Color(0xFFFFFFFF)
            )
        ) {Text("Ingresar")}
        Spacer(modifier = Modifier.height(20.dp))

        // ---------- MENSAJE DE VALIDACIÓN ----------
        if (mensaje.isNotEmpty()) {
            Text(
                text = mensaje,
                fontSize = 18.sp,
                color = if (mensaje.contains("✅")) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
        }
    }
}