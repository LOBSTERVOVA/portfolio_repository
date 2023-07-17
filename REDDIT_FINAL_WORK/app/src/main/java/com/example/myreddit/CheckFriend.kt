package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface CheckFriend {

    @GET("/api/v1/me/friends/{username}")
    suspend fun checkUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("username")user:String,
    ): JsonObject?

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val checkFriendRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(CheckFriend.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(CheckFriend::class.java)