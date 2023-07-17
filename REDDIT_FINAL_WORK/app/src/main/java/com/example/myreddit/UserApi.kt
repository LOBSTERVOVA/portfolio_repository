package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface UserApi{

    @GET("/user/{user}/about/")
    suspend fun loadUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("user")user:String,
    ): UserResponse

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val userRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(UserApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(UserApi::class.java)

data class UserResponse(
    val data:UserInfo
)

data class UserInfo(
    var is_friend:Boolean,
    val icon_img:String? = null,
    val name:String,
    val created_utc:Long? = null,
    val snoovatar_img:String? = null,
    val subreddit:SubredditId,
    var has_subscribed:Boolean = false,
)

data class SubredditId(
    val name:String,
)