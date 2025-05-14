package notecom.itaem.ai.note.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef

@Dao
interface NoteCategoryCrossRefDao {

    @Insert
    suspend fun insert(noteCategoryCrossRef: NoteCategoryCrossRef)

    @Update
    suspend fun update(noteCategoryCrossRef: NoteCategoryCrossRef)

    @Delete
    suspend fun delete(noteCategoryCrossRef: NoteCategoryCrossRef)

    @Query("select * from notecategorycrossref where categoryId=:id")
    suspend fun getNoteCrossRef(id: Int): List<Note>

    @Query("select * from notecategorycrossref where noteId=:id")
    suspend fun geCategoryCrossRef(id: Int): List<NoteCategory>
}