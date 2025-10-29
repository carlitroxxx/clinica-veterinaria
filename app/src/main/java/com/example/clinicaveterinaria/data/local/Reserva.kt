package com.example.clinicaveterinaria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservas")
data class Reserva(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val profesionalId: Int,
    val pacienteId: Int, // (Por ahora podemos usar un '1' fijo)
    val fecha: String, // Formato "AAAA-MM-DD"
    val hora: String,  // Formato "HH:MM"
    val servicio: String,
    val estado: String // "Pendiente", "Realizada", "Cancelada"
)