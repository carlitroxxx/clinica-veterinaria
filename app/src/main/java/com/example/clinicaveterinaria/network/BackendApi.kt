package com.example.clinicaveterinaria.network

import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.ui.cliente.MascotaForm
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


data class CrearReservaRequest(
    val clienteRut: String,
    val profesionalRut: String,
    val fecha: String,
    val hora: String,
    val servicio: String
)

data class EstadoReservaRequest(
    val estadoNuevo: String
)

interface BackendApi {


    @POST("clientes")
    suspend fun crearCliente(
        @Body cliente: Cliente
    ): Cliente

    @GET("clientes/email/{email}")
    suspend fun getClientePorEmail(
        @Path("email") email: String
    ): Cliente

    @GET("clientes")
    suspend fun getClientes(): List<Cliente>

    @GET("clientes/{rut}")
    suspend fun getClientePorRut(
        @Path("rut") rut: String
    ): Cliente

    @PUT("clientes/{rut}")
    suspend fun actualizarCliente(
        @Path("rut") rut: String,
        @Body cliente: Cliente
    ): Cliente

    @DELETE("clientes/{rut}")
    suspend fun eliminarCliente(
        @Path("rut") rut: String
    )



    @GET("profesionales")
    suspend fun getProfesionales(): List<Profesional>

    @GET("profesionales/{rut}")
    suspend fun getProfesional(
        @Path("rut") rut: String
    ): Profesional

    @GET("profesionales/email/{email}")
    suspend fun getProfesionalPorEmail(
        @Path("email") email: String
    ): Profesional

    @POST("profesionales")
    suspend fun createProfesional(
        @Body profesional: Profesional
    ): Profesional

    @PUT("profesionales/{rut}")
    suspend fun updateProfesional(
        @Path("rut") rut: String,
        @Body profesional: Profesional
    ): Profesional

    @DELETE("profesionales/{rut}")
    suspend fun deleteProfesional(
        @Path("rut") rut: String
    )



    @POST("mascotas")
    suspend fun crearMascota(
        @Body request: MascotaForm
    )

    @GET("mascotas/cliente/{rutCliente}/tiene")
    suspend fun clienteTieneMascota(
        @Path("rutCliente") rutCliente: String
    ): Boolean

    @GET("mascotas/cliente/{rutCliente}")
    suspend fun getMascotasPorCliente(
        @Path("rutCliente") rutCliente: String
    ): List<Any>


    @POST("reservas")
    suspend fun crearReserva(
        @Body request: CrearReservaRequest
    ): Reserva

    @GET("reservas/cliente/{rutCliente}")
    suspend fun getReservasCliente(
        @Path("rutCliente") rutCliente: String
    ): List<Reserva>

    @GET("reservas/profesional/{rutProfesional}")
    suspend fun getReservasProfesionalEn(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<Reserva>

    @GET("reservas/profesional/{rutProfesional}/disponibles")
    suspend fun getHorasDisponibles(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<String>

    @PUT("reservas/{idReserva}/estado")
    suspend fun actualizarEstadoReserva(
        @Path("idReserva") idReserva: Long,
        @Body body: EstadoReservaRequest
    )

    @DELETE("reservas/{idReserva}")
    suspend fun cancelarReserva(
        @Path("idReserva") idReserva: Long
    )
}
