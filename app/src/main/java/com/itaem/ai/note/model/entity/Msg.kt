package notecom.itaem.ai.note.model.entity

data class Msg(
    val content:String,
    val type:Int,
    var isAnimated:Boolean = false
) {
    companion object{
        val TYPE_RECEIVED: Int = 0
        val TYPE_SENT: Int = 1
    }

}