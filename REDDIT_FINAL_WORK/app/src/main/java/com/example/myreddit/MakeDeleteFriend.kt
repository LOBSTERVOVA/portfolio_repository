package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface MakeFriend{

    @GET("/api/v1/me/friends/{username}")
    suspend fun showUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("username")username:String,
    ):JsonObject?

    @PUT("/api/v1/me/friends/{username}")
    suspend fun addUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("username")username:String,
        @Body requestBody:RequestBody = RequestBody(username)
    ):JsonObject?



    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

interface DeleteFriend{
    @DELETE("/api/v1/me/friends/{username}")
    suspend fun deleteUser(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("username")username:String,
        //@Body requestBody:RequestBody = RequestBody(username)
    ):JsonObject?
}

val makeFriendRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(MakeFriend.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(MakeFriend::class.java)

val deleteFriendRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(MakeFriend.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(DeleteFriend::class.java)

data class RequestBody(
    val name:String
)
