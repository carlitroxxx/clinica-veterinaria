package com.example.clinicaveterinaria.model

data class Reserva(
    val id: Long,
    val clienteRut: String,
    val profesionalRut: String,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val estado: String
)
