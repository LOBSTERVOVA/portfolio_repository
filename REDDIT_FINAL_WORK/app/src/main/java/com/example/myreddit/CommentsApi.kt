package com.example.myreddit

import android.content.ClipData.Item
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface CommentsApi{

    @GET("/comments/{id}")
    suspend fun loadComments(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("id")id:String
    ): List<CommentResponse>

    companion object {
        const val BASE_URL = "https://oauth.reddit.com"
    }
}

val commentsRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(CommentsApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(CommentsApi::class.java)

data class CommentResponse(
    val data:CommentItem
)

data class CommentItem(
    val children:List<Children>
)

data class Children(
    val data:CommentData? = null
)

data class CommentData(
    val author:String? = null,
    val body:String? = null
)