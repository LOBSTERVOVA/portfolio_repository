package com.example.myreddit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GetSubbedSubredditsApi {

    @GET("subreddits/mine")
    suspend fun loadSubbedSubs(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Query("after") after: String?,
        @Query("limit") limit: Int = 10,
    ): Result

    companion object {
        const val BASE_URL = "https://oauth.reddit.com/"
    }
}

val subbedSubsRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(GetSubbedSubredditsApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(GetSubbedSubredditsApi::class.java)