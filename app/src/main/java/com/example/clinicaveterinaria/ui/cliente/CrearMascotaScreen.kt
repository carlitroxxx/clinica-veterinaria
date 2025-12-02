package com.example.clinicaveterinaria.ui.cliente

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.clinicaveterinaria.R
import com.example.clinicaveterinaria.data.Repository
import com.example.clinicaveterinaria.ui.mascota.MascotaViewModel
import com.example.clinicaveterinaria.ui.mascota.TipoAnimalDropdown
import kotlinx.coroutines.launch

data class MascotaForm(
    val clienteRut: String,
    val nombre: String,
    val especie: String,
    val raza: String?,
    val sexo: String?,
    val fechaNacimiento: String?
)

@Composable
fun CrearMascotaRoute(
    nav: NavHostController,
    clienteRut: String,
    onGuardarMascota: (MascotaForm) -> Unit = { }
) {
    val scope = rememberCoroutineScope()
    var error by remember { mutableStateOf<String?>(null) }

    CrearMascotaScreen(
        clienteRut = clienteRut,
        onGuardar = { form ->
            scope.launch {
                error = null
                val res = Repository.agregarMascota(form)
                if (res.ok) {
                    onGuardarMascota(form)
                    nav.navigate("clienteProfesionales") {
                        popUpTo("clienteProfesionales") { inclusive = true }
                    }
                } else {
                    error = res.mensaje ?: "No se pudo guardar la mascota"
                }
            }
        },
        onCancelar = { nav.popBackStack() },
        errorMessage = error
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMascotaScreen(
    clienteRut: String,
    onGuardar: (MascotaForm) -> Unit,
    onCancelar: () -> Unit,
    errorMessage: String? = null,
    viewModel: MascotaViewModel = viewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.cargarTiposAnimal()
    }

    val tiposAnimal by viewModel.tiposAnimal.collectAsState()

    var rut by rememberSaveable { mutableStateOf(clienteRut) }
    var nombre by rememberSaveable { mutableStateOf("") }
    var especie by rememberSaveable { mutableStateOf("") }
    var raza by rememberSaveable { mutableStateOf("") }
    var sexo by rememberSaveable { mutableStateOf("") }
    var fechaNac by rememberSaveable { mutableStateOf("") }

    val sexos = listOf("Macho", "Hembra", "Desconocido")

    val nombreOk = nombre.trim().isNotEmpty()
    val especieOk = especie.trim().isNotEmpty()
    val fechaOk = fechaNac.isBlank() || Regex("""^\d{4}-\d{2}-\d{2}$""").matches(fechaNac)
    val formOk = nombreOk && especieOk && fechaOk

    var expSexo by rememberSaveable { mutableStateOf(false) }

    val colorPrincipal = Color(0xFF00AAB0)
    val colorFondoCampo = Color(0xFFF7FCFC)
    val fieldColors = OutlinedTextFieldDefaults.colors(
        unfocusedContainerColor = colorFondoCampo,
        unfocusedBorderColor = colorPrincipal,
        focusedBorderColor = colorPrincipal,
        focusedLabelColor = colorPrincipal,
        disabledContainerColor = Color(0xFFEEEEEE),
        disabledBorderColor = Color.Gray,
        disabledLabelColor = Color.Gray
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Mascota") },
                navigationIcon = {
                    IconButton(onClick = onCancelar) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancelar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colorPrincipal,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp, top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Logo Clínica",
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth(0.6f)
                )
            }

            item {
                OutlinedTextField(
                    value = rut,
                    onValueChange = { },
                    label = { Text("RUT Cliente") },
                    singleLine = true,
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            // Nombre mascota
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la mascota") },
                    singleLine = true,
                    isError = nombre.isNotEmpty() && !nombreOk,
                    supportingText = {
                        if (nombre.isNotEmpty() && !nombreOk) Text("Ingresa el nombre")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            item {
                TipoAnimalDropdown(
                    tipos = tiposAnimal,
                    selected = especie,
                    onSelectedChange = { especie = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // RAZA
            item {
                OutlinedTextField(
                    value = raza,
                    onValueChange = { raza = it },
                    label = { Text("Raza (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = fieldColors
                )
            }

            // SEXO
            item {
                ExposedDropdownMenuBox(
                    expanded = expSexo,
                    onExpandedChange = { expSexo = !expSexo }
                ) {
                    OutlinedTextField(
                        value = sexo,
                        onValueChange = {},
                        label = { Text("Sexo (opcional)") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expSexo) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        colors = fieldColors
                    )
                    ExposedDropdownMenu(
                        expanded = expSexo,
                        onDismissRequest = { expSexo = false }
                    ) {
                        sexos.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    sexo = opt
                                    expSexo = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                OutlinedTextField(
                    value = fechaNac,
                    onValueChange = { fechaNac = it },
                    label = { Text("Fecha de nacimiento (opcional)") },
                    placeholder = { Text("AAAA-MM-DD") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    isError = !fechaOk && fechaNac.isNotEmpty(),
                    supportingText = {
                        if (!fechaOk && fechaNac.isNotEmpty()) Text("Formato esperado: AAAA-MM-DD")
                    },
                    colors = fieldColors
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onCancelar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colorPrincipal),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(colorPrincipal))
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onGuardar(
                                MascotaForm(
                                    clienteRut = rut.trim(),
                                    nombre = nombre.trim(),
                                    especie = especie.trim(),
                                    raza = raza.trim().ifEmpty { null },
                                    sexo = sexo.trim().ifEmpty { null },
                                    fechaNacimiento = fechaNac.trim().ifEmpty { null }
                                )
                            )
                        },
                        enabled = formOk,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = colorPrincipal)
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
