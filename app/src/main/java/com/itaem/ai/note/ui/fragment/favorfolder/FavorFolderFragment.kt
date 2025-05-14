package notecom.itaem.ai.note.ui.fragment.favorfolder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import notecom.itaem.ai.note.R
import notecom.itaem.ai.note.common.CommonRVAdapter
import notecom.itaem.ai.note.common.DefaultUI
import notecom.itaem.ai.note.databinding.FragmentFavorFolderBinding
import notecom.itaem.ai.note.model.database.NotesDatabase
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.model.repository.CategoryNoteRepo
import notecom.itaem.ai.note.ui.dialogfragment.BottomInputDialog
import notecom.itaem.ai.note.viewmodel.CategoryNoteViewModel
import notecom.itaem.ai.note.viewmodel.factory.CommonViewModelFactory

class FavorFolderFragment : Fragment(),DefaultUI {
    private val binding by lazy {
        FragmentFavorFolderBinding.inflate(layoutInflater)
    }
    private val noteCategoryRepo by lazy {
        CategoryNoteRepo(NotesDatabase.getDataBase(requireActivity()).categoryNoteDao())
    }
    private val categoryNoteVM by lazy {
        CommonViewModelFactory<CategoryNoteViewModel,CategoryNoteRepo>(noteCategoryRepo){
            CategoryNoteViewModel(it)
        }.create(CategoryNoteViewModel::class.java)
    }
    private val favorRVAdapter by lazy {
        CommonRVAdapter<NoteCategory>(
            listOf(),
            R.layout.notes_favor_item_layout,
            {
                itemView.findViewById<MaterialCardView>(R.id.favor_item_card)
            }
        ){
            val titleText = itemView.findViewById<TextView>(R.id.favor_item_title)
            val noteCountText = itemView.findViewById<TextView>(R.id.favor_item_content)
            titleText.text = it.name
            lifecycleScope.launch {
                noteCategoryRepo.getNotesByCategory(it.id).collect{
                    noteCountText.text = "共${it?.notes?.size?:0}个笔记"
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initView()
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.favorFolderToolbar.apply {
            inflateMenu(R.menu.favor_folder_menu)
            setOnMenuItemClickListener {
                when(it.itemId){
                    R.id.favor_folder_add->{
                        val dialog = BottomInputDialog()
                        dialog.show(parentFragmentManager,"")
                        dialog.setOnSubmitListener{
                            categoryNoteVM.insertCategory(
                                NoteCategory(
                                    name = it
                                )
                            )
                        }
                    }
                }
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.notesLottieEmpty.apply {
            if (!this.isAnimating){
                progress= 1F
            }
        }
    }

    override fun initView() {
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z,false)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z,true)

        refreshUI()
        binding.favorFolderRv.apply {
            adapter = favorRVAdapter
            layoutManager = LinearLayoutManager(requireActivity()).apply {
                orientation = LinearLayoutManager.VERTICAL
            }
        }
        favorRVAdapter.apply {
            setOnItemClickListener {vh->
                val dialog = BottomInputDialog(BottomInputDialog.UPDATE_FAVOR,favorRVAdapter.items[vh.adapterPosition].name)
                dialog.show(parentFragmentManager,"")
                dialog.setOnSubmitListener{
                    categoryNoteVM.updateCategory(
                        NoteCategory(
                            favorRVAdapter.items[vh.adapterPosition].id,
                            name = it
                        )
                    )
                }
            }
            setOnItemLongClickListener {
                categoryNoteVM.deleteCategory(items[it.adapterPosition])
            }
        }


    }

    private val refreshUI:()->Unit = {
        lifecycleScope.launch {
            categoryNoteVM.categories.collectLatest {
                favorRVAdapter.apply {
                    items = it
                    notifyDataSetChanged()
                    showEmpty(it.isNotEmpty())
                }
            }
        }
    }

    private val showEmpty:(Boolean)->Unit = { isNotEmpty->
        if (isNotEmpty){
            binding.favorFolderRv.visibility = View.VISIBLE
            binding.noItemLayout.visibility = View.GONE
        }else{
            binding.favorFolderRv.visibility = View.GONE
            binding.noItemLayout.visibility = View.VISIBLE
        }
    }

}