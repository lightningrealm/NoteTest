package notecom.itaem.ai.note.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

    @Entity(tableName = "NoteCategory")
    data class NoteCategory(
        @PrimaryKey(autoGenerate = true)
        val id:Long=0,
        val name:String
    ){
        var isSelected = false
    }
