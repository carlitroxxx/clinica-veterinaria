package com.example.clinicaveterinaria.ui.screens.paciente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

data class ReservaMock(
    val id: String,
    val fecha: String,
    val hora: String,
    val profesional: String,
    val servicio: String,
    val estado: String // "Pendiente", "Realizada", "Cancelada"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(
    reservas: List<ReservaMock>,
    onCancelarClick: (reservaId: String) -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Reservas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
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
        if (reservas.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tienes reservas agendadas.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        } else {
            //Listar reservas
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(reservas, key = { it.id }) { reserva ->
                    ReservaCard(
                        reserva = reserva,
                        onCancelarClick = { onCancelarClick(reserva.id) }
                    )
                }
            }
        }
    }
}
@Composable
fun ReservaCard(
    reserva: ReservaMock,
    onCancelarClick: () -> Unit
) {
    val (statusColor, statusDecoration) = when (reserva.estado) {
        "Realizada" -> MaterialTheme.colorScheme.primary to TextDecoration.None
        "Cancelada" -> Color.Gray to TextDecoration.LineThrough
        else -> MaterialTheme.colorScheme.secondary to TextDecoration.None // "Pendiente"
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${reserva.fecha} - ${reserva.hora}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textDecoration = statusDecoration // Tacha si está cancelada
                )
                Text(
                    text = reserva.estado,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = reserva.servicio,
                style = MaterialTheme.typography.bodyLarge,
                textDecoration = statusDecoration
            )
            Text(
                text = "con ${reserva.profesional}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textDecoration = statusDecoration
            )

            if (reserva.estado == "Pendiente") {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onCancelarClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar Reserva")
                }
            }
        }
    }
}