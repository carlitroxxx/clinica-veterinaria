package com.example.clinicaveterinaria.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * SQLiteOpenHelper simple, sin capas extra. Mantiene el diseño base:
 * - profesional
 * - cliente
 * - mascota
 * - turno_profesional
 * - cita
 */
class BaseDatos(context: Context) :
    SQLiteOpenHelper(context, "clinica.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
    /** Turnos por defecto Lunes(0) a Viernes(4), 10:00 a 16:00, duración 30 min */
    private fun insertarTurnosDefaultParaProfesional(
        profesionalRut: String,
        duracionMin: Int = 30,
        horaInicio: String = "10:00",
        horaFin: String = "16:00"
    ) {
        val db = writableDatabase
        for (dia in 0..4) { // L-V
            val cv = ContentValues().apply {
                put("profesional_rut", profesionalRut)
                put("dia_semana", dia)
                put("hora_inicio", horaInicio)
                put("hora_fin", horaFin)
                put("duracion_min", duracionMin)
            }
            db.insert("turno_profesional", null, cv)
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // PROFESIONAL
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS profesional(
                rut TEXT PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                genero TEXT NOT NULL,
                fecha_nacimiento TEXT NOT NULL,     -- "YYYY-MM-DD"
                especialidad TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT NOT NULL
            );
        """.trimIndent())

        // CLIENTE
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cliente(
                rut TEXT PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT NOT NULL
            );
        """.trimIndent())

        // MASCOTA
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS mascota(
                id_mascota INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_rut TEXT NOT NULL,
                nombre TEXT NOT NULL,
                especie TEXT NOT NULL,              -- "perro","gato", etc.
                raza TEXT,
                sexo TEXT,                          -- "macho","hembra"
                fecha_nacimiento TEXT,              -- "YYYY-MM-DD"
                FOREIGN KEY(cliente_rut) REFERENCES cliente(rut)
                    ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())

        // TURNO PROFESIONAL
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS turno_profesional(
                id_turno INTEGER PRIMARY KEY AUTOINCREMENT,
                profesional_rut TEXT NOT NULL,
                dia_semana INTEGER NOT NULL,        -- 0..6 (L..D)
                hora_inicio TEXT NOT NULL,          -- "HH:MM"
                hora_fin TEXT NOT NULL,             -- "HH:MM"
                duracion_min INTEGER NOT NULL,
                FOREIGN KEY(profesional_rut) REFERENCES profesional(rut)
                    ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())

        // CITA
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cita(
                id_cita INTEGER PRIMARY KEY AUTOINCREMENT,
                profesional_rut TEXT NOT NULL,
                cliente_rut TEXT NOT NULL,
                id_mascota INTEGER NOT NULL,
                fecha TEXT NOT NULL,                -- "YYYY-MM-DD"
                hora_inicio TEXT NOT NULL,          -- "HH:MM"
                hora_fin TEXT NOT NULL,             -- "HH:MM"
                estado TEXT NOT NULL DEFAULT 'pendiente' 
                    CHECK(estado IN ('pendiente','realizada','cancelada','ausente')),
                motivo TEXT,
                FOREIGN KEY(profesional_rut) REFERENCES profesional(rut)
                    ON UPDATE CASCADE ON DELETE RESTRICT,
                FOREIGN KEY(cliente_rut) REFERENCES cliente(rut)
                    ON UPDATE CASCADE ON DELETE RESTRICT,
                FOREIGN KEY(id_mascota) REFERENCES mascota(id_mascota)
                    ON UPDATE CASCADE ON DELETE RESTRICT
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cita")
        db.execSQL("DROP TABLE IF EXISTS turno_profesional")
        db.execSQL("DROP TABLE IF EXISTS mascota")
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS profesional")
        onCreate(db)
    }

    // =========================
    // =      PROFESIONAL      =
    // =========================

    fun insertarProfesional(
        rut: String,
        nombres: String,
        apellidos: String,
        genero: String,
        fechaNacimiento: String,
        especialidad: String,
        email: String,
        telefono: String
    ): Long {
        val db = writableDatabase
        var rowId = -1L
        db.beginTransaction()
        try {
            val cv = ContentValues().apply {
                put("rut", rut)
                put("nombres", nombres)
                put("apellidos", apellidos)
                put("genero", genero)
                put("fecha_nacimiento", fechaNacimiento)
                put("especialidad", especialidad)
                put("email", email)
                put("telefono", telefono)
            }
            rowId = db.insert("profesional", null, cv)

            if (rowId != -1L) {
                // Lunes(0) a Viernes(4), 10:00–16:00, bloques de 30 min
                insertarTurnosDefaultParaProfesional(rut, duracionMin = 30, horaInicio = "10:00", horaFin = "16:00")
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return rowId
    }

    fun actualizarProfesional(
        rut: String,
        nombres: String,
        apellidos: String,
        genero: String,
        fechaNacimiento: String,
        especialidad: String,
        email: String,
        telefono: String
    ): Int {
        val cv = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("genero", genero)
            put("fecha_nacimiento", fechaNacimiento)
            put("especialidad", especialidad)
            put("email", email)
            put("telefono", telefono)
        }
        return writableDatabase.update("profesional", cv, "rut=?", arrayOf(rut))
    }

    fun listaProfesional(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c: Cursor = readableDatabase.rawQuery(
            """
            SELECT rut, nombres, apellidos, genero, fecha_nacimiento, especialidad, email, telefono 
            FROM profesional
            ORDER BY apellidos, nombres
            """.trimIndent(), null
        )
        c.use { cursor ->
            while (cursor.moveToNext()) {
                val item = mapOf(
                    "rut" to cursor.getString(0),
                    "nombres" to cursor.getString(1),
                    "apellidos" to cursor.getString(2),
                    "genero" to cursor.getString(3),
                    "fecha_nacimiento" to cursor.getString(4),
                    "especialidad" to cursor.getString(5),
                    "email" to cursor.getString(6),
                    "telefono" to cursor.getString(7),
                )
                lista.add(item)
            }
        }
        return lista
    }

    fun eliminarProfesional(rut: String): Int {
        return writableDatabase.delete("profesional", "rut=?", arrayOf(rut))
    }

    // =========================
    // =        CLIENTE        =
    // =========================

    fun insertarCliente(
        rut: String,
        nombres: String,
        apellidos: String,
        email: String,
        telefono: String
    ): Long {
        val cv = ContentValues().apply {
            put("rut", rut)
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("email", email)
            put("telefono", telefono)
        }
        return writableDatabase.insert("cliente", null, cv)
    }

    fun listaCliente(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c = readableDatabase.rawQuery(
            """
            SELECT rut, nombres, apellidos, email, telefono
            FROM cliente
            ORDER BY apellidos, nombres
            """.trimIndent(), null
        )
        c.use { cursor ->
            while (cursor.moveToNext()) {
                val item = mapOf(
                    "rut" to cursor.getString(0),
                    "nombres" to cursor.getString(1),
                    "apellidos" to cursor.getString(2),
                    "email" to cursor.getString(3),
                    "telefono" to cursor.getString(4),
                )
                lista.add(item)
            }
        }
        return lista
    }

    // =========================
    // =        MASCOTA        =
    // =========================

    fun insertarMascota(
        clienteRut: String,
        nombre: String,
        especie: String,
        raza: String?,
        sexo: String?,
        fechaNacimiento: String?
    ): Long {
        val cv = ContentValues().apply {
            put("cliente_rut", clienteRut)
            put("nombre", nombre)
            put("especie", especie)
            put("raza", raza)
            put("sexo", sexo)
            put("fecha_nacimiento", fechaNacimiento)
        }
        return writableDatabase.insert("mascota", null, cv)
    }

    fun listarMascotasDeCliente(clienteRut: String): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c = readableDatabase.rawQuery(
            """
            SELECT id_mascota, nombre, especie, raza, sexo, fecha_nacimiento
            FROM mascota
            WHERE cliente_rut = ?
            ORDER BY nombre
            """.trimIndent(),
            arrayOf(clienteRut)
        )
        c.use { cursor ->
            while (cursor.moveToNext()) {
                val item = mapOf(
                    "id_mascota" to cursor.getInt(0).toString(),
                    "nombre" to cursor.getString(1),
                    "especie" to cursor.getString(2),
                    "raza" to (cursor.getString(3) ?: ""),
                    "sexo" to (cursor.getString(4) ?: ""),
                    "fecha_nacimiento" to (cursor.getString(5) ?: "")
                )
                lista.add(item)
            }
        }
        return lista
    }

    // =========================
    // =   TURNO PROFESIONAL   =
    // =========================

    fun insertarTurnoProfesional(
        profesionalRut: String,
        diaSemana: Int,
        horaInicio: String,
        horaFin: String,
        duracionMin: Int
    ): Long {
        val cv = ContentValues().apply {
            put("profesional_rut", profesionalRut)
            put("dia_semana", diaSemana)
            put("hora_inicio", horaInicio)
            put("hora_fin", horaFin)
            put("duracion_min", duracionMin)
        }
        return writableDatabase.insert("turno_profesional", null, cv)
    }

    fun listarTurnosProfesional(profesionalRut: String): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c = readableDatabase.rawQuery(
            """
            SELECT id_turno, dia_semana, hora_inicio, hora_fin, duracion_min
            FROM turno_profesional
            WHERE profesional_rut = ?
            ORDER BY dia_semana, hora_inicio
            """.trimIndent(),
            arrayOf(profesionalRut)
        )
        c.use { cursor ->
            while (cursor.moveToNext()) {
                val item = mapOf(
                    "id_turno" to cursor.getInt(0).toString(),
                    "dia_semana" to cursor.getInt(1).toString(),
                    "hora_inicio" to cursor.getString(2),
                    "hora_fin" to cursor.getString(3),
                    "duracion_min" to cursor.getInt(4).toString()
                )
                lista.add(item)
            }
        }
        return lista
    }

    // =========================
    // =         CITA          =
    // =========================

    fun insertarCita(
        profesionalRut: String,
        clienteRut: String,
        idMascota: Int,
        fecha: String,
        horaInicio: String,
        horaFin: String,
        estado: String = "pendiente",
        motivo: String? = null
    ): Long {
        val cv = ContentValues().apply {
            put("profesional_rut", profesionalRut)
            put("cliente_rut", clienteRut)
            put("id_mascota", idMascota)
            put("fecha", fecha)
            put("hora_inicio", horaInicio)
            put("hora_fin", horaFin)
            put("estado", estado)
            put("motivo", motivo)
        }
        return writableDatabase.insert("cita", null, cv)
    }

    fun actualizarEstadoCita(idCita: Int, nuevoEstado: String): Int {
        val cv = ContentValues().apply { put("estado", nuevoEstado) }
        return writableDatabase.update(
            "cita", cv, "id_cita=?", arrayOf(idCita.toString())
        )
    }

    /** Agenda de un profesional en una fecha (para tu pantalla "Agenda de hoy") */
    fun getAgendaProfesionalPorFecha(profesionalRut: String, fecha: String): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c = readableDatabase.rawQuery(
            """
            SELECT c.id_cita, c.hora_inicio, c.hora_fin, c.estado,
                   m.nombre AS mascota, cl.nombres || ' ' || cl.apellidos AS cliente, c.motivo
            FROM cita c
            JOIN mascota m ON m.id_mascota = c.id_mascota
            JOIN cliente cl ON cl.rut = c.cliente_rut
            WHERE c.profesional_rut = ? AND c.fecha = ?
            ORDER BY c.hora_inicio
            """.trimIndent(),
            arrayOf(profesionalRut, fecha)
        )
        c.use { cursor ->
            while (cursor.moveToNext()) {
                val item = mapOf(
                    "id_cita" to cursor.getInt(0).toString(),
                    "hora_inicio" to cursor.getString(1),
                    "hora_fin" to cursor.getString(2),
                    "estado" to cursor.getString(3),
                    "mascota" to cursor.getString(4),
                    "cliente" to cursor.getString(5),
                    "motivo" to (cursor.getString(6) ?: "")
                )
                lista.add(item)
            }
        }
        return lista
    }
}
