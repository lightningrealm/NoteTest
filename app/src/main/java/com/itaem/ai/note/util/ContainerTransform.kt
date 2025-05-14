package notecom.itaem.ai.note.util

import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialContainerTransform

object ContainerTransform {
    fun Fragment.getFragmentContainerTransform(
        mDrawingViewId:Int?=null,
        mDuration: Long?=null,
    ): MaterialContainerTransform {
        val containerTransform = MaterialContainerTransform()
            .apply {
            mDrawingViewId?.let {
                drawingViewId = it
            }
            duration = mDuration?:300
            scrimColor = Color.TRANSPARENT
            val typeValue = TypedValue()
            val surfaceColor = requireActivity().theme?.resolveAttribute(com.google.android.material.R.attr.colorSurface,typeValue,true)
            setAllContainerColors(ContextCompat.getColor(requireActivity(),typeValue.resourceId))
        }
        return containerTransform
    }
}