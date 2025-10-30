// Cita.kt
package com.example.clinicaveterinaria.model
data class Cita(
    val id_cita: Int = 0,
    val profesional_rut: String,
    val cliente_rut: String,
    val id_mascota: Int,
    val fecha: String,
    val hora_inicio: String,
    val hora_fin: String,
    val estado: String = "pendiente",
    val motivo: String? = null
)
