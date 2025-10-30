package com.example.clinicaveterinaria.ui.cliente

import com.example.clinicaveterinaria.R

object ClienteMockData {
    val profesionales = listOf(
        Profesional(
            rut = "11.111.111-1",
            nombres = "Ana",
            apellidos = "Pérez",
            genero = "F",
            fechaNacimiento = "1990-05-12",
            especialidad = "Medicina General",
            email = "ana.perez@clivet.cl",
            telefono = "+56 9 1111 1111"
        ),
        Profesional(
            rut = "22.222.222-2",
            nombres = "José",
            apellidos = "Soto",
            genero = "M",
            fechaNacimiento = "1986-11-02",
            especialidad = "Vacunación y Control",
            email = "jose.soto@clivet.cl",
            telefono = "+56 9 2222 2222"
        )
    )

    fun buscarPorRut(rut: String): Profesional? =
        profesionales.find { it.rut == rut }

    fun fotoDe(p: Profesional): Int =
        if (p.genero.equals("F", ignoreCase = true)) R.drawable.perfildoctora1
        else R.drawable.perfildoctor1

    fun bioDe(p: Profesional): String = when (p.rut) {
        "11.111.111-1" -> "Amante de los animales, con 8 años de experiencia en consulta general y bienestar animal."
        "22.222.222-2" -> "Especializado en vacunación, control sano y orientación preventiva para cachorros y adultos."
        else -> "Profesional de la salud veterinaria."
    }

    fun serviciosDe(p: Profesional): List<String> = when (p.especialidad) {
        "Vacunación y Control" -> listOf("Vacunación", "Control sano", "Desparasitación")
        else -> listOf("Consulta General", "Urgencia ambulatoria", "Control sano")
    }
}
