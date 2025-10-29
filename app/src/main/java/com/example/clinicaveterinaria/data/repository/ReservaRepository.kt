package com.example.clinicaveterinaria.data.repository

import ReservaDao
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clinicaveterinaria.data.local.Reserva
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

// Recibe el DAO
class ReservaRepository(private val reservaDao: ReservaDao) {

    //Listar reservas
    fun getReservasByPaciente(idPaciente: Int): Flow<List<Reserva>> {
        return reservaDao.getByPacienteId(idPaciente)
    }

    //Cancelar reserva
    suspend fun cancelarReserva(idReserva: Int) {
        reservaDao.cancelarReserva(idReserva)
    }

    //Lógica de crear reserva
    // Retorna un String con error, o null si es exitoso
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun crearReserva(reserva: Reserva): String? {

        // 1. Regla "No permitir pasado"
        val hoy = LocalDate.now()
        val fechaReserva = LocalDate.parse(reserva.fecha, DateTimeFormatter.ISO_LOCAL_DATE)

        if (fechaReserva.isBefore(hoy)) {
            return "Error: No puedes agendar en una fecha pasada."
        }

        // 2. Regla "No permitir solape"
        val reservaExistente = reservaDao.findReserva(
            idProf = reserva.profesionalId,
            fecha = reserva.fecha,
            hora = reserva.hora
        )

        if (reservaExistente != null) {
            return "Error: La hora seleccionada ya no está disponible."
        }

        // 3. ¡Éxito!
        reservaDao.insert(reserva)
        return null
    }
}