package com.example.myreddit

import androidx.lifecycle.ViewModel

class SubredditPostsFragmentViewModel(val title:String):ViewModel() {

    val pagingItems by lazy { PostsPagingSource.pager(title = title) }
}