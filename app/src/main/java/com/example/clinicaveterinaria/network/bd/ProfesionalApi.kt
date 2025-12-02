package com.example.clinicaveterinaria.network.bd

import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.network.RetrofitInstance
import retrofit2.http.*

interface ProfesionalApi {

    @GET("profesionales")
    suspend fun getProfesionales(): List<Profesional>

    @GET("profesionales/{rut}")
    suspend fun getProfesionalPorRut(@Path("rut") rut: String): Profesional

    @GET("profesionales/email/{email}")
    suspend fun getProfesionalPorEmail(@Path("email") email: String): Profesional

    @POST("profesionales")
    suspend fun crearProfesional(@Body profesional: Profesional): Profesional

    @PUT("profesionales/{rut}")
    suspend fun actualizarProfesional(
        @Path("rut") rut: String,
        @Body profesional: Profesional
    ): Profesional

    @DELETE("profesionales/{rut}")
    suspend fun eliminarProfesional(@Path("rut") rut: String)

    data class LoginProfesionalResponse(val ok: Boolean, val token: String?)

    @FormUrlEncoded
    @POST("profesionales/login")
    suspend fun loginProfesional(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginProfesionalResponse

    companion object {
        val service: ProfesionalApi by lazy {
            RetrofitInstance.retrofit.create(ProfesionalApi::class.java)
        }
    }
}
