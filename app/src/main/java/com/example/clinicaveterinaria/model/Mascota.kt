package com.example.clinicaveterinaria.model


data class Mascota(
    val id_mascota: Int = 0,
    val cliente_rut: String,
    val nombre: String,
    val especie: String,
    val raza: String? = null,
    val sexo: String? = null,
    val fechaNacimiento: String? = null  // "YYYY-MM-DD"
)
