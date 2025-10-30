package com.example.clinicaveterinaria.ui.cliente

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R

//data class
data class Profesional (
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val genero: String, // <-- Usaremos este campo
    val fechaNacimiento: String,
    val especialidad: String,
    val email: String,
    val telefono: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesionalesScreen(
    profesionales: List<Profesional>,
    onProfesionalClick: (profesionalRut: String) -> Unit
) {
    Scaffold(
        topBar = {
            // 1. Barra de título (TopAppBar)
            TopAppBar(
                title = { Text("Nuestros Profesionales") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre cards
        ) {

            // 2. Logo de la clínica en la parte superior
            item {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Clínica",
                    modifier = Modifier
                        .height(100.dp)
                        .fillMaxWidth(0.7f)
                        .padding(bottom = 16.dp)
                )
            }

            // 3. Lista de profesionales
            items(profesionales) { profesional ->

                // Se elige la foto basada en el campo 'genero'
                val fotoId = when (profesional.genero) {
                    "Femenino" -> R.drawable.perfildoctora1
                    "Masculino" -> R.drawable.perfildoctor1
                    else -> R.drawable.logo // Una foto 'default' por si acaso
                }

                ProfesionalCard(
                    profesional = profesional,
                    // Se pasa el ID de foto correcto
                    fotoResId = fotoId,
                    onClick = {
                        onProfesionalClick(profesional.rut)
                    }
                )
            }
        }
    }
}
@Composable
fun ProfesionalCard(
    profesional: Profesional,
    @androidx.annotation.DrawableRes fotoResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Color de fondo suave
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = fotoResId),
                contentDescription = "Foto de ${profesional.nombres}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Column {
                Text(
                    text = "${profesional.nombres} ${profesional.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = profesional.especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary // Color del tema
                )
            }
        }
    }
}