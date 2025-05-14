package com.itaem.markdown.data.api

import com.itaem.markdown.data.api.params.AIMessage

object ApiRepo {
    //获取AI回复
    suspend fun getAIResponse(message:AIMessage) =
        RetrofitClient.createApi(ApiService::class.java).getResponse(message)

}