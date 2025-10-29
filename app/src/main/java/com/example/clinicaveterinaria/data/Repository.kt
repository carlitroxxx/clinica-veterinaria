package com.example.clinicaveterinaria.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.clinicaveterinaria.model.Profesional

object Repository {
    private lateinit var db: BaseDatos

    // Lista observable que usa SIEMPRE el modelo de package 'model'
    private val _profesionales = mutableStateListOf<Profesional>()
    val profesionales: List<Profesional> get() = _profesionales

    fun init(context: Context) {
        if (!::db.isInitialized) {
            db = BaseDatos(context.applicationContext)
            cargarProfesionalesDesdeBD()
        }
    }

    private fun cargarProfesionalesDesdeBD() {
        _profesionales.clear()

        // ⚠️ Si tu BaseDatos.listaProfesional() devuelve List<Map<String,String>>
        // usamos este mapeo; si en tu proyecto ya devuelve List<Profesional>,
        // cambia la línea por:  _profesionales.addAll(db.listaProfesional())
        val filas = db.listaProfesional() // List<Map<String, String>>
        _profesionales.addAll(filas.map { mapToProfesional(it) })
    }

    private fun mapToProfesional(row: Map<String, String>): Profesional {
        // Keys esperadas según el CREATE TABLE:
        // rut, nombres, apellidos, genero, fecha_nacimiento, especialidad, email, telefono
        return Profesional(
            rut = row["rut"].orEmpty(),
            nombres = row["nombres"].orEmpty(),
            apellidos = row["apellidos"].orEmpty(),
            genero = row["genero"].orEmpty(),
            // En la app usamos 'fechaNacimiento' (camelCase) pero en BD es 'fecha_nacimiento'
            fechaNacimiento = row["fecha_nacimiento"].orEmpty(),
            especialidad = row["especialidad"].orEmpty(),
            email = row["email"].orEmpty(),
            telefono = row["telefono"].orEmpty()
        )
    }

    fun obtenerProfesional(rut: String): Profesional? =
        _profesionales.firstOrNull { it.rut == rut }

    /** Inserta en BD (crea turnos L–V 10–16 en BaseDatos) y refleja en la lista observable. */
    fun agregarProfesional(p: Profesional): Boolean {
        val id = db.insertarProfesional(
            p.rut, p.nombres, p.apellidos, p.genero, p.fechaNacimiento,
            p.especialidad, p.email, p.telefono
        )
        if (id == -1L) return false

        // Actualiza la lista observable (evita duplicados por RUT)
        val idx = _profesionales.indexOfFirst { it.rut == p.rut }
        if (idx >= 0) _profesionales[idx] = p else _profesionales.add(p)
        return true
    }

    fun actualizarProfesional(p: Profesional): Boolean {
        val filas = db.actualizarProfesional(
            p.rut, p.nombres, p.apellidos, p.genero, p.fechaNacimiento,
            p.especialidad, p.email, p.telefono
        )
        if (filas > 0) {
            val idx = _profesionales.indexOfFirst { it.rut == p.rut }
            if (idx >= 0) _profesionales[idx] = p
            return true
        }
        return false
    }

    fun eliminarProfesional(rut: String) {
        db.eliminarProfesional(rut)
        _profesionales.removeAll { it.rut == rut }
    }

    // Útil si alguna pantalla necesita llamar helpers directos de BD (insertar cita, etc.)
    fun getDb(): BaseDatos = db
}
