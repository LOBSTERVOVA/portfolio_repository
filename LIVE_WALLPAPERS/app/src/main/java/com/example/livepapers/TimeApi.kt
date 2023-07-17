package com.example.livepapers

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface TimeApi{

    @GET("json")
    suspend fun loadTime(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double
    ): TimeResult

    companion object {
        const val BASE_URL = "https://api.sunrise-sunset.org/"
    }
}

val timeRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(TimeApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(TimeApi::class.java)

data class TimeResult(
    val results: Results
)

data class Results(
    val sunrise:String,
    val sunset:String,
    val solar_noon:String,
    val civil_twilight_begin:String,
    val civil_twilight_end:String
)