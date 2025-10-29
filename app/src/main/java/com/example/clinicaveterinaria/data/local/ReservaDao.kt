import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.clinicaveterinaria.data.local.Reserva
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservaDao {
    // Para RF-A10
    @Query("SELECT * FROM reservas WHERE pacienteId = :idPaciente ORDER BY fecha, hora")
    fun getByPacienteId(idPaciente: Int): Flow<List<Reserva>>

    // Para RF-A8 (Validación de solape)
    @Query("SELECT * FROM reservas WHERE profesionalId = :idProf AND fecha = :fecha AND hora = :hora")
    suspend fun findReserva(idProf: Int, fecha: String, hora: String): Reserva?

    // Para RF-A11 (Cancelar)
    @Query("UPDATE reservas SET estado = 'CANCELADA' WHERE id = :idReserva")
    suspend fun cancelarReserva(idReserva: Int)

    @Insert
    suspend fun insert(reserva: Reserva)
}