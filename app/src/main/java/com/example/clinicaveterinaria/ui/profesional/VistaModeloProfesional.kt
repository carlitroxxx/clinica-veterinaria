package com.example.clinicaveterinaria.ui.profesional

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.clinicaveterinaria.data.BaseDatos
import com.example.clinicaveterinaria.model.Profesional

class VistaModeloProfesional(context: Context): ViewModel(){
    private val db = BaseDatos(context)
    var profesionales = mutableListOf<Profesional>()
    private set

    fun cargarProfesionales(){
        profesionales.clear()
        profesionales.addAll(db.listaProfesional())
    }
    fun agregarProfesional(rut: String, nombres: String, apellidos: String, genero: String, fechaNacimiento: String, especialidad: String, email: String, telefono: String){
        db.insertarProfesional(rut, nombres, apellidos,genero, fechaNacimiento, especialidad, email, telefono)
    }
    fun eliminarProfesional(rut: String){
        db.eliminarProfesional(rut)
    }
}