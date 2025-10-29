package com.example.clinicaveterinaria.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profesionales")
data class Profesional(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val especialidad: String,
    val bio: String,
    val fotoUrl: String? = null
)