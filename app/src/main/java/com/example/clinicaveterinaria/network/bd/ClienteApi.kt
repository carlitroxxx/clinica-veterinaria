package com.example.clinicaveterinaria.network.bd

import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.network.RetrofitInstance
import retrofit2.http.*

interface ClienteApi {

    @POST("clientes")
    suspend fun crearCliente(@Body cliente: Cliente): Cliente

    @GET("clientes/email/{email}")
    suspend fun getClientePorEmail(@Path("email") email: String): Cliente

    companion object {
        val service: ClienteApi by lazy {
            RetrofitInstance.retrofit.create(ClienteApi::class.java)
        }
    }
}
