package notecom.itaem.ai.note.model.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef

data class NoteWithCategories(
    @Embedded
    val note: Note,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = NoteCategoryCrossRef::class,
            parentColumn = "noteId",
            entityColumn = "categoryId"
        )
    )
    val noteCategories:List<NoteCategory>
)
