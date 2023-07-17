package com.example.myreddit

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

class LikedPagingItems(val username:String): PagingSource<String, Posts>(){

    var hasBeenNull = false
    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<String, Posts>): String? = "null"

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Posts> {
        val page = params.key ?:"null"

        try {

            val response: PostsResult = likedPostsRetrofit.loadLikedPhotos(username = username, after = page)

            if(hasBeenNull && page == "null")return LoadResult.Page(emptyList(), null, nextKey = response.data.after)
            if(page == "null")hasBeenNull = true
            Log.d("SUCCESS", "after ${response.data.after}")
            return LoadResult.Page(
                response.data.children, "null",
                nextKey = response.data.after)
        }catch (e: Exception) {
            delay(2000)
            Log.d("ERROR", "occured")
            return LoadResult.Page(emptyList(), null, page)
        }
    }

    companion object{
        fun pager(user:String) = Pager(config = PagingConfig(5), pagingSourceFactory = {LikedPagingItems(user)
        })
    }
}