// network/ApiClient.kt
package com.example.clinicaveterinaria.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    //private const val BASE_URL = "http://10.0.2.2:8080/api/"
    private const val BASE_URL = "http://192.168.1.100:8080/api/"

    val backendApi: BackendApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApi::class.java)
    }
}
