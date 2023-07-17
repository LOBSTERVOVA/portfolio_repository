package com.example.myreddit

import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface PostsApi {

    @GET("r/{title}/")
    suspend fun loadPosts(
        @Header("Authorization") token: String = TokenKeeper.token!!,
        @Path("title")title:String,
        @Query("after") after: String?,
        @Query("limit") limit: Int = 5,
        ): PostsResult

    companion object {
        const val BASE_URL = "https://oauth.reddit.com/"
    }
}

val postsRetrofit = Retrofit
    .Builder()
    .client(
        OkHttpClient.Builder().addInterceptor(HttpLoggingInterceptor().also {
            it.level = HttpLoggingInterceptor.Level.BODY
        }).build()
    )
    .baseUrl(PostsApi.BASE_URL)
    .addConverterFactory(GsonConverterFactory.create())
    .build()
    .create(PostsApi::class.java)

data class PostsResult(
    val data:PostsList
)

data class PostsList(
    val after:String,
    var children:List<Posts> = emptyList()
)

data class Posts(
    val data:Post
)

data class Post(
    val selftext:String? = null,
    val subreddit:String? = null,
    val author_fullname:String? = null,
    val is_gallery:Boolean,
    val title:String,
    val id:String,
    val name:String,
    val thumbnail:String? = null,
    val created_utc:Long? = null,
    val ups:Int? = null,
    val url:String? = null,
    val num_comments:Int? = 0,
    val post_hint:String? = null,
    val url_overridden_by_dest:String? = null,
    val media_matadata:MediaMetadata? = null,
    val gallery_data:GalleryData? = null,
    val is_video:Boolean? = null,
    val fallback_url:String? = null,
    val secure_media:SecureMedia? = null,
    val permalink:String? = null,
    var likes:Boolean? = null,
    var myOwnLinks:List<String> = emptyList()
)

data class GalleryData(
    val items:List<Photos>? = null
)
data class Photos(
    val media_id:String,
)

data class MediaMetadata(
    val media_matadata:Map<String, MediaData>? = null
)

data class MediaData(
    val status:String?,
)

data class SecureMedia(
    val reddit_video:RedditVideo? = null
)

data class RedditVideo(
    val fallback_url:String? = null
)