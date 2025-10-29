import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clinicaveterinaria.data.local.Profesional
import com.example.clinicaveterinaria.data.local.Reserva

@Database(entities = [Profesional::class, Reserva::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun profesionalDao(): ProfesionalDao
    abstract fun reservaDao(): ReservaDao
}