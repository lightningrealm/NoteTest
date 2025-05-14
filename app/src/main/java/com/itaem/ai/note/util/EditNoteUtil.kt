package notecom.itaem.ai.note.util

import android.os.Build
import android.text.Editable
import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText
import androidx.core.text.HtmlCompat

object EditNoteUtil {
    //
    fun EditText.loadEditTextContent(content:String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.setText(
                Html.fromHtml(
                    content,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                )
            )
        } else {
            this.setText(Html.fromHtml(content))
        }
        //每次读取莫名多一行 删除即可
        val newText = this.text.dropLast(1)
        this.setText(newText)
    }
    //
    fun EditText.htmlContentCompat(): String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        Html.toHtml(this.text, HtmlCompat.TO_HTML_PARAGRAPH_LINES_INDIVIDUAL)
    } else {
        Html.toHtml(this.text)
    }
    //
    fun EditText.applySpan(span: Any) {
        val start = this.selectionStart
        val end = this.selectionEnd

        if (start in 0..<end) {
            val text = this.text as SpannableStringBuilder
            val spans = text.getSpans(selectionStart, selectionEnd, span.javaClass)
            val shouldRemove = spans.any{existingSpan->
                if (span is StyleSpan && existingSpan is StyleSpan){
                    existingSpan.style == span.style
                }else{
                    true
                }
            }
            if (shouldRemove){
                spans.forEach {existingSpan->
                    if (span !is StyleSpan || (existingSpan is StyleSpan && existingSpan.style == span.style)) {
                        text.removeSpan(existingSpan)
                    }
                }
            }else {
                this.text.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
    fun Editable.checkSpansInSelection(
        start: Int,
        end: Int,
        typeface: Int? = null, // 可选参数，用于检查 StyleSpan 的样式
        underline: Boolean = false, // 是否检查下划线
        relativeSize: Float? = null // 可选参数，用于检查字体大小
    ): Boolean {
        // 检查 StyleSpan (加粗或斜体)
        if (typeface != null) {
            val styleSpans = this.getSpans(start, end, StyleSpan::class.java)
            val isUsedType = styleSpans.any { it.style == typeface }
            if (isUsedType) return true
        }

        // 检查下划线
        if (underline) {
            val underlineSpans = this.getSpans(start, end, UnderlineSpan::class.java)
            if (underlineSpans.isNotEmpty()) return true
        }

        // 检查字体大小
        if (relativeSize != null) {
            val sizeSpans = this.getSpans(start, end, RelativeSizeSpan::class.java)
            val isUsedSize = sizeSpans.any { it.sizeChange == relativeSize }
            if (isUsedSize) return true
        }

        return false // 如果没有匹配到任何 Span，返回 false
    }

}