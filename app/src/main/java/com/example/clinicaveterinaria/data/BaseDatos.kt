package com.example.clinicaveterinaria.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.clinicaveterinaria.model.Profesional

class BaseDatos(context: Context) : SQLiteOpenHelper(context, "clinica.db", null, 2) {

    // Configuracion de la base de datos para habilitar claves foraneas.
    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // PROFESIONAL
        db.execSQL("""
            CREATE TABLE profesional (
                rut TEXT NOT NULL PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                genero TEXT NOT NULL,
                fechaNacimiento TEXT NOT NULL,
                especialidad TEXT NOT NULL,
                email TEXT NOT NULL,
                telefono TEXT
            )
        """.trimIndent())

        // CLIENTE (dueño/a)
        db.execSQL("""
            CREATE TABLE cliente (
                rut TEXT NOT NULL PRIMARY KEY,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                email TEXT,
                telefono TEXT
            )
        """.trimIndent())

        // MASCOTA (relación con cliente por rut)
        db.execSQL("""
            CREATE TABLE mascota (
                id_mascota INTEGER PRIMARY KEY AUTOINCREMENT,
                cliente_rut TEXT NOT NULL,
                nombre TEXT NOT NULL,
                especie TEXT NOT NULL,   -- perro, gato, etc.
                raza TEXT,
                sexo TEXT,
                fechaNacimiento TEXT,    -- "YYYY-MM-DD" opcional
                FOREIGN KEY (cliente_rut) REFERENCES cliente(rut)
            )
        """.trimIndent())

        // TURNO del profesional (plantilla semanal)
        db.execSQL("""
            CREATE TABLE turno_profesional (
                id_turno INTEGER PRIMARY KEY AUTOINCREMENT,
                profesional_rut TEXT NOT NULL,
                dia_semana INTEGER NOT NULL CHECK(dia_semana BETWEEN 0 AND 6),
                hora_inicio TEXT NOT NULL,  -- "HH:MM"
                hora_fin TEXT NOT NULL,     -- "HH:MM"
                duracion_min INTEGER NOT NULL,
                FOREIGN KEY (profesional_rut) REFERENCES profesional(rut)
            )
        """.trimIndent())

        // CITA (reserva) con UNIQUE anti-doble booking
        db.execSQL("""
            CREATE TABLE cita (
                id_cita INTEGER PRIMARY KEY AUTOINCREMENT,
                profesional_rut TEXT NOT NULL,
                cliente_rut TEXT NOT NULL,
                id_mascota INTEGER NOT NULL,
                fecha TEXT NOT NULL,        -- "YYYY-MM-DD"
                hora_inicio TEXT NOT NULL,  -- "HH:MM"
                hora_fin TEXT NOT NULL,     -- "HH:MM"
                estado TEXT NOT NULL CHECK(estado IN ('pendiente','realizada','cancelada','ausente')),
                motivo TEXT,
                FOREIGN KEY (profesional_rut) REFERENCES profesional(rut),
                FOREIGN KEY (cliente_rut) REFERENCES cliente(rut),
                FOREIGN KEY (id_mascota) REFERENCES mascota(id_mascota),
                UNIQUE(profesional_rut, fecha, hora_inicio)
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Simple para desarrollo: reinicia esquema
        db.execSQL("DROP TABLE IF EXISTS cita")
        db.execSQL("DROP TABLE IF EXISTS turno_profesional")
        db.execSQL("DROP TABLE IF EXISTS mascota")
        db.execSQL("DROP TABLE IF EXISTS cliente")
        db.execSQL("DROP TABLE IF EXISTS profesional")
        onCreate(db)
    }

    // --- CRUD mínimos que ya tenías ---

    fun insertarProfesional(
        rut: String,
        nombres: String,
        apellidos: String,
        genero: String,
        fechaNacimiento: String,
        especialidad: String,
        email: String,
        telefono: String
    ){
        val valores = ContentValues().apply{
            put("rut", rut)
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("genero", genero)
            put("fechaNacimiento", fechaNacimiento)
            put("especialidad", especialidad)
            put("email", email)
            put("telefono", telefono)
        }
        writableDatabase.insert("profesional", null, valores)
    }

    fun listaProfesional(): List<Profesional> {
        val lista = mutableListOf<Profesional>()
        val cursor = readableDatabase.rawQuery(
            "SELECT rut, nombres, apellidos, genero, fechaNacimiento, especialidad, email, telefono FROM profesional",
            null
        )
        while (cursor.moveToNext()) {
            val rut = cursor.getString(0)
            val nombres = cursor.getString(1)
            val apellidos = cursor.getString(2)
            val genero = cursor.getString(3)
            val fechaNacimiento = cursor.getString(4)
            val especialidad = cursor.getString(5)
            val email = cursor.getString(6)
            val telefono = cursor.getString(7)

            lista.add(Profesional(rut, nombres, apellidos, genero, fechaNacimiento, especialidad, email, telefono))
        }
        cursor.close()
        return lista
    }

    fun eliminarProfesional(rut: String){
        writableDatabase.delete("profesional", "rut=?", arrayOf(rut))
    }
}
