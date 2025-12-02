package com.example.clinicaveterinaria.model

data class Profesional(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val genero: String,
    val fechaNacimiento: String,
    val especialidad: String,
    val email: String,
    val telefono: String,
    val password: String
)
