// data/SesionManager.kt
package com.example.clinicaveterinaria.data

import android.content.Context
import android.content.SharedPreferences

object SesionManager {

    private const val PREFS_NAME = "sesion_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_TIPO = "tipo"
    private const val KEY_TOKEN = "token"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun iniciarSesion(
        context: Context,
        email: String,
        tipo: String,
        token: String? = null   // 👈 token opcional
    ) {
        prefs(context).edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_TIPO, tipo.lowercase())   // 👈 normalizamos
            .putString(KEY_TOKEN, token)
            .apply()
    }

    fun cerrarSesion(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun haySesionActiva(context: Context): Boolean {
        val p = prefs(context)
        val email = p.getString(KEY_EMAIL, null)
        val tipo = p.getString(KEY_TIPO, null)
        // 👈 NO exigimos token, solo email + tipo
        return !email.isNullOrBlank() && !tipo.isNullOrBlank()
    }

    fun obtenerEmail(context: Context): String? =
        prefs(context).getString(KEY_EMAIL, null)

    fun obtenerTipo(context: Context): String? =
        prefs(context).getString(KEY_TIPO, null)

    fun obtenerToken(context: Context): String? =
        prefs(context).getString(KEY_TOKEN, null)
}
