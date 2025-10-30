package com.example.clinicaveterinaria.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.model.Reserva
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class BaseDatos(context: Context) :
    SQLiteOpenHelper(context, "clinica.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }
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
                fecha_nacimiento TEXT NOT NULL,
                especialidad TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT NOT NULL,
                password TEXT NOT NULL DEFAULT '1234'
            );
        """.trimIndent())

        // CLIENTE
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS cliente(
                rut TEXT PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                telefono TEXT NOT NULL,
                contrasena TEXT NOT NULL
            );
        """.trimIndent())

        // MASCOTA
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS mascota(
                id_mascota INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_rut TEXT NOT NULL,
                nombre TEXT NOT NULL,
                especie TEXT NOT NULL,
                raza TEXT,
                sexo TEXT,
                fecha_nacimiento TEXT,
                FOREIGN KEY(cliente_rut) REFERENCES cliente(rut)
                    ON UPDATE CASCADE ON DELETE CASCADE
            );
        """.trimIndent())

        // TURNO PROFESIONAL
        db.execSQL("""
          CREATE TABLE IF NOT EXISTS turno_profesional(
            id_turno INTEGER PRIMARY KEY AUTOINCREMENT,
            profesional_rut TEXT NOT NULL,
            dia_semana INTEGER NOT NULL,   -- 1=Lun .. 7=Dom (o tu convención)
            hora_inicio TEXT NOT NULL,     -- "HH:MM"
            hora_fin TEXT NOT NULL,        -- "HH:MM"
            duracion_min INTEGER NOT NULL  -- ej: 30
          );
        """.trimIndent())

        // RESERVA
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS reserva(
                id_reserva INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_rut TEXT NOT NULL,
                profesional_rut TEXT NOT NULL,
                fecha TEXT NOT NULL,     -- "YYYY-MM-DD"
                hora TEXT NOT NULL,      -- "HH:MM"
                servicio TEXT NOT NULL,
                estado TEXT NOT NULL DEFAULT 'Pendiente', -- Pendiente | Realizada | Cancelada
                FOREIGN KEY(cliente_rut) REFERENCES cliente(rut) ON UPDATE CASCADE ON DELETE CASCADE
                -- Si tu tabla profesional usa rut como PK, puedes añadir también:
                -- ,FOREIGN KEY(profesional_rut) REFERENCES profesional(rut) ON UPDATE CASCADE ON DELETE RESTRICT
            );
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS reserva")
        db.execSQL("DROP TABLE IF EXISTS turno_profesional")
        db.execSQL("DROP TABLE IF EXISTS mascota")
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS profesional")
        onCreate(db)
    }

    //PROFESIONAL
    fun insertarProfesional(
        rut: String,
        nombres: String,
        apellidos: String,
        genero: String,
        fechaNacimiento: String,
        especialidad: String,
        email: String,
        telefono: String,
        password: String = "1234"
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
                put("password", password)
            }
            rowId = db.insert("profesional", null, cv)

            if (rowId != -1L) {
                insertarTurnosDefaultParaProfesional(rut, 30, "10:00", "16:00")
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
        telefono: String,
        password: String
    ): Int {
        val cv = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("genero", genero)
            put("fecha_nacimiento", fechaNacimiento)
            put("especialidad", especialidad)
            put("email", email)
            put("telefono", telefono)
            put("password", password)
        }
        return writableDatabase.update("profesional", cv, "rut=?", arrayOf(rut))
    }

    fun listaProfesional(): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c: Cursor = readableDatabase.rawQuery(
            """
            SELECT rut, nombres, apellidos, genero, fecha_nacimiento, especialidad, email, telefono, password
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
                    "password" to (cursor.getString(8) ?: "1234")
                )
                lista.add(item)
            }
        }
        return lista
    }

    fun eliminarProfesional(rut: String): Int {
        return writableDatabase.delete("profesional", "rut=?", arrayOf(rut))
    }

    fun validarProfesional(email: String, password: String): Boolean {
        val c = readableDatabase.rawQuery(
            "SELECT 1 FROM profesional WHERE email=? AND password=? LIMIT 1",
            arrayOf(email, password)
        )
        c.use { return it.moveToFirst() }
    }


    fun existeClientePorRut(rut: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT 1 FROM cliente WHERE rut = ? LIMIT 1",
            arrayOf(rut)
        ).use { c -> return c.moveToFirst() }
    }

    fun existeClientePorEmail(email: String): Boolean {
        readableDatabase.rawQuery(
            "SELECT 1 FROM cliente WHERE LOWER(email) = LOWER(?) LIMIT 1",
            arrayOf(email)
        ).use { c -> return c.moveToFirst() }
    }

    fun insertCliente(c: Cliente): Long {
        val cv = ContentValues().apply {
            put("rut", c.rut)
            put("nombres", c.nombres)
            put("apellidos", c.apellidos)
            put("email", c.email)
            put("telefono", c.telefono)
            put("contrasena", c.contrasena)
        }
        return writableDatabase.insert("cliente", null, cv)
    }
    fun getClientePorEmail(email: String): Cliente? {
        readableDatabase.rawQuery(
            "SELECT rut, nombres, apellidos, email, telefono, contrasena FROM cliente WHERE LOWER(email)=LOWER(?) LIMIT 1",
            arrayOf(email)
        ).use { c ->
            return if (c.moveToFirst()) {
                com.example.clinicaveterinaria.model.Cliente(
                    rut = c.getString(0),
                    nombres = c.getString(1),
                    apellidos = c.getString(2),
                    email = c.getString(3),
                    telefono = c.getString(4),
                    contrasena = c.getString(5)
                )
            } else null
        }
    }

    //MASCOTA
    fun insertMascota(
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



    // INSERT
    fun insertReserva(
        clienteRut: String,
        profesionalRut: String,
        fecha: String,
        hora: String,
        servicio: String,
        estado: String = "Pendiente"
    ): Long {
        val cv = ContentValues().apply {
            put("cliente_rut", clienteRut)
            put("profesional_rut", profesionalRut)
            put("fecha", fecha)
            put("hora", hora)
            put("servicio", servicio)
            put("estado", estado)
        }
        return writableDatabase.insert("reserva", null, cv)
    }

    // LISTAR por cliente
    fun getReservasPorCliente(rutCliente: String): List<Reserva> {
        val lista = mutableListOf<Reserva>()
        readableDatabase.rawQuery(
            """
        SELECT id_reserva, fecha, hora, servicio, estado, profesional_rut
        FROM reserva
        WHERE cliente_rut = ?
        ORDER BY fecha DESC, hora DESC
        """.trimIndent(),
            arrayOf(rutCliente)
        ).use { c ->
            while (c.moveToNext()) {
                lista.add(
                    Reserva(
                        id = c.getLong(0),
                        fecha = c.getString(1),
                        hora = c.getString(2),
                        servicio = c.getString(3),
                        estado = c.getString(4),
                        profesionalRut = c.getString(5)
                    )
                )
            }
        }
        return lista
    }

    // CANCELAR (marca estado)
    fun cancelarReserva(idReserva: Long): Int {
        val cv = ContentValues().apply { put("estado", "Cancelada") }
        return writableDatabase.update("reserva", cv, "id_reserva = ?", arrayOf(idReserva.toString()))
    }

    fun getProfesionales(): List<Profesional> {
        val out = mutableListOf<Profesional>()
        readableDatabase.rawQuery(
            "SELECT nombres, apellidos, rut, genero, fecha_nacimiento, especialidad, email, telefono, password FROM profesional",
            null
        ).use { c ->
            while (c.moveToNext()) {
                out.add(
                    Profesional(
                        nombres = c.getString(0),
                        apellidos = c.getString(1),
                        rut = c.getString(2),
                        genero = c.getString(3),
                        fechaNacimiento = c.getString(4),
                        especialidad = c.getString(5),
                        email = c.getString(6),
                        telefono = c.getString(7),
                        password = c.getString(8)
                    )
                )
            }
        }
        return out
    }

    fun getProfesional(rut: String): com.example.clinicaveterinaria.model.Profesional? {
        readableDatabase.rawQuery(
            "SELECT nombres, apellidos, rut, genero, fecha_nacimiento, especialidad, email, telefono, password FROM Profesional WHERE rut=? LIMIT 1",
            arrayOf(rut)
        ).use { c ->
            return if (c.moveToFirst()) {
                com.example.clinicaveterinaria.model.Profesional(
                    nombres = c.getString(0),
                    apellidos = c.getString(1),
                    rut = c.getString(2),
                    genero = c.getString(3),
                    fechaNacimiento = c.getString(4),
                    especialidad = c.getString(5),
                    email = c.getString(6),
                    telefono = c.getString(7),
                    password = c.getString(8)
                )
            } else null
        }
    }

    // ---------- TURNOS ----------
    data class TurnoDb(
        val diaSemana: Int,
        val horaInicio: String,  // "HH:MM"
        val horaFin: String,     // "HH:MM"
        val duracionMin: Int
    )

    fun getTurnosProfesional(rut: String): List<TurnoDb> {
        val out = mutableListOf<TurnoDb>()
        readableDatabase.rawQuery(
            "SELECT dia_semana, hora_inicio, hora_fin, duracion_min FROM turno_profesional WHERE profesional_rut=?",
            arrayOf(rut)
        ).use { c ->
            while (c.moveToNext()) {
                out.add(
                    TurnoDb(
                        diaSemana = c.getInt(0),
                        horaInicio = c.getString(1),
                        horaFin = c.getString(2),
                        duracionMin = c.getInt(3)
                    )
                )
            }
        }
        return out
    }

    // Reservas existentes de un profesional en una fecha (para excluir horas ya tomadas)
    fun getHorasReservadas(profRut: String, fecha: String): Set<String> {
        val out = mutableSetOf<String>()
        readableDatabase.rawQuery(
            "SELECT hora FROM reserva WHERE profesional_rut=? AND fecha=? AND estado <> 'Cancelada'",
            arrayOf(profRut, fecha)
        ).use { c ->
            while (c.moveToNext()) out.add(c.getString(0))
        }
        return out
    }



}
