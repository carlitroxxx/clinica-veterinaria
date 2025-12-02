package com.example.clinicaveterinaria.data

class LoginRepository {
    suspend fun login(
        email: String,
        password: String,
        tipo: String
    ): Result<Unit> {
        return Result.success(Unit)
    }
}
