package com.example.clinicaveterinaria.network.dto

data class ReservaProfesional(
    val id: Long,
    val clienteRut: String,
    val profesionalRut: String,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val estado: String,
    // Este campo es opcional: si el backend no lo envía, quedará en null
    val clienteNombre: String? = null
)
