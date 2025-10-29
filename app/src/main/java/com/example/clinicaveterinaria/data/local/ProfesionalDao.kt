import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.clinicaveterinaria.data.local.Profesional
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfesionalDao {
    @Query("SELECT * FROM profesionales")
    fun getAll(): Flow<List<Profesional>> // Flow para que se actualice sola

    @Query("SELECT * FROM profesionales WHERE id = :id")
    fun getById(id: Int): Flow<Profesional>

    @Insert
    suspend fun insertAll(vararg profesional: Profesional)
}