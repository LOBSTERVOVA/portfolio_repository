package com.example.mywork

import com.example.mywork.framework.BookingInfo
import com.example.mywork.framework.HotelModel
import com.example.mywork.framework.Rooms
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HotelApi {
    @GET("/v3/d144777c-a67f-4e35-867a-cacc3b827473")
    suspend fun getHotel():HotelModel
    @GET("/v3/8b532701-709e-4194-a41c-1a903af00195")
    suspend fun getRoom():Rooms
    @GET("/v3/63866c74-d593-432c-af8e-f279d1a8d2ff")
    suspend fun getBooking(): BookingInfo
}
    val getHotel = Retrofit
        .Builder()
        .client(
            OkHttpClient
                .Builder()
                .addInterceptor(
                    HttpLoggingInterceptor()
                        .also {
                            it.level = HttpLoggingInterceptor.Level.BODY
                        }).build()
        )
        .baseUrl("https://run.mocky.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HotelApi::class.java)

