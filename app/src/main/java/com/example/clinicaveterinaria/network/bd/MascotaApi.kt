// network/bd/MascotaApi.kt
package com.example.clinicaveterinaria.network.bd

import com.example.clinicaveterinaria.network.RetrofitInstance
import retrofit2.http.*

interface MascotaApi {

    data class MascotaRequest(
        val clienteRut: String,
        val nombre: String,
        val especie: String,
        val raza: String?,
        val sexo: String?,
        val fechaNacimiento: String?
    )

    data class MascotaResponse(
        val id: Long,
        val clienteRut: String,
        val nombre: String,
        val especie: String,
        val raza: String?,
        val sexo: String?,
        val fechaNacimiento: String?
    )

    @POST("mascotas")
    suspend fun crearMascota(@Body mascota: MascotaRequest): Long

    @GET("mascotas/cliente/{rutCliente}")
    suspend fun getMascotasCliente(
        @Path("rutCliente") rutCliente: String
    ): List<MascotaResponse>

    @GET("mascotas/por-reserva/{idReserva}")
    suspend fun getMascotaNombrePorReserva(
        @Path("idReserva") reservaId: Long
    ): String

    companion object {
        val service: MascotaApi by lazy {
            RetrofitInstance.retrofit.create(MascotaApi::class.java)
        }
    }
}
