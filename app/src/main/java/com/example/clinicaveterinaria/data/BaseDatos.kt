package com.example.clinicaveterinaria.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.clinicaveterinaria.model.Cliente
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
                dia_semana INTEGER NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
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
                fecha TEXT NOT NULL,
                hora_inicio TEXT NOT NULL,
                hora_fin TEXT NOT NULL,
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

    //CITA
    fun insertarCita(
        profesionalRut: String,
        clienteRut: String,
        idMascota: Int,
        fecha: String,        // "YYYY-MM-DD"
        horaInicio: String,   // "HH:MM"
        horaFin: String,      // "HH:MM"
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
        return writableDatabase.update("cita", cv, "id_cita=?", arrayOf(idCita.toString()))
    }

    fun listarCitasDeCliente(clienteRut: String): List<Map<String, String>> {
        val lista = mutableListOf<Map<String, String>>()
        val c = readableDatabase.rawQuery("""
            SELECT c.id_cita, c.fecha, c.hora_inicio, c.hora_fin, c.estado,
                   p.nombres || ' ' || p.apellidos as profesional, 
                   m.nombre as mascota, c.motivo
            FROM cita c
            JOIN profesional p ON p.rut = c.profesional_rut
            JOIN mascota m ON m.id_mascota = c.id_mascota
            WHERE c.cliente_rut=?
            ORDER BY c.fecha DESC, c.hora_inicio DESC
        """.trimIndent(), arrayOf(clienteRut))
        c.use { cur ->
            while (cur.moveToNext()) {
                lista.add(
                    mapOf(
                        "id_cita" to cur.getInt(0).toString(),
                        "fecha" to cur.getString(1),
                        "hora_inicio" to cur.getString(2),
                        "hora_fin" to cur.getString(3),
                        "estado" to cur.getString(4),
                        "profesional" to cur.getString(5),
                        "mascota" to cur.getString(6),
                        "motivo" to (cur.getString(7) ?: "")
                    )
                )
            }
        }
        return lista
    }

    //Horarios disponibles según turnos y citas existentes
    @RequiresApi(Build.VERSION_CODES.O)
    fun getHorariosDisponibles(profesionalRut: String, fecha: String): List<String> {
        // 1) Día de semana 0..6
        val diaSemana = diaSemanaDeFecha(fecha)

        //Para cada turno del día, generamos bloques y restamos los ocupados
        val ocupados = citasOcupadas(profesionalRut, fecha).toMutableSet()
        val disponibles = mutableListOf<String>()

        val c = readableDatabase.rawQuery("""
            SELECT hora_inicio, hora_fin, duracion_min
            FROM turno_profesional
            WHERE profesional_rut=? AND dia_semana=?
        """.trimIndent(), arrayOf(profesionalRut, diaSemana.toString()))

        c.use { cur ->
            while (cur.moveToNext()) {
                val hIni = cur.getString(0)
                val hFin = cur.getString(1)
                val dur = cur.getInt(2)

                var t = aMinutos(hIni)
                val fin = aMinutos(hFin)
                while (t + dur <= fin) {
                    val slot = aHora(t)
                    if (!ocupados.contains(slot)) {
                        disponibles.add(slot)
                    }
                    t += dur
                }
            }
        }
        return disponibles.sorted()
    }

    private fun citasOcupadas(profesionalRut: String, fecha: String): Set<String> {
        val set = mutableSetOf<String>()
        val c = readableDatabase.rawQuery("""
            SELECT hora_inicio
            FROM cita
            WHERE profesional_rut=? AND fecha=? AND estado IN ('pendiente','realizada','ausente')
        """.trimIndent(), arrayOf(profesionalRut, fecha))
        c.use { cur ->
            while (cur.moveToNext()) set.add(cur.getString(0))
        }
        return set
    }

    private fun aMinutos(hhmm: String): Int {
        val p = hhmm.split(":")
        return p[0].toInt() * 60 + p[1].toInt()
    }

    private fun aHora(mins: Int): String {
        val h = mins / 60
        val m = mins % 60
        return "%02d:%02d".format(h, m)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun diaSemanaDeFecha(fecha: String): Int {
        val ld = LocalDate.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE)
        val dow = ld.dayOfWeek.value // 1..7 (L..D)
        return (dow - 1)
    }
}
