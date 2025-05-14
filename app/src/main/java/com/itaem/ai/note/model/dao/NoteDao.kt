package notecom.itaem.ai.note.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import notecom.itaem.ai.note.model.entity.Note

@Dao
interface NoteDao {
    @Insert
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note WHERE id = :id")
    fun getNote(id:Int): Flow<Note?>

    @Query("SELECT * FROM note ORDER BY id ASC")
    fun getAllNotes() : Flow<List<Note>>


    @Query("SELECT * FROM note ORDER BY title ASC")
    fun getAllNotesByTitle() : Flow<List<Note>>
}