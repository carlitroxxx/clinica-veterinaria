package com.example.clinicaveterinaria.model
data class Reserva(
    val id: Long,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val estado: String,
    val profesionalRut: String
)
