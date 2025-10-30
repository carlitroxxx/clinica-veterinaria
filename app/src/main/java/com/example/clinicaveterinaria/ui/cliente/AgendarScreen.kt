package com.example.clinicaveterinaria.ui.cliente

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clinicaveterinaria.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class DiaCalendario(
    val fechaIso: String,        // "yyyy-MM-dd"
    val nombreDiaSemana: String, // "Lun", "Mar", ...
    val numeroDia: String        // "1", "2", ...
)

private fun generarProximos7Dias(): List<DiaCalendario> {
    val locale = Locale("es", "ES")
    val cal = Calendar.getInstance() // hoy
    val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val sdfNombre = SimpleDateFormat("EEE", locale)
    val sdfNum = SimpleDateFormat("d", locale)

    return List(7) { i ->
        val c = cal.clone() as Calendar
        c.add(Calendar.DAY_OF_MONTH, i)
        val iso = sdfIso.format(c.time)
        val nombre = sdfNombre.format(c.time).replaceFirstChar { it.uppercase(locale) }
        val num = sdfNum.format(c.time)
        DiaCalendario(iso, nombre, num)
    }
}

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
    onConfirmarClick: () -> Unit,
    onBackClick: () -> Unit,
    profesionalNombre: String? = null,
    profesionalEspecialidad: String? = null,
    @DrawableRes profesionalFotoResId: Int? = null,
    horariosDisponibles: List<String> = emptyList()
) {
    val dias = remember { generarProximos7Dias() }

    var diaSeleccionado by remember(fecha) {
        mutableStateOf(
            dias.find { it.fechaIso == fecha } ?: dias.first()
        )
    }
    var horaSeleccionada by remember(hora) { mutableStateOf(hora) }

    LaunchedEffect(diaSeleccionado.fechaIso) { onFechaChange(diaSeleccionado.fechaIso) }
    LaunchedEffect(horaSeleccionada) { onHoraChange(horaSeleccionada) }

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        unfocusedBorderColor = colorPrincipal,
        focusedBorderColor = colorPrincipal,
        focusedLabelColor = colorPrincipal
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
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

            //Info profesional
            if (!profesionalNombre.isNullOrBlank() || !profesionalEspecialidad.isNullOrBlank()) {
                InfoProfesionalCard(
                    nombre = profesionalNombre ?: "Profesional",
                    especialidad = profesionalEspecialidad ?: "",
                    fotoResId = profesionalFotoResId ?: R.drawable.logo
                )
            }

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
                    if (horaSeleccionada.isNotBlank() && horaSeleccionada !in horariosDisponibles) {
                        horaSeleccionada = ""
                    }
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
                onHoraClick = { nuevaHora -> horaSeleccionada = nuevaHora }
            )

            Text(
                "Paso 3: Detalla el motivo",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            OutlinedTextField(
                value = servicio,
                onValueChange = onServicioChange,
                label = { Text("Servicio o motivo de la consulta") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = fieldColors // <-- Color aplicado
            )

            Button(
                onClick = onConfirmarClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = fecha.isNotBlank() && hora.isNotBlank() && servicio.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal) // <-- Color
            ) {
                Text("Confirmar Reserva")
            }

            // Feedback
            if (mensajeError != null) {
                Text(mensajeError, color = MaterialTheme.colorScheme.error)
            }
            if (mensajeExito != null) {
                Text(mensajeExito, color = Color(0xFF2E7D32))
            }
        }
    }
}

@Composable
private fun InfoProfesionalCard(
    nombre: String,
    especialidad: String,
    @DrawableRes fotoResId: Int
) {
    val colorPrincipal = Color(0xFF00AAB0)

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
                    .border(1.dp, colorPrincipal, CircleShape)
            )
            Column {
                Text(text = nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (especialidad.isNotBlank()) {
                    Text(
                        text = especialidad,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorPrincipal
                    )
                }
            }
        }
    }
}

@Composable
private fun CalendarioHorizontal(
    dias: List<DiaCalendario>,
    diaSeleccionado: DiaCalendario,
    onDiaClick: (DiaCalendario) -> Unit
) {
    val colorPrincipal = Color(0xFF00AAB0)

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(dias.size) { index ->
            val dia = dias[index]
            val isSelected = dia == diaSeleccionado
            val backgroundColor = if (isSelected) colorPrincipal else MaterialTheme.colorScheme.surfaceVariant
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
                    Text(text = dia.nombreDiaSemana, fontSize = 12.sp, color = contentColor)
                    Text(text = dia.numeroDia, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = contentColor)
                }
            }
        }
    }
}

@Composable
private fun GrillaDeHoras(
    horarios: List<String>,
    horaSeleccionada: String,
    onHoraClick: (String) -> Unit
) {
    val colorPrincipal = Color(0xFF00AAB0)

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        modifier = Modifier.height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(horarios) { hora ->
            val isSelected = hora == horaSeleccionada
            val colors = if (isSelected) {
                ButtonDefaults.buttonColors(containerColor = colorPrincipal, contentColor = MaterialTheme.colorScheme.onPrimary)
            } else {
                ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal)
            }
            Button(
                onClick = { onHoraClick(hora) },
                colors = colors,
                border = if (!isSelected) ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal)) else null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = hora, textAlign = TextAlign.Center)
            }
        }
    }
}