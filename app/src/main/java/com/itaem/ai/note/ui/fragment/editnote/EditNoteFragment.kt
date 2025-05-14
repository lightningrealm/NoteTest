package notecom.itaem.ai.note.ui.fragment.editnote

import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.InputType
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.CommonRVAdapter
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.common.UpdateUI
import notecom.itaem.ai.note.databinding.EditNoteToolbarBinding
import notecom.itaem.ai.note.databinding.FragmentEditNoteBinding
import notecom.itaem.ai.note.model.database.NotesDatabase
import notecom.itaem.ai.note.model.entity.Note
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.entity.NoteCategoryCrossRef
import notecom.itaem.ai.note.model.repository.CategoryNoteRepo
import notecom.itaem.ai.note.ui.dialogfragment.CategorySelectDialog
import notecom.itaem.ai.note.util.ContainerTransform.getFragmentContainerTransform
import notecom.itaem.ai.note.util.Edge2EdgeUtil.enablePadding
import notecom.itaem.ai.note.util.Edge2EdgeUtil.softInputAdjustResize
import notecom.itaem.ai.note.util.EditNoteUtil.applySpan
import notecom.itaem.ai.note.util.EditNoteUtil.checkSpansInSelection
import notecom.itaem.ai.note.util.EditNoteUtil.htmlContentCompat
import notecom.itaem.ai.note.util.EditNoteUtil.loadEditTextContent
import notecom.itaem.ai.note.util.ToastUtil.toast
import notecom.itaem.ai.note.viewmodel.CategoryNoteViewModel
import notecom.itaem.ai.note.viewmodel.factory.CommonViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class EditNoteFragment : Fragment(),DefaultUI,UpdateUI {
    companion object {
        //NOTE_MODE
        const val MODE_ADD = 0
        const val MODE_EDIT = 1
    }


    private var isSpaceStyleEnabled = false

    private val binding by lazy {
        FragmentEditNoteBinding.inflate(layoutInflater)
    }
    private val toolBinding by lazy {
        EditNoteToolbarBinding.bind(binding.addNoteTools.root)
    }
    private var categories:MutableList<NoteCategory> = mutableListOf()
    private var categoriesSelected:MutableList<NoteCategory> = mutableListOf()
    private val parentNavCtrl by lazy{
        requireActivity().findNavController(R.id.fragment_container)
    }
    private val categoryNoteRepo by lazy {
        CategoryNoteRepo(NotesDatabase.getDataBase(requireActivity()).categoryNoteDao())
    }
    private val categoryNoteVM by lazy {
        CommonViewModelFactory(categoryNoteRepo){
            CategoryNoteViewModel(it)
        }.create(CategoryNoteViewModel::class.java)
    }
    val itemCategoriesAdapter by lazy {
        CommonRVAdapter<NoteCategory>(listOf(),R.layout.category_select){
            val textView = itemView.findViewById<TextView>(R.id.categoryName)
            textView.text = it.name
        }
    }
    private val args by navArgs<EditNoteFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        updateUI()
        binding.root.softInputAdjustResize()
        return binding.root
    }

    override fun initView() {
        binding.root.transitionName = args.transitionName
        val containerTransform = getFragmentContainerTransform(R.id.fragment_container,350)
        sharedElementEnterTransition = containerTransform
        sharedElementReturnTransition = containerTransform
        binding.editNoteBottomLayout.enablePadding(0,0,0)

        binding.editNoteCategoryRv.apply {
            val lm =  LinearLayoutManager(requireActivity())
            lm.orientation = LinearLayoutManager.HORIZONTAL
            layoutManager = lm
            adapter = itemCategoriesAdapter
        }
        binding.getBackButton.setOnClickListener {
            parentNavCtrl.popBackStack()
        }
        addEffect()
        when(args.mode){
            MODE_ADD->{
                initAddView()
            }
            MODE_EDIT->{
                initEditView()
            }
        }
        binding.editNoteContent.apply {
            setOnSelectionChangedListener { start, end ->
                if(start!=end){
                    val isTitle = this.text?.checkSpansInSelection(start,end, relativeSize = 1.5f)
                    isTitle?.let {
                        toolBinding.chipTitle.isChecked = it
                    }
                    val isBold = this.text?.checkSpansInSelection(start,end,Typeface.BOLD)
                    isBold?.let {
                        toolBinding.chipBold.isChecked = it
                    }
                    val isItalic = this.text?.checkSpansInSelection(start,end,Typeface.ITALIC)
                    isItalic?.let {
                        toolBinding.chipItalic.isChecked = it
                    }
                    val isUnderline = this.text?.checkSpansInSelection(start,end, underline = true)
                    isUnderline?.let {
                        toolBinding.chipUnderline.isChecked = it
                    }
                }else{
                    toolBinding.chipTitle.isChecked = false
                    toolBinding.chipBold.isChecked = false
                    toolBinding.chipItalic.isChecked = false
                    toolBinding.chipUnderline.isChecked = false

                }
            }
        }
        binding.editAiImg.setOnClickListener {
            val temp = SystemClock.currentThreadTimeMillis()
            val navDirections = EditNoteFragmentDirections.actionAddNoteFragmentToAiFragment(
                transitionName = "${temp}${it}"
            )
            it.transitionName = "${temp}${it}"
            parentNavCtrl.navigate(
                navDirections,
                FragmentNavigatorExtras(it to "${temp}${it}")
            )
        }
    }

    private val initAddView = {
        binding.editNoteContent.enablePadding(0,0,0)
        binding.categoriesAddLabel.setOnClickListener {
            val categorySelectDialog = CategorySelectDialog(categories)
            categorySelectDialog.show(parentFragmentManager,"")
            categorySelectDialog.setOnSubmitListener{
                //这些添加到多对多关系 更新ui 点击保存按钮时才保存
                categoriesSelected = it.toMutableList()
                itemCategoriesAdapter.updateItems(categoriesSelected)
                categorySelectDialog.dismiss()
            }
        }
        binding.editNoteSaveButton.setOnClickListener {
            //插入笔记
            val title = binding.editNoteTitleEdit.text.toString()
            val content = binding.editNoteContent.text.toString()
            val htmlContent = binding.editNoteContent.htmlContentCompat()
            val currentTime = System.currentTimeMillis()
            if(title.isBlank()){
                requireActivity().toast("请输入标题")
                return@setOnClickListener
            }
            //更新note
            val id = categoryNoteVM.insertNote(
                Note(
                    title = title,
                    createTime = currentTime,
                    content = content,
                    htmlContent = htmlContent
                )
            )
            lifecycleScope.launch {
                categoriesSelected.forEach {
                    categoryNoteVM.insertNoteCategoryCrossRef(
                        NoteCategoryCrossRef(
                            id.await(),
                            it.id
                        )
                    )
                }
            }

            parentNavCtrl.popBackStack()
        }
    }

    private val initEditView = {
        val noteID = args.id.toLong()
        categoryNoteVM.getCategoriesByNoteThenSelect(noteID)
        binding.editNoteTitleEdit.setText(args.title)
        binding.editNoteContent.loadEditTextContent(args.content)
        binding.createTimeText.visibility = View.VISIBLE
        val timeFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault()).format(args.createTime.toLong())
        binding.createTimeText.text = "创建时间：$timeFormat"
        binding.categoriesAddLabel.setOnClickListener {
            val categorySelectDialog = CategorySelectDialog(categories)
            categorySelectDialog.show(parentFragmentManager,"")
            categorySelectDialog.setOnSubmitListener{
                //这些添加到多对多关系
                categoriesSelected = it.toMutableList()
                itemCategoriesAdapter.updateItems(categoriesSelected)
                categorySelectDialog.dismiss()
            }
        }
        binding.editNoteSaveButton.setOnClickListener {
            //插入笔记
            val title = binding.editNoteTitleEdit.text.toString()
            val content = binding.editNoteContent.text.toString()
            val htmlContent = binding.editNoteContent.htmlContentCompat()
            if(title.isBlank()){
                requireActivity().toast("请输入标题")
                return@setOnClickListener
            }
            categoryNoteVM.updateNote(
                Note(
                    id = args.id.toLong(),
                    title = title,
                    createTime = args.createTime.toLong(),
                    content = content,
                    htmlContent = htmlContent
                )
            )
            val job = categoryNoteVM.deleteRefByNoteId(
                args.id.toLong()
            )
            lifecycleScope.launch {
                job.await()
                categoriesSelected.forEach {
                    categoryNoteVM.insertNoteCategoryCrossRef(
                        NoteCategoryCrossRef(
                            args.id.toLong(),
                            it.id
                        )
                    )
                }
            }

            parentNavCtrl.popBackStack()
        }
    }

    private fun addEffect(){
        val editText = binding.editNoteContent
        editText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        toolBinding.apply {
            chipTitle.setOnClickListener {
                editText.applySpan(RelativeSizeSpan(1.5f))
            }
            chipBold.setOnClickListener {
                editText.applySpan(StyleSpan(Typeface.BOLD))
            }
            chipItalic.setOnClickListener {
                editText.applySpan(StyleSpan(Typeface.ITALIC))
            }
            chipUnderline.setOnClickListener {
                editText.applySpan(UnderlineSpan())
            }
        }
    }


    override fun updateUI() {
        lifecycleScope.launch {
            categoryNoteVM.categories.collect(){
                categories = it.toMutableList()
            }
        }
        lifecycleScope.launch {
            categoryNoteVM.noteWithCategories.collectLatest {
                it?.noteCategories?.let {_categoriesSelected->
                    //更新选过的标签
                    categoriesSelected = _categoriesSelected.toMutableList()
                    //更新所有标签的选择状态
                    categories = categories.map { noteCategory ->
                        _categoriesSelected.find {
                            it.isSelected!=noteCategory.isSelected &&
                                    it.name == noteCategory.name
                        }?:noteCategory
                    }.toMutableList()
                }
                itemCategoriesAdapter.updateItems(categoriesSelected)
            }
        }
    }

}