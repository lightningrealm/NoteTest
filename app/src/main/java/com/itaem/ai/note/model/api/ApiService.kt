package com.itaem.markdown.data.api

import com.itaem.markdown.data.api.params.AIMessage
import com.itaem.markdown.data.api.result.AIResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    /**
     * 获取AI回复
     */
    //headers里要放api.chatanywhere.tech提供的token
    @Headers(
    )
    @POST("/v1/chat/completions")
    suspend fun getResponse(@Body message:AIMessage):AIResponse
}