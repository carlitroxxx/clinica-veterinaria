package com.example.clinicaveterinaria.data

import androidx.compose.runtime.mutableStateListOf

data class Profesional(
    val rut: String,
    val nombres: String,
    val apellidos: String,
    val genero: String,
    val fechaNacimiento: String, // AAAA-MM-DD
    val especialidad: String,
    val email: String,
    val telefono: String
)

object Repository {
    private val _profesionales = mutableStateListOf(
        Profesional(
            rut = "11.111.888-1",
            nombres = "Ana",
            apellidos = "Rojas",
            genero = "Femenino",
            fechaNacimiento = "1990-05-01",
            especialidad = "Medicina General",
            email = "ana@ejemplo.com",
            telefono = "987654321"
        ),
        Profesional(
            rut = "22.222.777-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.666-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.555-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.334-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.223-3",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-9",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-8",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-7",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-6",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-5",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.222-3",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.223-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.232-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        ),
        Profesional(
            rut = "22.222.322-2",
            nombres = "Carlos",
            apellidos = "Toro",
            genero = "Masculino",
            fechaNacimiento = "1988-07-11",
            especialidad = "Kinesiología",
            email = "carlos@ejemplo.com",
            telefono = "912345678"
        )
    )
    val profesionales: List<Profesional> get() = _profesionales

    fun listarProfesionales(): List<Profesional> = profesionales

    fun obtenerProfesional(rut: String): Profesional? =
        _profesionales.find { it.rut == rut }

    fun agregarProfesional(p: Profesional) {
        val existe = obtenerProfesional(p.rut)
        if (existe == null) {
            _profesionales.add(p)
        } else {
            actualizarProfesional(p)
        }
    }

    fun actualizarProfesional(p: Profesional) {
        val idx = _profesionales.indexOfFirst { it.rut == p.rut }
        if (idx >= 0) _profesionales[idx] = p
    }

    fun eliminarProfesional(rut: String) {
        _profesionales.removeAll { it.rut == rut }
    }
}
