package notecom.itaem.ai.note.ui.fragment.notelist

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.chip.Chip
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.CommonRVAdapter
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.FragmentNoteListBinding
import notecom.itaem.ai.note.model.database.NotesDatabase
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.repository.CategoryNoteRepo
import notecom.itaem.ai.note.ui.fragment.editnote.EditNoteFragment
import notecom.itaem.ai.note.ui.fragment.maincontainer.MainContainerFragmentDirections
import notecom.itaem.ai.note.util.HashColor
import notecom.itaem.ai.note.util.ToastUtil.toast
import notecom.itaem.ai.note.viewmodel.CategoryNoteViewModel
import notecom.itaem.ai.note.viewmodel.factory.CommonViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale


class NoteListFragment : Fragment(),DefaultUI{
    private val categoryNoteViewModel by lazy {
        CommonViewModelFactory(categoryNoteRepo){
            CategoryNoteViewModel(it)
        }.create(CategoryNoteViewModel::class.java)
    }
    private val binding by lazy {
        FragmentNoteListBinding.inflate(layoutInflater)
    }
    private val categoryNoteRepo by lazy {
        CategoryNoteRepo(NotesDatabase.getDataBase(requireActivity()).categoryNoteDao())
    }

    private val notesAdapter by lazy {
        CommonRVAdapter<Note>(listOf(),R.layout.notes_item_layout,
            {
                this.itemView.findViewById<MaterialCardView>(R.id.notes_item_card)
            }){
            val titleText = itemView.findViewById<TextView>(R.id.notes_item_title)
            val createTimeText = itemView.findViewById<TextView>(R.id.notes_item_create_time)
            val contentText = itemView.findViewById<TextView>(R.id.notes_item_content)
            val circleShape = itemView.findViewById<View>(R.id.notes_item_color_circle)
            val categoriesRV = itemView.findViewById<RecyclerView>(R.id.notes_item_categories)
            val timeFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(it.createTime)
            titleText.text = it.title
            createTimeText.text = timeFormat
            contentText.text = it.content
            //初始化hashColor
            var generateColor = 1
            //获取控件的圆形drawable
            val gradientDrawable = circleShape.background as (GradientDrawable)
            val itemCategoriesAdapter = CommonRVAdapter<NoteCategory>(listOf(),R.layout.category_select){ note ->
                val textView = itemView.findViewById<TextView>(R.id.categoryName)
                textView.text = note.name
            }
            lifecycleScope.launch {
                categoryNoteRepo.getCategoriesByNote(it.id).collect{ noteWithCategories ->
                    noteWithCategories?.noteCategories?.let {
                        itemCategoriesAdapter.apply {
                            items = it
                            notifyDataSetChanged()
                            //通过it生产hashColor
                            generateColor = HashColor.generatePleasantColorFromHash(it.hashCode())
                            gradientDrawable.setColor(generateColor)
                        }
                    }
                }
            }
            categoriesRV.adapter = itemCategoriesAdapter
            categoriesRV.layoutManager = LinearLayoutManager(requireActivity()).also { layoutManager ->
                layoutManager.orientation = LinearLayoutManager.HORIZONTAL
            }
        }
    }
    private val parentNavCtrl by lazy{
        requireActivity().findNavController(R.id.fragment_container)
    }
    private val lmVertical by lazy {
        LinearLayoutManager(requireActivity()).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
    }
    private var transitionname = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.noteListToolbar.apply{
            inflateMenu(R.menu.notelist_menu)
            setOnMenuItemClickListener{
                requireActivity().toast("开发中")
                true
            }
        }
        initView()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        when(binding.notesLottieEmpty.visibility){
            View.VISIBLE->{
                binding.notesLottieEmpty.apply {
                    if (!this.isAnimating){
                        //progress= 1F
                        playAnimation()
                    }
                }
            }
            else->{
               //refreshRv()
            }
        }
    }

    override fun initView() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,true)
        refreshRv()
        binding.notesRv.apply {
            adapter = notesAdapter
            layoutManager = lmVertical
        }
        notesAdapter.apply {
            setOnItemClickListener {vh->
                val position = vh.layoutPosition
                val temp = SystemClock.currentThreadTimeMillis()
                transitionname = "$temp${position}"
                val view = binding.notesRv.layoutManager?.findViewByPosition(position)!!
                view.transitionName = transitionname
                    val direction = MainContainerFragmentDirections.actionMainContainerFragmentToAddNoteFragment(
                        transitionname,
                        EditNoteFragment.MODE_EDIT,
                        notesAdapter.items[position].id.toString(),
                        notesAdapter.items[position].title,
                        notesAdapter.items[position].createTime.toString(),
                        notesAdapter.items[position].htmlContent
                    )
                    parentNavCtrl.navigate(
                        direction,
                        FragmentNavigatorExtras(view to transitionname)
                    )
                }
            setOnItemLongClickListener {
                val position = it.adapterPosition
                categoryNoteViewModel.deleteNote(notesAdapter.items[position])
            }
        }
        binding.showAllNotesChip.setOnClickListener {
            binding.noteSearchEdit.clearEditFocus()
            categoryNoteViewModel.getAllNotes()
        }
        //搜索框
        binding.noteSearchEdit.addTextChangedListener (onTextChanged = {text, start, before, count ->
            binding.showAllNotesChip.isChecked = true
            categoryNoteViewModel.getNotesBySearch(text.toString())
        })

    }

    private fun refreshRv(){
        lifecycleScope.launch {
            categoryNoteViewModel.categories.collectLatest{
                refreshCategoriesUI(it)
            }
        }
        lifecycleScope.launch {
            categoryNoteViewModel.notes.collectLatest{
                notesAdapter.items = it
                showEmpty(notesAdapter.items.isNotEmpty())
                notesAdapter.notifyDataSetChanged()
                Log.d("tta",it.size.toString())
            }
        }
    }

    private val refreshCategoriesUI:(List<NoteCategory>)->Unit = { categories->
        categories.forEach {category->
            val chip = Chip(requireActivity()).apply {
                text = category.name
                isCheckable = true
                isChecked = false
                setOnClickListener {
                    binding.noteSearchEdit.clearEditFocus()
                    refreshNoteUIByCategory(category)
                }
            }
            binding.chooseFavorChipGroup.addView(chip)
        }
    }

    private val refreshNoteUIByCategory:(NoteCategory)->Unit = {
        categoryNoteViewModel.getNotesByCategory(it.id)
    }

    private val showEmpty:(Boolean)->Unit = { isNotEmpty->
        if (isNotEmpty){
            binding.notesRv.visibility = View.VISIBLE
            binding.noItemLayout.visibility = View.GONE
        }else{
            binding.notesRv.visibility = View.GONE
            binding.noItemLayout.visibility = View.VISIBLE
        }
    }

    private fun EditText.clearEditFocus(){
        if (this.isFocused){
            this.clearFocus()
            val imm = ContextCompat.getSystemService(requireActivity(),InputMethodManager::class.java)
            imm?.hideSoftInputFromWindow(this.windowToken,InputMethodManager.RESULT_UNCHANGED_SHOWN)
            return
        }
    }
}