package com.example.clinicaveterinaria.util


object RutUtils {

    /** Quita puntos y guiones, y deja mayúsculas. */
    fun limpiarRut(input: String): String =
        input.replace(".", "").replace("-", "").trim().uppercase()

    /** Calcula dígito verificador (K para 10). */
    fun calcularDV(numSinDv: String): Char {
        var m = 0
        var s = 1
        var t = numSinDv.toIntOrNull() ?: return '?'
        while (t != 0) {
            s = (s + t % 10 * (9 - (m++ % 6))) % 11
            t /= 10
        }
        return if (s == 0) 'K' else (s + 47).toChar()
    }

    /** Valida que el RUT tenga DV correcto. Acepta con o sin guión/puntos. */
    fun rutEsValido(input: String): Boolean {
        val clean = limpiarRut(input)
        if (clean.length < 2) return false
        val cuerpo = clean.dropLast(1)
        val dvIngresado = clean.last()
        if (cuerpo.any { !it.isDigit() }) return false
        val dvOk = calcularDV(cuerpo)
        return dvIngresado == dvOk
    }

    /** Devuelve RUT formateado: 12345678-K  (sin puntos). */
    fun formatearRut(input: String): String {
        val clean = limpiarRut(input)
        if (clean.length < 2) return input.trim().uppercase()
        val cuerpo = clean.dropLast(1)
        val dv = clean.last()
        return "${cuerpo}-${dv}"
    }
}
