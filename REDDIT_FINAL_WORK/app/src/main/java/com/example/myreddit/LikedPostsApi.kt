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

interface LikedPostsApi {

    @GET("/user/{username}/liked")
    suspend fun loadLikedPhotos(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("username")username:String,
        @Query("limit")limit:Int = 5,
        @Query("after")after:String?,
    ): PostsResult

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val likedPostsRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(LikedPostsApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(LikedPostsApi::class.java)