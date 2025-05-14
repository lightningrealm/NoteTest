package notecom.itaem.ai.note.ui.dialogfragment

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import notecom.itaem.ai.note.databinding.BottomInputLayoutBinding
import notecom.itaem.ai.note.util.Edge2EdgeUtil.enablePadding
import notecom.itaem.ai.note.util.ToastUtil.toast

class BottomInputDialog(
    private val mode: Int = NEW_INSERT,
    private val favorName: String = ""
) : DialogFragment() {
    private val binding by lazy {
        BottomInputLayoutBinding.inflate(layoutInflater)
    }

    companion object {
        const val NEW_INSERT = 0
        const val UPDATE_FAVOR = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (mode == UPDATE_FAVOR) {
            binding.bottomInsertTitle.text = "更新文件夹"
        }
        binding.bottomInsertInput.setText(favorName)
        binding.bottomInsertCancel.setOnClickListener {
            dismiss()
        }
        binding.bottomInsertSubmit.setOnClickListener {
            val content = binding.bottomInsertInput.text.toString()
            if (content.isBlank()) {
                requireActivity().toast("请输入内容", Toast.LENGTH_SHORT)
                return@setOnClickListener
            }
            onSubmit?.invoke(binding.bottomInsertInput.text.toString())
            dismiss()
        }
        return binding.root
    }

    private var onSubmit: ((String) -> Unit)? = null
    val setOnSubmitListener: ((((content: String) -> Unit)?) -> Unit) = { it ->
        this.onSubmit = it
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.apply {

        }
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
            setGravity(Gravity.BOTTOM)
            decorView.enablePadding(left = 15, right = 15, bottom = 10)
        }
    }
}