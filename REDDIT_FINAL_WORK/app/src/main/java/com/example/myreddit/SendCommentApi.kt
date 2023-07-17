package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface SendCommentApi{

    @POST("/api/comment")
    suspend fun sendComment(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("text")text:String,
        @Query("thing_id")thing_id:String,
        @Query("api_type=")api_type:String = "json",
    ): JsonObject?

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val sendRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(SendCommentApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(SendCommentApi::class.java)