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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clinicaveterinaria.R

data class ProfesionalUi(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val genero: String,
    val fechaNacimiento: String,
    val especialidad: String,
    val email: String,
    val telefono: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfesionalesScreen(
    profesionales: List<ProfesionalUi>,
    onProfesionalClick: (profesionalRut: String) -> Unit
) {
    val colorPrincipal = Color(0xFF00AAB0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuestros Profesionales") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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

            if (profesionales.isEmpty()) {
                item {
                    EmptyProfesionalesState()
                }
            } else {
                items(profesionales) { profesional ->
                    val fotoId = when (profesional.genero) {
                        "Femenino" -> R.drawable.perfildoctora1
                        "Masculino" -> R.drawable.perfildoctor1
                        else -> R.drawable.logo
                    }
                    ProfesionalCard(
                        profesional = profesional,
                        fotoResId = fotoId,
                        onClick = { onProfesionalClick(profesional.rut) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfesionalCard(
    profesional: ProfesionalUi,
    @androidx.annotation.DrawableRes fotoResId: Int,
    onClick: () -> Unit
) {
    val colorPrincipal = Color(0xFF00AAB0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                    .border(1.dp, colorPrincipal, CircleShape)
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
                    color = colorPrincipal
                )
            }
        }
    }
}

@Composable
private fun EmptyProfesionalesState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Aún no hay profesionales disponibles",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Vuelve más tarde o refresca la pantalla.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
