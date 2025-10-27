package com.example.clinicaveterinaria.model

data class Cita(
    val id_cita: Int = 0,        // AUTOINCREMENT en DB
    val profesional_rut: String,
    val cliente_rut: String,
    val id_mascota: Int,
    val fecha: String,           // "YYYY-MM-DD"
    val hora_inicio: String,     // "HH:MM"
    val hora_fin: String,        // "HH:MM"
    val estado: String,          // 'pendiente','realizada','cancelada','ausente'
    val motivo: String? = null
)
