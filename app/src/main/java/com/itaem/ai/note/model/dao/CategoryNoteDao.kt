package NoteCategorycom.itaem.ai.NoteCategory.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef
import notecom.itaem.ai.note.model.relation.CategoryWithNotes
import notecom.itaem.ai.note.model.relation.NoteWithCategories

@Dao
interface CategoryNoteDao {

    //category
    @Insert
    suspend fun insert(noteCategory: NoteCategory)

    @Update
    suspend fun update(noteCategory: NoteCategory)

    @Delete
    suspend fun delete(noteCategory: NoteCategory)

    @Query("SELECT * FROM notecategory WHERE id = :id")
    fun getNoteCategory(id:Int): Flow<NoteCategory?>

    @Query("SELECT * FROM notecategory ORDER BY id ASC")
    fun getAllNoteCategories() : Flow<List<NoteCategory>>

    //note

    @Insert
    suspend fun insert(note: Note):Long

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

    @Query("select * from note where title like '%' || :str || '%' or content like '%'|| :str || '%'")
    fun getNotesByKeywords(str:String):Flow<List<Note>>

    //correspond
    @Insert
    suspend fun insert(categoryCrossRef: NoteCategoryCrossRef)

    @Update
    suspend fun update(categoryCrossRef: NoteCategoryCrossRef)

    @Delete
    suspend fun delete(categoryCrossRef: NoteCategoryCrossRef)

    @Query("delete from notecategorycrossref where noteId = :id")
    fun deleteRefByNoteId(id:Long)

    @Insert
    suspend fun insertCategoryNoteCrossRef(categoryCrossRef: NoteCategoryCrossRef)

    @Transaction
    @Query("select * from notecategory where id=:categoryId")
    fun getNotesByCategory(categoryId: Long): Flow<CategoryWithNotes?>

    @Transaction
    @Query("SELECT * FROM Note WHERE id = :noteId")
    fun getCategoriesByNote(noteId: Long): Flow<NoteWithCategories?>
}