package com.example.clinicaveterinaria.model
data class TurnoProfesional(
    val id_turno: Int = 0,
    val profesional_rut: String,
    val dia_semana: Int,
    val hora_inicio: String,
    val hora_fin: String,
    val duracion_min: Int
)
