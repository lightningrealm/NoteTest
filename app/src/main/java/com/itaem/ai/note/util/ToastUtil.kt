package notecom.itaem.ai.note.util

import android.content.Context
import android.widget.Toast

object ToastUtil {
    var toast:Toast? = null
    fun Context.toast(content:String,duration:Int= Toast.LENGTH_SHORT){
        if(toast==null){
            toast = Toast.makeText(this.applicationContext,content,duration)
        }else{
            toast?.setText(content)
            toast?.duration = duration
        }
        toast?.show()
    }
}