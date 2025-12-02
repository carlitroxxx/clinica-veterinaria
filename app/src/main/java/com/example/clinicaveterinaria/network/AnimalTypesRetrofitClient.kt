package com.example.clinicaveterinaria.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AnimalTypesRetrofitClient {
    private const val BASE_URL =
        "https://raw.githubusercontent.com/mvrtinnbz/Animal-Api/refs/heads/main/"

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
