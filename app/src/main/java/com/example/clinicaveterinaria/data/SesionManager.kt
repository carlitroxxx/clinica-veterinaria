package com.example.clinicaveterinaria.data

import android.content.Context
import android.content.SharedPreferences

object SesionManager {
    private const val PREF_NAME = "sesion_prefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_TIPO = "tipo"
    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun iniciarSesion(context: Context, email: String, tipo: String) {
        prefs(context).edit()
            .putString(KEY_EMAIL, email)
            .putString(KEY_TIPO, tipo)
            .apply()
    }

    fun cerrarSesion(context: Context) {
        prefs(context).edit().clear().apply()
    }

    fun obtenerTipo(context: Context): String? =
        prefs(context).getString(KEY_TIPO, null)

    fun obtenerEmail(context: Context): String? =
        prefs(context).getString(KEY_EMAIL, null)

    fun haySesionActiva(context: Context): Boolean =
        prefs(context).contains(KEY_EMAIL)
}
