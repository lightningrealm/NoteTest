package notecom.itaem.ai.note.model.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef

data class CategoryWithNotes(
    @Embedded
    val category: NoteCategory,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteCategoryCrossRef::class,
            parentColumn = "categoryId",
            entityColumn = "noteId"
        )
    )
    val notes:List<Note>
) {
}