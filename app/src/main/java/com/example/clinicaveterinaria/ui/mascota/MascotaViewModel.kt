package com.example.clinicaveterinaria.ui.mascota

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clinicaveterinaria.data.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MascotaViewModel : ViewModel() {

    private val _tiposAnimal = MutableStateFlow<List<String>>(emptyList())
    val tiposAnimal: StateFlow<List<String>> = _tiposAnimal

    fun cargarTiposAnimal() {
        viewModelScope.launch {
            val tipos = Repository.obtenerTiposAnimalDesdeApi()
            _tiposAnimal.value = tipos
        }
    }
}
