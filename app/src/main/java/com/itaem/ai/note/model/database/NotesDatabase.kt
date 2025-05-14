package notecom.itaem.ai.note.model.database

import NoteCategorycom.itaem.ai.NoteCategory.model.dao.CategoryNoteDao
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef

@Database(entities = [Note::class,NoteCategory::class,NoteCategoryCrossRef::class], version = 1)
abstract class NotesDatabase: RoomDatabase() {
    abstract fun categoryNoteDao():CategoryNoteDao
    companion object{
        @Volatile
        private var Instacne:NotesDatabase?=null
        fun getDataBase(context: Context):NotesDatabase{
            return Instacne ?: synchronized(this){
                Room.databaseBuilder(context,NotesDatabase::class.java,"notes_database")
                    /*.addCallback(object : Callback() {
                        override fun onCreate(connection: SQLiteConnection) {
                            super.onCreate(connection)
                            CoroutineScope(Dispatchers.IO).launch {
                                getDataBase(context).categoryNoteDao().insert(
                                    NoteCategory(name ="全部")
                                )
                            }
                        }
                    })*/
                    .build().also {
                        Instacne = it
                    }
            }
        }

    }
}