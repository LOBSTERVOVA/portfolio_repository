package com.example.myreddit


import android.annotation.SuppressLint
import android.util.Base64
import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


@SuppressLint("NewApi")
interface TokenApi {
    @POST("/api/v1/access_token")
    suspend fun getAccessToken(
        @Header("Authorization")header: String = "Basic $encoded",
        @Query("grant_type")grant_type: String = "authorization_code",
        @Query("code")code: String,
        @Query("redirect_uri")redirect_uri: String = "com.example.myreddit://auth",

        ): AuthInfo

    companion object{
        const val id = "2CqfPB-vHiKS-9jG4KrHfg"
        const val secret = ""
        private const val str = "${id}:${secret}"
        val encoded:String = Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP)
    }
}

val retrofit = Retrofit.Builder()
    .baseUrl("https://www.reddit.com")
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(TokenApi::class.java)

class AuthInfo(val access_token: String?)

suspend fun getToken(authCode: String):String? {

    runCatching {
        Log.d("encode", TokenApi.encoded)
        retrofit.getAccessToken(code =
        authCode)}.fold(
        onSuccess = {
            Log.d("TOKEN SUCCESS", it.toString())
            return it.access_token
        },
        onFailure = { Log.d("TOKEN FAILURE", it.message ?: "")
            return null
        }
    )
}