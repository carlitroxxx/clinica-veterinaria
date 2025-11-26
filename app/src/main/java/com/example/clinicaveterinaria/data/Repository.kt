package com.example.clinicaveterinaria.data

import com.example.clinicaveterinaria.network.AnimalTypesApi
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.ui.cliente.MascotaForm

object Repository {

    suspend fun obtenerTiposAnimalDesdeApi(): List<String> {
        return try {
            AnimalTypesApi.service.getAnimalTypes()
        } catch (e: Exception) {
            listOf("Perro", "Gato", "Caballo")
        }
    }
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
        return Profesional(
            rut = row["rut"].orEmpty(),
            nombres = row["nombres"].orEmpty(),
            apellidos = row["apellidos"].orEmpty(),
            genero = row["genero"].orEmpty(),
            fechaNacimiento = row["fecha_nacimiento"].orEmpty(),
            especialidad = row["especialidad"].orEmpty(),
            email = row["email"].orEmpty(),
            telefono = row["telefono"].orEmpty(),
            password = row["password"] ?: "1234"
        )
    }


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


    fun agregarReserva(
        clienteRut: String,
        profesionalRut: String,
        fecha: String,
        hora: String,
        servicio: String
    ): Resultado<Long> {
        if (clienteRut.isBlank() || profesionalRut.isBlank() || fecha.isBlank() || hora.isBlank() || servicio.isBlank()) {
            return Resultado(false, mensaje = "Faltan campos obligatorios")
        }

        val rowId = db.insertReserva(
            clienteRut = clienteRut.trim(),
            profesionalRut = profesionalRut.trim(),
            fecha = fecha.trim(),
            hora = hora.trim(),
            servicio = servicio.trim()
        )
        return if (rowId != -1L) Resultado(true, data = rowId) else Resultado(false, mensaje = "No se pudo crear la reserva")
    }

    fun obtenerReservasCliente(rutCliente: String): List<Reserva> {
        return db.getReservasPorCliente(rutCliente).map {
            Reserva(
                id = it.id,
                fecha = it.fecha,
                hora = it.hora,
                servicio = it.servicio,
                estado = it.estado,
                profesionalRut = it.profesionalRut
            )
        }
    }

    fun cancelarReserva(idReserva: Long): Resultado<Unit> {
        val filas = db.cancelarReserva(idReserva)
        return if (filas > 0) Resultado(true) else Resultado(false, mensaje = "No se pudo cancelar")
    }


    // Profesionales
    fun obtenerProfesionales(): List<Profesional> =
        db.getProfesionales()

    fun obtenerProfesional(rut: String): Profesional? =
        db.getProfesional(rut)

    // Turnos y disponibilidad
    fun obtenerTurnosProfesional(rut: String): List<BaseDatos.TurnoDb> =
        db.getTurnosProfesional(rut)

    @RequiresApi(Build.VERSION_CODES.O)
    private fun diaSemanaDesdeFecha(fecha: String): Int {
        return java.time.LocalDate.parse(fecha).dayOfWeek.value // 1=Mon .. 7=Sun
    }

    private fun generarSlots(hInicio: String, hFin: String, dur: Int): List<String> {
        fun toMin(hhmm: String): Int {
            val (h,m) = hhmm.split(":")
            return h.toInt()*60 + m.toInt()
        }
        fun toStr(min: Int): String {
            val h = min/60
            val m = min%60
            return "%02d:%02d".format(h,m)
        }
        val ini = toMin(hInicio); val fin = toMin(hFin)
        val out = mutableListOf<String>()
        var t = ini
        while (t + dur <= fin) {
            out.add(toStr(t))
            t += dur
        }
        return out
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun horasDisponibles(profRut: String, fecha: String): List<String> {
        if (fecha.isBlank()) return emptyList()
        val dow = diaSemanaDesdeFecha(fecha)
        val turnos = db.getTurnosProfesional(profRut).filter { it.diaSemana == dow }
        if (turnos.isEmpty()) return emptyList()

        val reservadas = db.getHorasReservadas(profRut, fecha)
        return turnos
            .flatMap { generarSlots(it.horaInicio, it.horaFin, it.duracionMin) }
            .filter { it !in reservadas }
            .sorted()
    }

    fun getDb(): BaseDatos = db

    // Para buscar al profesional por email (sesión)
    fun obtenerProfesionalPorEmail(email: String): com.example.clinicaveterinaria.model.Profesional? =
        try {
            obtenerProfesionales().firstOrNull { it.email.equals(email, ignoreCase = true) }
        } catch (_: Exception) { null }

    data class ReservaProfesional(
        val id: Long,
        val hora: String,
        val servicio: String,
        val estado: String,
        val clienteNombre: String,
        val clienteRut: String
    )

    fun obtenerReservasProfesionalEn(rutProfesional: String, fecha: String): List<ReservaProfesional> {
        return try {
            val crudas = db.getReservasDeProfesionalEn(rutProfesional, fecha)
            crudas.map { r ->
                val nom = db.getClienteNombrePorRut(r.clienteRut)?.let { "${it.first} ${it.second}" } ?: r.clienteRut
                ReservaProfesional(
                    id = r.id,
                    hora = r.hora,
                    servicio = r.servicio,
                    estado = r.estado,
                    clienteNombre = nom,
                    clienteRut = r.clienteRut
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun clienteTieneMascota(rutCliente: String): Boolean {
        val sql = "SELECT 1 FROM Mascota WHERE cliente_rut = ? LIMIT 1"
        val args = arrayOf(rutCliente)
        val rd = db.readableDatabase
        rd.rawQuery(sql, args).use { c ->
            return c.moveToFirst()
        }
    }
    fun obtenerMascotaNombrePorReserva(reservaId: Long): String? {
        val rd = db.readableDatabase
        val sql = """
        SELECT m.nombre
        FROM reserva r
        JOIN mascota m ON m.cliente_rut = r.cliente_rut
        WHERE r.id_reserva = ?
        ORDER BY m.id_mascota ASC
        LIMIT 1
    """.trimIndent()

        rd.rawQuery(sql, arrayOf(reservaId.toString())).use { c ->
            return if (c.moveToFirst()) c.getString(0) else null
        }
    }

    fun actualizarEstadoReserva(idReserva: Long, estadoNuevo: String): Boolean {
        val estadoDb = when (estadoNuevo.trim().lowercase()) {
            "realizada" -> "Realizada"
            "cancelada" -> "Cancelada"
            else -> "Pendiente"
        }
        return try {
            db.writableDatabase.execSQL(
                "UPDATE reserva SET estado = ? WHERE id_reserva = ?",
                arrayOf(estadoDb, idReserva)
            )
            true
        } catch (_: Exception) { false }
    }

}
