package com.example.clinicaveterinaria.ui.screens.paciente

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicaveterinaria.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DiaCalendario(
    val fecha: LocalDate,
    val nombreDiaSemana: String, // "Lun."
    val numeroDia: String      // "30"
)

@RequiresApi(Build.VERSION_CODES.O)
private fun generarProximos7Dias(): List<DiaCalendario> {
    val locale = Locale("es", "ES")
    val hoy = LocalDate.now()
    return List(7) { i ->
        val fecha = hoy.plusDays(i.toLong())
        DiaCalendario(
            fecha = fecha,
            nombreDiaSemana = fecha.format(DateTimeFormatter.ofPattern("E", locale)).capitalize(locale),
            numeroDia = fecha.format(DateTimeFormatter.ofPattern("d"))
        )
    }
}

//Horarios disponibles
private val horariosDisponibles = listOf(
    "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
    "14:00", "14:30", "15:00", "15:30"
)


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarScreen(
    fecha: String,
    onFechaChange: (String) -> Unit,
    hora: String,
    onHoraChange: (String) -> Unit,
    servicio: String,
    onServicioChange: (String) -> Unit,
    mensajeError: String?,
    mensajeExito: String?,
    onConfirmarClick: () -> Unit
) {

    val dias = remember { generarProximos7Dias() }

    var diaSeleccionado by remember { mutableStateOf(dias.first()) }

    var horaSeleccionada by remember { mutableStateOf("") }

    LaunchedEffect(diaSeleccionado) {
        onFechaChange(diaSeleccionado.fecha.format(DateTimeFormatter.ISO_LOCAL_DATE))
    }
    LaunchedEffect(horaSeleccionada) {
        onHoraChange(horaSeleccionada)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: navController.popBackStack() */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InfoProfesionalCard(
                nombre = "Dr. Juan Pérez",
                especialidad = "Cardiología Veterinaria",
                fotoResId = R.drawable.perfildoctor1
            )
            Text(
                "Paso 1: Selecciona el día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            CalendarioHorizontal(
                dias = dias,
                diaSeleccionado = diaSeleccionado,
                onDiaClick = { nuevoDia ->
                    diaSeleccionado = nuevoDia
                }
            )
            Text(
                "Paso 2: Selecciona la hora",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            GrillaDeHoras(
                horarios = horariosDisponibles,
                horaSeleccionada = horaSeleccionada,
                onHoraClick = { nuevaHora ->
                    horaSeleccionada = nuevaHora
                }
            )
            Text(
                "Paso 3: Detalla el motivo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = servicio,
                onValueChange = onServicioChange,
                label = { Text("Servicio o Motivo de la consulta") },
                isError = mensajeError?.contains("servicio") == true,
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = onConfirmarClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Confirmar Reserva")
            }
            if (mensajeError != null) {
                Text(mensajeError, color = MaterialTheme.colorScheme.error)
            }
            if (mensajeExito != null) {
                Text(mensajeExito, color = Color(0xFF2E7D32)) // Verde éxito
            }
        }
    }
}
@Composable
fun InfoProfesionalCard(
    nombre: String,
    especialidad: String,
    @DrawableRes fotoResId: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = fotoResId),
                contentDescription = "Foto de $nombre",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )
            Column {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = especialidad,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
@Composable
fun CalendarioHorizontal(
    dias: List<DiaCalendario>,
    diaSeleccionado: DiaCalendario,
    onDiaClick: (DiaCalendario) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dias) { dia ->
            val isSelected = dia == diaSeleccionado
            val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
            val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant

            Card(
                modifier = Modifier
                    .width(60.dp)
                    .clickable { onDiaClick(dia) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = dia.nombreDiaSemana,
                        fontSize = 12.sp,
                        color = contentColor
                    )
                    Text(
                        text = dia.numeroDia,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = contentColor
                    )
                }
            }
        }
    }
}
@Composable
fun GrillaDeHoras(
    horarios: List<String>,
    horaSeleccionada: String,
    onHoraClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp), // Se adapta al tamaño de pantalla
        modifier = Modifier.height(200.dp), // Damos una altura fija para la grilla
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(horarios) { hora ->
            val isSelected = hora == horaSeleccionada
            val colors = if (isSelected) {
                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            } else {
                ButtonDefaults.outlinedButtonColors() // Botón con borde
            }

            Button(
                onClick = { onHoraClick(hora) },
                colors = colors,
                border = if (!isSelected) ButtonDefaults.outlinedButtonBorder else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = hora,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}