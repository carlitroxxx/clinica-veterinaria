package com.example.clinicaveterinaria.model

data class TurnoProfesional(
    val id_turno: Int = 0, //autoincrementable
    val profesional_rut: String,
    val dia_semana: Int,  // 0=Lunes - 1=Martes-2=Miercoles-3=Jueves-4=Viernes-5=Sabado
    val hora_inicio: String,
    val hora_fin: String,
    val duracion_min: Int
)
