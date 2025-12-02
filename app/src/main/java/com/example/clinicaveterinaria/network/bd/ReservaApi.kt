// network/bd/ReservaApi.kt
package com.example.clinicaveterinaria.network.bd

import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.network.dto.ReservaProfesional
import com.example.clinicaveterinaria.network.RetrofitInstance
import retrofit2.http.*

interface ReservaApi {

    data class ReservaRequest(
        val clienteRut: String,
        val profesionalRut: String,
        val fecha: String,
        val hora: String,
        val servicio: String
    )

    data class EstadoReservaRequest(
        val estado: String
    )

    @POST("reservas")
    suspend fun crearReserva(@Body req: ReservaRequest): Long

    @GET("reservas/cliente/{rutCliente}")
    suspend fun getReservasPorCliente(
        @Path("rutCliente") rutCliente: String
    ): List<Reserva>

    @POST("reservas/{idReserva}/cancelar")
    suspend fun cancelarReserva(
        @Path("idReserva") idReserva: Long
    )

    @GET("reservas/profesional/{rutProfesional}")
    suspend fun getReservasProfesionalEn(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<ReservaProfesional>

    @PUT("reservas/{idReserva}/estado")
    suspend fun actualizarEstadoReserva(
        @Path("idReserva") idReserva: Long,
        @Body body: EstadoReservaRequest
    )

    @GET("reservas/horas-disponibles/{rutProfesional}")
    suspend fun getHorasDisponibles(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<String>

    companion object {
        val service: ReservaApi by lazy {
            RetrofitInstance.retrofit.create(ReservaApi::class.java)
        }
    }
}
