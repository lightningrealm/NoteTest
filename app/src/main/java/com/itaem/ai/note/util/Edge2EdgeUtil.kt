package notecom.itaem.ai.note.util

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

object Edge2EdgeUtil {
    fun View.enablePadding(left:Int?=null, top:Int?=null, right:Int?=null, bottom:Int?=null){
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                left?:systemBars.left,
                top?:systemBars.top,
                right?:systemBars.right,
                bottom?:systemBars.bottom
            )
            insets
        }
    }

    fun View.addNavigationBarPadding(maintainBottom:Boolean) {
        val originalPadding = Rect(
            paddingLeft,
            paddingTop,
            paddingRight,
            paddingBottom
        )
        ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            view.setPadding(
                originalPadding.left,
                originalPadding.top,
                originalPadding.right,
                if (maintainBottom) {
                    originalPadding.bottom + navBarHeight
                }else{
                    navBarHeight
                }
            )
            insets
        }
        ViewCompat.requestApplyInsets(this)
    }

    fun View.enableMargin(left:Int?=null, top:Int?=null, right:Int?=null, bottom:Int?=null){
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
            val mLeft = left ?: (systemBars.left + layoutParams.leftMargin)
            val mTop = top?:(systemBars.top + layoutParams.topMargin)
            val mRight = right?:(systemBars.right + layoutParams.rightMargin)
            val mBottom = bottom?:(systemBars.bottom + layoutParams.bottomMargin)
            layoutParams.setMargins(
                mLeft,
                mTop,
                mRight,
                mBottom
            )
            insets
        }
    }

    fun View.enableMargin(left:Int?=null, top:Int?=null, right:Int?=null, bottom:Int?=null,margin:Int = 0){
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val layoutParams = this.layoutParams as ViewGroup.MarginLayoutParams
            val mLeft = left ?: (systemBars.left + margin)
            val mTop = top?:(systemBars.top + margin)
            val mRight = right?:(systemBars.right + margin)
            val mBottom = bottom?:(systemBars.bottom + margin)
            layoutParams.setMargins(
                mLeft,
                mTop,
                mRight,
                mBottom
            )
            insets
        }
    }

    //处理软键盘顶起输入框
    fun View.softInputAdjustResize(){
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.updatePadding(bottom = imeInsets.bottom)
            insets
        }
    }

}