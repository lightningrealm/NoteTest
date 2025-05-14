package notecom.itaem.ai.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef
import notecom.itaem.ai.note.model.relation.CategoryWithNotes
import notecom.itaem.ai.note.model.relation.NoteWithCategories
import notecom.itaem.ai.note.model.repository.CategoryNoteRepo

class CategoryNoteViewModel(private val categoryNoteRepo: CategoryNoteRepo): ViewModel() {
    private val _notes = MutableStateFlow<List<Note>>(mutableListOf())
    val notes:StateFlow<List<Note>> get() = _notes.asStateFlow()
    private val _categories = MutableStateFlow<List<NoteCategory>>(mutableListOf())
    val categories:StateFlow<List<NoteCategory>> get()= _categories.asStateFlow()
    private var _noteWithCategories  = MutableStateFlow<NoteWithCategories?>(null)
    val noteWithCategories = _noteWithCategories.asStateFlow()
    private var _categoryWithNotes  = MutableStateFlow<CategoryWithNotes?>(null)
    val categoryWithNotes = _categoryWithNotes.asStateFlow()
    init {
        viewModelScope.launch {
            categoryNoteRepo.getAllNotesStream().collect{
                _notes.value = it
            }
        }
        viewModelScope.launch {
            categoryNoteRepo.getAllCategoriesStream().collect{
                _categories.value = it
            }
        }
    }

    fun insertNote(note:Note) = viewModelScope.async {
        categoryNoteRepo.insertNote(note)
    }

    fun updateNote(note:Note) = viewModelScope.launch {
            categoryNoteRepo.updateNote(note)
        }


    fun deleteNote(note:Note){
        viewModelScope.launch {
            categoryNoteRepo.deleteNote(note)
            _notes.value = _notes.value.toMutableList().apply {
                remove(note)
            }
        }
    }

    fun insertCategory(noteCategory: NoteCategory) = viewModelScope.launch {
        categoryNoteRepo.insertCategory(noteCategory)
    }

    fun updateCategory(noteCategory: NoteCategory) = viewModelScope.launch {
        categoryNoteRepo.updateCategory(noteCategory)
        //_categories.value = _categories.value.toMutableList()
    }

    fun deleteCategory(noteCategory: NoteCategory) = viewModelScope.launch {
        categoryNoteRepo.deleteCategory(noteCategory)
        _categories.value = _categories.value.toMutableList().apply {
            remove(noteCategory)
        }
    }

    fun getCategoriesByNote(id:Long) = viewModelScope.launch {
        categoryNoteRepo.getCategoriesByNote(id).collect{
            _noteWithCategories.value = it
        }
    }

    fun getCategoriesByNoteThenSelect(id:Long) = viewModelScope.launch {
        categoryNoteRepo.getCategoriesByNote(id).collect{
            _noteWithCategories.value = it.also {
                it?.noteCategories?.forEach {
                    it.isSelected = true
                }
            }
        }
    }

    //先定义一个空协程任务
    private var getNotesJob: Job? = null


    fun getAllNotes() {
        getNotesJob?.cancel()
        getNotesJob = viewModelScope.launch {
            categoryNoteRepo.getAllNotesStream().collectLatest {
                _notes.value = it.toMutableList()
            }
        }
    }

    fun getNotesByCategory(id:Long) {
        //如果之前的协程任务部为空 则先取消
        getNotesJob?.cancel()
        //取消后再执行新协程任务 不然之前的协程任务可能没有cancel 会和当前协程并发修改数据
        getNotesJob = viewModelScope.launch {
            categoryNoteRepo.getNotesByCategory(id).collectLatest {
                _categoryWithNotes.value = it
                it?.notes?.let {
                    _notes.value = it.toMutableList()
                }
            }
        }
    }

    fun getNotesBySearch(str:String){
        getNotesJob?.cancel()
        getNotesJob = viewModelScope.launch {
            if (str.isNotBlank()) {
                categoryNoteRepo.getNotesByKeywords(str).collectLatest {
                    _notes.value = it.toMutableList()
                }
            }else{
                categoryNoteRepo.getAllNotesStream().collectLatest {
                    _notes.value = it.toMutableList()
                }
            }
        }
    }

    fun insertNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) = viewModelScope.launch {
        categoryNoteRepo.insertNoteCategoryCrossRef(noteCategoryCrossRef)
    }

    fun updateNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) = viewModelScope.launch {
        categoryNoteRepo.updateNoteCategoryCrossRef(noteCategoryCrossRef)
    }

    fun deleteNoteCategoryCrossRef(noteCategoryCrossRef: NoteCategoryCrossRef) = viewModelScope.launch {
        categoryNoteRepo.deleteNoteCategoryCrossRef(noteCategoryCrossRef)
    }

    fun deleteRefByNoteId(id:Long) = viewModelScope.async(Dispatchers.IO) {
        categoryNoteRepo.deleteRefByNoteId(id)
    }
}