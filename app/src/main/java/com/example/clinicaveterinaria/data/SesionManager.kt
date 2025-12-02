package com.example.clinicaveterinaria.data

import android.content.Context

object SesionManager {

    fun iniciarSesion(
        context: Context,
        email: String,
        tipo: String,
        token: String = "" // 👈 valor por defecto para no romper llamadas antiguas
    ) {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        prefs.edit()
            .putString("email", email)
            .putString("tipo", tipo)
            .putString("token", token)
            .apply()
    }

    fun obtenerEmail(context: Context): String? {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        return prefs.getString("email", null)
    }

    fun obtenerTipo(context: Context): String? {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        return prefs.getString("tipo", null)
    }

    fun obtenerToken(context: Context): String? {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        return prefs.getString("token", null)
    }

    fun haySesionActiva(context: Context): Boolean {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val tipo = prefs.getString("tipo", null)
        return !email.isNullOrBlank() && !tipo.isNullOrBlank()
    }

    fun cerrarSesion(context: Context) {
        val prefs = context.getSharedPreferences("sesion", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }
}
