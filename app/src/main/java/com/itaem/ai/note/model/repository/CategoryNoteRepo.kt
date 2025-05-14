package notecom.itaem.ai.note.model.repository

import NoteCategorycom.itaem.ai.NoteCategory.model.dao.CategoryNoteDao
import kotlinx.coroutines.flow.Flow
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef
import notecom.itaem.ai.note.model.relation.CategoryWithNotes
import notecom.itaem.ai.note.model.relation.NoteWithCategories

class CategoryNoteRepo(private val categoryNoteDao: CategoryNoteDao ){
    fun getAllCategoriesStream(): Flow<List<NoteCategory>> = categoryNoteDao.getAllNoteCategories()

    suspend fun insertCategory(noteCategory: NoteCategory) = categoryNoteDao.insert(noteCategory)

    suspend fun deleteCategory(noteCategory: NoteCategory) = categoryNoteDao.delete(noteCategory)

    suspend fun updateCategory(noteCategory: NoteCategory) = categoryNoteDao.update(noteCategory)

    fun getAllNotesStream(): Flow<List<Note>> = categoryNoteDao.getAllNotes()

    fun getNotesByKeywords(str:String) :Flow<List<Note>> = categoryNoteDao.getNotesByKeywords(str)

    suspend fun insertNote(note: Note) = categoryNoteDao.insert(note)

    suspend fun deleteNote(note: Note) = categoryNoteDao.delete(note)

    suspend fun updateNote(note: Note) = categoryNoteDao.update(note)

    suspend fun insertNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) =
        categoryNoteDao.insertCategoryNoteCrossRef(noteCategoryCrossRef)

    suspend fun updateNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) =
        categoryNoteDao.update(noteCategoryCrossRef)

    suspend fun deleteNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) =
        categoryNoteDao.delete(noteCategoryCrossRef)

    fun deleteRefByNoteId(id:Long) = categoryNoteDao.deleteRefByNoteId(id)

    suspend fun getNotesByCategory(categoryId: Long): Flow<CategoryWithNotes?> =
        categoryNoteDao.getNotesByCategory(categoryId)

    suspend fun getCategoriesByNote(noteId: Long): Flow<NoteWithCategories?> =
        categoryNoteDao.getCategoriesByNote(noteId)

}