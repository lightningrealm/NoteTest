package notecom.itaem.ai.note.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class SelectableEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr:Int = android.R.attr.editTextStyle
) : AppCompatEditText(context, attrs,defStyleAttr) {
    private var selectionChangeListener: ((start: Int, end: Int) -> Unit)? = null

    // 设置选中变化监听器
    fun setOnSelectionChangedListener(listener: (start: Int, end: Int) -> Unit) {
        selectionChangeListener = listener
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        selectionChangeListener?.invoke(selStart, selEnd)
    }
}