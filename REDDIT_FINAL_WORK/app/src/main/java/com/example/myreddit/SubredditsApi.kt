package com.example.myreddit

import android.os.Parcelable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SubredditsApi {

    @GET("subreddits/popular")
    suspend fun loadSubreddits(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("limit") limit: Int = 10,
        @Query("after") after: String
    ): Result

    companion object {
        const val BASE_URL = "https://oauth.reddit.com/"
    }
}

val subredditsRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(SubredditsApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(SubredditsApi::class.java)

data class Result(
    val data:SubredditList
)

data class SubredditList(
    val after:String? = null,
    var children:List<Subreddit> = emptyList(),
    val before:String? = null,
)

data class Subreddit(
    val data:Data,
)

data class Data(
    val id:String,
    val icon_img:String? = null,
    val display_name:String = "",
    val created_utc:Long,
    var user_is_subscriber:Boolean = false,
    val subscribers:Int = 0,
)