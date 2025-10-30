package com.example.clinicaveterinaria.ui.admin

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.model.Profesional

import com.example.clinicaveterinaria.data.Repository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaProfesionalesScreen(nav: NavHostController) {

    val lista: List<Profesional> = Repository.profesionales


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { nav.navigate("crearProfesional") }) {
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
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Profesionales",
                    style = MaterialTheme.typography.titleLarge
                )
                Divider()
                Spacer(Modifier.height(24.dp))
                Text("No hay profesionales todavía")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 88.dp)
            )  {
                item {
                    Column(Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                        Text("Profesionales", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(4.dp))
                        Text("Total: ${lista.size}", style = MaterialTheme.typography.labelMedium)
                        Divider(Modifier.padding(top = 8.dp))
                    }
                }
                items(lista, key = { it.rut }) { p ->
                    Card {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "${p.nombres} ${p.apellidos}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(p.especialidad, style = MaterialTheme.typography.bodySmall)
                                Text(p.rut, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = {
                                    val rutEnc = Uri.encode(p.rut)
                                    nav.navigate("modificarProfesional/$rutEnc")
                                }
                            ) { Text("Editar") }
                        }
                    }
                }
            }
        }
    }
}
