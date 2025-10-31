package com.example.clinicaveterinaria.ui.admin

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.data.Repository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProfesionalesScreen(nav: NavHostController) {

    val lista: List<Profesional> = Repository.profesionales

    val colorPrincipal = Color(0xFF00AAB0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profesionales") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { nav.navigate("crearProfesional") },
                containerColor = colorPrincipal,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar profesional")
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { inner ->
        if (lista.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Clínica",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.7f)
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    "No hay profesionales registrados",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Presiona (+) para agregar el primero.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            )  {
                items(lista, key = { it.rut }) { p ->
                    AdminProfesionalCard(
                        profesional = p,
                        onEditarClick = {
                            val rutEnc = Uri.encode(p.rut)
                            nav.navigate("modificarProfesional/$rutEnc")
                        },
                        colorPrincipal = colorPrincipal
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminProfesionalCard(
    profesional: Profesional,
    onEditarClick: () -> Unit,
    colorPrincipal: Color
) {
    val fotoId = when (profesional.genero) {
        "Femenino" -> R.drawable.perfildoctora1
        "Masculino" -> R.drawable.perfildoctor1
        else -> R.drawable.logo
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = fotoId),
                contentDescription = "Foto de ${profesional.nombres}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, colorPrincipal, CircleShape)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${profesional.nombres} ${profesional.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    profesional.especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorPrincipal
                )
                Text(
                    profesional.rut,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Button(
                onClick = onEditarClick,
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
            ) {
                Text("Editar")
            }
        }
    }
}