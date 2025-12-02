// network/BackendApi.kt
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

// ==== DTOs simples para requests ====

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

data class LoginRequest(
    val email: String,
    val password: String,
    val tipo: String // "CLIENTE" o "PROFESIONAL"
)

data class LoginResponse(
    val token: String,
    val tipo: String
)

interface BackendApi {

    // ========= AUTH =========

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse


    // ========= PROFESIONALES =========
    // @RequestMapping("/api/profesionales")

    @GET("api/profesionales")
    suspend fun getProfesionales(): List<Profesional>

    @GET("api/profesionales/{rut}")
    suspend fun getProfesional(@Path("rut") rut: String): Profesional?

    @GET("api/profesionales/email/{email}")
    suspend fun getProfesionalPorEmail(@Path("email") email: String): Profesional?

    @POST("api/profesionales")
    suspend fun createProfesional(@Body profesional: Profesional): Profesional

    @PUT("api/profesionales/{rut}")
    suspend fun updateProfesional(
        @Path("rut") rut: String,
        @Body profesional: Profesional
    ): Profesional

    @DELETE("api/profesionales/{rut}")
    suspend fun deleteProfesional(@Path("rut") rut: String)


    // ========= CLIENTES =========
    // @RequestMapping("/api/clientes")

    @GET("api/clientes/email/{email}")
    suspend fun getClientePorEmail(@Path("email") email: String): Cliente?

    @POST("api/clientes")
    suspend fun crearCliente(@Body cliente: Cliente): Cliente


    // ========= MASCOTAS =========
    // @RequestMapping("/api/mascotas")

    // Tu backend recibe MascotaRequest, pero mientras los campos coincidan
    // con MascotaForm (clienteRut, nombre, especie, raza, sexo, fechaNacimiento),
    // Retrofit lo serializa igual.
    @POST("api/mascotas")
    suspend fun crearMascota(@Body mascota: MascotaForm): Any

    @GET("api/mascotas/cliente/{rutCliente}/tiene")
    suspend fun clienteTieneMascota(@Path("rutCliente") rutCliente: String): Boolean


    // ========= RESERVAS =========
    // @RequestMapping("/api/reservas")

    @GET("api/reservas/cliente/{rutCliente}")
    suspend fun getReservasCliente(
        @Path("rutCliente") rutCliente: String
    ): List<Reserva>

    @POST("api/reservas")
    suspend fun crearReserva(@Body body: CrearReservaRequest): Reserva

    // DELETE /api/reservas/{idReserva}
    @DELETE("api/reservas/{idReserva}")
    suspend fun cancelarReserva(@Path("idReserva") idReserva: Long)

    // GET /api/reservas/profesional/{rutProfesional}/disponibles?fecha=YYYY-MM-DD
    @GET("api/reservas/profesional/{rutProfesional}/disponibles")
    suspend fun getHorasDisponibles(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<String>

    // Opcional pero útil para la agenda del profesional:
    // GET /api/reservas/profesional/{rutProfesional}?fecha=YYYY-MM-DD
    @GET("api/reservas/profesional/{rutProfesional}")
    suspend fun getReservasProfesionalEnFecha(
        @Path("rutProfesional") rutProfesional: String,
        @Query("fecha") fecha: String
    ): List<Reserva>

    // PUT /api/reservas/{idReserva}/estado
    @PUT("api/reservas/{idReserva}/estado")
    suspend fun actualizarEstadoReserva(
        @Path("idReserva") idReserva: Long,
        @Body body: EstadoReservaRequest
    ): Any
}
