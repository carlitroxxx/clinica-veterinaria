package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R // <-- Importante para la foto

/**
 * Pantalla de Perfil Profesional (Solo Frontend)
 *
 * @param nombre Nombre del profesional.
 * @param especialidad Especialidad principal.
 * @param bio Biografía o descripción.
 * @param servicios Lista de servicios que ofrece.
 * @param fotoResId El ID del recurso drawable (ej. R.drawable.perfildoctor1).
 * @param onAgendarClick Lambda para cuando se presiona el botón "Agendar".
 * @param onBackClick Lambda para cuando se presiona el botón "Volver" (en la TopAppBar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilProfesionalScreen(
    nombre: String,
    especialidad: String,
    bio: String,
    servicios: List<String>,
    @DrawableRes fotoResId: Int,
    onAgendarClick: () -> Unit,
    onBackClick: () -> Unit // <-- Nueva lambda para el botón de volver
) {

    Scaffold(
        topBar = {
            // Barra de título (TopAppBar) consistente con AgendarScreen
            TopAppBar(
                title = { Text("Perfil Profesional") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { // <-- Llama a la nueva lambda
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                // Hacemos que la columna tenga scroll por si el contenido es muy largo
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp) // Espacio entre secciones
        ) {

            // --- Sección de Foto y Nombre ---
            Image(
                painter = painterResource(id = fotoResId),
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop, // Asegura que la imagen llene el círculo
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape) // <-- Recorta la imagen en forma de círculo
                    .border(
                        2.dp,
                        MaterialTheme.colorScheme.primary, // <-- Borde con color del tema
                        CircleShape
                    )
            )

            Text(
                text = nombre,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = especialidad,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary // <-- Color del tema
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp)) // Separador

            // --- Sección "Sobre mí" (Biografía) ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinea el texto a la izquierda
            ) {
                Text(
                    text = "Sobre mí",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bio,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            // --- Sección "Servicios" ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start // Alinea el texto a la izquierda
            ) {
                Text(
                    text = "Servicios Principales",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Muestra la lista de servicios con un ícono
                servicios.forEach { servicio ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Servicio",
                            tint = MaterialTheme.colorScheme.primary, // Color del tema
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = servicio,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f)) // <-- Empuja el botón al final

            // --- Botón "Agendar" ---
            Button(
                onClick = onAgendarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agendar Cita")
            }
        }
    }
}