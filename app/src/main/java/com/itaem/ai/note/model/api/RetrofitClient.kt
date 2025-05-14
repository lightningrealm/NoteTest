package com.itaem.markdown.data.api

import android.net.Network
import android.util.Log
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASEURL = "https://api.chatanywhere.tech"

object RetrofitClient {
    private val instance by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor{
            Log.d("NetWork","Network:${it}")
        }
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(5,TimeUnit.SECONDS)
            .retryOnConnectionFailure(true).build()
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASEURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun<T> createApi(clazz: Class<T>):T{
        return instance.create(clazz)
    }

}