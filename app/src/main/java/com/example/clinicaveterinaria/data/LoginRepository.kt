package com.example.clinicaveterinaria.data

class LoginRepository {
    // Por ahora no lo usas en la UI, así que lo dejamos simple
    suspend fun login(
        email: String,
        password: String,
        tipo: String
    ): Result<Unit> {
        // Si quisieras, aquí podrías validar contra Repository y devolver éxito o error.
        return Result.success(Unit)
    }
}
