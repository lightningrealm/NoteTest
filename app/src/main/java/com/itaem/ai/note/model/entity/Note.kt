package notecom.itaem.ai.note.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "Note"
)
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id:Long = 0,
    val title:String,
    val createTime:Long,
    val content:String,
    val htmlContent:String,
    val favor:Boolean = false
)