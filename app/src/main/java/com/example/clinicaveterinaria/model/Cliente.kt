package com.example.clinicaveterinaria.model

data class Cliente(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val email: String? = null,
    val telefono: String? = null
)
