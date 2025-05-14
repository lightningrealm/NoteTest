package com.itaem.markdown.data.api.params

data class AIMessage(
    val model:String="gpt-3.5-turbo",
    val messages:MutableList<Message>
){
    companion object{
        val defaultMessages =  mutableListOf(
            Message("system","你是一个实用的AI助理。")
        )
    }
}
data class Message(
    val role:String="user",
    val content:String
)
