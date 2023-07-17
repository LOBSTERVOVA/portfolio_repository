package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface MyProfileApi {

    @GET("/api/v1/me")
    suspend fun loadMe(
        @Header("Authorization") token: String = TokenKeeper.token!!,
    ): MyProfile

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val myProfileRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(MyProfileApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(MyProfileApi::class.java)

data class MyProfile(
    val name:String,
    val icon_img:String,
    val created_utc:Long,
    val num_friends:Int,
)