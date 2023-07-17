package com.example.myreddit

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.ViewModel


class SubredditsFragmentViewModel(context: Context):ViewModel() {
    init {
        Log.d("ViewModel", "init")
    }
    val pagingItems by lazy { SubredditsPagingSource.pager(1) }

}