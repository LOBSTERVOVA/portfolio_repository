package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface SubUnsubApi {

    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/api/subscribe")
    suspend fun subUnsub(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("action") action:String,
        @Query("sr_name") st_name: String,
        @Query("api_type") api_type: String = "json"
    ): JsonObject

    companion object {
        const val BASE_URL = "https://oauth.reddit.com/"
    }
}

val subUnsubRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(SubUnsubApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(SubUnsubApi::class.java)