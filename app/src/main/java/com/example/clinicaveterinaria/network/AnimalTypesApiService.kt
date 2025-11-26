package com.example.clinicaveterinaria.network

import retrofit2.http.GET

interface AnimalTypesApiService {

    @GET("animal_types.json")
    suspend fun getAnimalTypes(): List<String>
}
