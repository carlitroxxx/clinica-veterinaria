// data/Repository.kt
package com.example.clinicaveterinaria.data

import com.example.clinicaveterinaria.model.Cliente
import com.example.clinicaveterinaria.model.Profesional
import com.example.clinicaveterinaria.model.Reserva
import com.example.clinicaveterinaria.network.ApiClient
import com.example.clinicaveterinaria.network.BackendApi
import com.example.clinicaveterinaria.network.CrearReservaRequest
import com.example.clinicaveterinaria.network.AnimalTypesApi
import com.example.clinicaveterinaria.network.EstadoReservaRequest
import com.example.clinicaveterinaria.ui.cliente.MascotaForm

object Repository {

    private val api: BackendApi = ApiClient.backendApi

    // Resultado genérico para operaciones que pueden fallar
    data class Resultado<out T>(
        val ok: Boolean,
        val data: T? = null,
        val mensaje: String? = null
    )

    // ========= TIPOS ANIMAL (ya lo tenías) =========

    suspend fun obtenerTiposAnimalDesdeApi(): List<String> {
        return try {
            AnimalTypesApi.service.getAnimalTypes()
        } catch (e: Exception) {
            listOf("Perro", "Gato", "Caballo")
        }
    }

    // ========= PROFESIONALES =========

    suspend fun obtenerProfesionales(): List<Profesional> {
        return try {
            api.getProfesionales()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun obtenerProfesional(rut: String): Profesional? {
        if (rut.isBlank()) return null
        return try {
            api.getProfesional(rut)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun agregarProfesional(p: Profesional): Resultado<Profesional> {
        return try {
            val creado = api.createProfesional(p)
            Resultado(ok = true, data = creado)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "Error al crear profesional")
        }
    }

    suspend fun actualizarProfesional(p: Profesional): Resultado<Profesional> {
        return try {
            val actualizado = api.updateProfesional(p.rut, p)
            Resultado(ok = true, data = actualizado)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "Error al actualizar profesional")
        }
    }

    suspend fun eliminarProfesional(rut: String): Resultado<Unit> {
        return try {
            api.deleteProfesional(rut)
            Resultado(ok = true)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "Error al eliminar profesional")
        }
    }

    suspend fun validarProfesional(email: String, password: String): Boolean {
        // Mientras no usemos /auth/login para profesionales, hacemos esto simple:
        if (email.isBlank() || password.isBlank()) return false
        val profesionales = obtenerProfesionales()
        return profesionales.any {
            it.email.equals(email, ignoreCase = true) && it.password == password
        }
    }

    // ========= CLIENTES =========

    suspend fun agregarCliente(c: Cliente): Resultado<Cliente> {
        return try {
            val creado = api.crearCliente(c)
            Resultado(ok = true, data = creado)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "No se pudo guardar el cliente")
        }
    }

    suspend fun obtenerClientePorEmail(email: String): Cliente? {
        if (email.isBlank()) return null
        return try {
            api.getClientePorEmail(email)
        } catch (_: Exception) {
            null
        }
    }

    // ========= MASCOTAS =========

    suspend fun agregarMascota(form: MascotaForm): Resultado<Unit> {
        if (form.clienteRut.isBlank() || form.nombre.isBlank() || form.especie.isBlank()) {
            return Resultado(false, mensaje = "Faltan campos obligatorios")
        }
        return try {
            api.crearMascota(form)  // Ignoramos el cuerpo, solo nos importa que no falle
            Resultado(ok = true)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "No se pudo guardar la mascota")
        }
    }

    suspend fun clienteTieneMascota(rutCliente: String): Boolean {
        if (rutCliente.isBlank()) return false
        return try {
            api.clienteTieneMascota(rutCliente)
        } catch (_: Exception) {
            false
        }
    }

    // ========= RESERVAS =========

    suspend fun agregarReserva(
        clienteRut: String,
        profesionalRut: String,
        fecha: String,
        hora: String,
        servicio: String
    ): Resultado<Reserva> {
        if (clienteRut.isBlank() || profesionalRut.isBlank() ||
            fecha.isBlank() || hora.isBlank() || servicio.isBlank()
        ) {
            return Resultado(false, mensaje = "Faltan campos obligatorios")
        }

        return try {
            val body = CrearReservaRequest(
                clienteRut = clienteRut.trim(),
                profesionalRut = profesionalRut.trim(),
                fecha = fecha.trim(),
                hora = hora.trim(),
                servicio = servicio.trim()
            )
            val reservaCreada = api.crearReserva(body)
            Resultado(ok = true, data = reservaCreada)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "No se pudo crear la reserva")
        }
    }

    suspend fun obtenerReservasCliente(rutCliente: String): List<Reserva> {
        if (rutCliente.isBlank()) return emptyList()
        return try {
            api.getReservasCliente(rutCliente)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun cancelarReserva(idReserva: Long): Resultado<Unit> {
        return try {
            api.cancelarReserva(idReserva)
            Resultado(ok = true)
        } catch (e: Exception) {
            Resultado(ok = false, mensaje = e.message ?: "No se pudo cancelar la reserva")
        }
    }

    suspend fun horasDisponibles(profRut: String, fecha: String): List<String> {
        if (profRut.isBlank() || fecha.isBlank()) return emptyList()
        return try {
            api.getHorasDisponibles(profRut, fecha)
        } catch (_: Exception) {
            emptyList()
        }
    }

    // ========= PROFESIONAL extra =========

    suspend fun obtenerProfesionalPorEmail(email: String): Profesional? {
        if (email.isBlank()) return null
        return try {
            api.getProfesionalPorEmail(email)
        } catch (_: Exception) {
            null
        }
    }

// ========= RESERVAS para profesional =========

    suspend fun obtenerReservasProfesionalEn(
        rutProfesional: String,
        fecha: String
    ): List<Reserva> {
        if (rutProfesional.isBlank() || fecha.isBlank()) return emptyList()
        return try {
            api.getReservasProfesionalEn(rutProfesional, fecha)
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun actualizarEstadoReserva(
        idReserva: Long,
        estadoNuevo: String
    ): Boolean {
        if (idReserva <= 0) return false
        return try {
            api.actualizarEstadoReserva(
                idReserva,
                EstadoReservaRequest(estadoNuevo = estadoNuevo)
            )
            true
        } catch (_: Exception) {
            false
        }
    }



}
