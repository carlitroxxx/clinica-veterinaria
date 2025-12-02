package com.example.clinicaveterinaria.network.dto

data class ReservaDTO(
    val idReserva: Long,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val estado: String,
    val clienteRut: String,
    val profesionalRut: String
)