package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface LikePostApi{

    @POST("/api/vote")
    suspend fun loadLike(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("id")id:String,
        @Query("dir")dir:Int
    ): JsonObject

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val likeRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(LikePostApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(LikePostApi::class.java)