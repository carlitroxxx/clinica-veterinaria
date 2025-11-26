package com.example.clinicaveterinaria.network

object AnimalTypesApi {

    val service: AnimalTypesApiService by lazy {
        AnimalTypesRetrofitClient.instance.create(AnimalTypesApiService::class.java)
    }
}
