package com.example.myreddit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SubUserApi {

    @POST("/api/subscribe")
    suspend fun subUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("action")action:String = "sub",
        @Query("sr")sr:String,
        @Query("name")name:String,
        @Query("api_type")api_type:String = "json"
    ): UserResponse

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val subUserRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(SubUserApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(SubUserApi::class.java)