package com.itaem.markdown.data.api.result

import com.google.gson.annotations.SerializedName
import com.itaem.markdown.data.api.params.Message

data class AIResponse(
    val id:String,
    val choices:List<Choice>,
    val created:Long,
    val model:String,
    @SerializedName("object")val usedModel:String
)
data class Choice(
    val index:Int,
    val message:Message
)