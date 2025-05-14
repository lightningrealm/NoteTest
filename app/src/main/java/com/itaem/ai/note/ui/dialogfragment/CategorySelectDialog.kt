package notecom.itaem.ai.note.ui.dialogfragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import notecom.itaem.ai.note.common.ChipRVAdapter
import notecom.itaem.ai.note.databinding.CategorySelectDialogLayoutBinding
import notecom.itaem.ai.note.model.entity.NoteCategory
import notecom.itaem.ai.note.util.Edge2EdgeUtil.enablePadding

class CategorySelectDialog(
    private val list:List<NoteCategory>
) : DialogFragment() {
    private val binding by lazy {
        CategorySelectDialogLayoutBinding.inflate(layoutInflater)
    }
    private val rvAdapter by lazy {
        ChipRVAdapter(list)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (list.isEmpty()){
            binding.categorySelectRv.visibility = View.GONE
            binding.notesEmptyText.visibility = View.VISIBLE
        }else{
            binding.categorySelectRv.visibility = View.VISIBLE
            binding.notesEmptyText.visibility = View.GONE
        }
        binding.categorySelectRv.apply {
            adapter = rvAdapter
            val lm = LinearLayoutManager(requireActivity())
            lm.orientation = LinearLayoutManager.VERTICAL
            layoutManager = lm
        }
        binding.categorySelectCancel.setOnClickListener {
            dismiss()
        }
        binding.categorySelectAffirm.setOnClickListener {
            onSubmit?.invoke(
                rvAdapter.items.filter {
                    it.isSelected
                }
            )
        }
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setGravity(Gravity.CENTER)
            decorView.enablePadding(left = 15, right = 15, bottom = 10)
        }
    }

    private var onSubmit: ((selectedCategories:List<NoteCategory>) -> Unit)? = null
    val setOnSubmitListener: (((selectedCategories:List<NoteCategory>) -> Unit)?) -> Unit = { it ->
        this.onSubmit = it
    }
}