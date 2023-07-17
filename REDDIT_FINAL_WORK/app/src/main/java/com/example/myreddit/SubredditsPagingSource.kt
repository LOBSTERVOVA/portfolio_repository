package com.example.myreddit

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.delay

class SubredditsPagingSource(val id:Int) : PagingSource<String, Subreddit>(){

    var hasBeenNull = false
    override val keyReuseSupported: Boolean
        get() = true

    override fun getRefreshKey(state: PagingState<String, Subreddit>): String? = "null"

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Subreddit> {
        val page = params.key ?:"null"

        try {

            val response: Result = if(id==1)subredditsRetrofit.loadSubreddits(after = page)else subbedSubsRetrofit.loadSubbedSubs(after = page)

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
        fun pager(id:Int) = Pager(config = PagingConfig(10), pagingSourceFactory = {SubredditsPagingSource(id)
        })
    }
}