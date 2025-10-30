package com.example.clinicaveterinaria.data

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.ui.cliente.MascotaForm

object Repository {
    private lateinit var db: BaseDatos
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

        val filas = db.listaProfesional()
        _profesionales.addAll(filas.map { mapToProfesional(it) })
    }

    private fun mapToProfesional(row: Map<String, String>): Profesional {
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
            telefono = row["telefono"].orEmpty(),
            password = row["password"] ?: "1234",
            bio = row["bio"] ?: ""
        )
    }

    fun obtenerProfesional(rut: String): Profesional? =
        _profesionales.firstOrNull { it.rut == rut }

    fun agregarProfesional(p: Profesional): Boolean {
        val id = db.insertarProfesional(
            p.rut, p.nombres, p.apellidos, p.genero, p.fechaNacimiento,
            p.especialidad, p.email, p.telefono, p.password
        )
        if (id == -1L) return false

        // Actualiza la lista observable
        val idx = _profesionales.indexOfFirst { it.rut == p.rut }
        if (idx >= 0) _profesionales[idx] = p else _profesionales.add(p)
        return true
    }
    fun validarProfesional(email: String, password: String): Boolean {
        return db.validarProfesional(email, password)
    }
    fun actualizarProfesional(p: Profesional): Boolean {
        val filas = db.actualizarProfesional(
            p.rut, p.nombres, p.apellidos, p.genero, p.fechaNacimiento,
            p.especialidad, p.email, p.telefono, p.password
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


    //cliente

    data class Resultado<out T>(val ok: Boolean, val data: T? = null, val mensaje: String? = null)

    fun agregarCliente(c: Cliente): Resultado<Unit> {
        // Duplicados simples por rut/email
        if (db.existeClientePorRut(c.rut)) {
            return Resultado(false, mensaje = "Ya existe un cliente con ese RUT")
        }
        if (db.existeClientePorEmail(c.email)) {
            return Resultado(false, mensaje = "Ya existe un cliente con ese correo")
        }

        val id = db.insertCliente(c)
        return if (id != -1L) Resultado(true) else Resultado(false, mensaje = "No se pudo guardar")
    }
    fun agregarMascota(form: MascotaForm): Resultado<Long> {
        if (form.clienteRut.isBlank() || form.nombre.isBlank() || form.especie.isBlank()) {
            return Resultado(false, mensaje = "Faltan campos obligatorios")
        }
        val rowId = db.insertMascota(
            clienteRut = form.clienteRut.trim(),
            nombre = form.nombre.trim(),
            especie = form.especie.trim(),
            raza = form.raza?.trim().takeUnless { it.isNullOrEmpty() },
            sexo = form.sexo?.trim().takeUnless { it.isNullOrEmpty() },
            fechaNacimiento = form.fechaNacimiento?.trim().takeUnless { it.isNullOrEmpty() }
        )
        return if (rowId != -1L) Resultado(true, data = rowId) else Resultado(false, mensaje = "No se pudo guardar la mascota")
    }
    fun obtenerClientePorEmail(email: String): Cliente? {
        if (email.isBlank()) return null
        return db.getClientePorEmail(email)
    }

    fun getDb(): BaseDatos = db
}
