package com.example.clinicaveterinaria.ui.admin

import android.R.color.black
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.data.Profesional

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProfesionalesScreen(nav: NavHostController) {
    // Observa directamente la lista observable para recomponer al volver
    val lista: List<Profesional> = Repository.profesionales

    Scaffold(

        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("crearProfesional") }) {
                Icon(Icons.Default.Add, contentDescription = "Agregar profesional")
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { inner ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(inner) // 👈 elimina el espacio reservado por el Scaffold
                .padding(horizontal = 8.dp), // deja solo margen lateral
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 0.dp)
        ) {
            item {
                TopAppBar(title = { Text("Profesionales") })
            }
            items(lista, key = { it.rut }) { p ->
                Card {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("${p.nombres} ${p.apellidos}", style = MaterialTheme.typography.titleMedium)
                            Text(p.especialidad, style = MaterialTheme.typography.bodySmall)
                            Text(p.rut, style = MaterialTheme.typography.labelSmall)
                        }
                        Button(onClick = {
                            val rutEnc = Uri.encode(p.rut)
                            nav.navigate("modificarProfesional/$rutEnc")
                        }) { Text("Editar") }
                    }
                }
            }
        }
    }
}
