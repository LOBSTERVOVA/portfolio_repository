package com.example.myreddit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import kotlinx.coroutines.launch

class FavouritesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        view.setContent {
            ShowFavourites(viewLifecycleOwner.lifecycleScope)
        }
        return view
    }

    @Composable
    fun ShowFavourites(thread: LifecycleCoroutineScope) {
        val clickedButton = remember { mutableStateOf(1) }
        val me: MutableState<MyProfile?> = remember { mutableStateOf(null) }
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
                .padding(top = 5.dp)
        ) {
            LazyColumn(Modifier.height(1.dp)) {
                thread.launch {
                    me.value = myProfileRetrofit.loadMe()
                }
            }
            Row(Modifier.fillMaxWidth()) {

                Column(
                    Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Liked posts",
                        Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .background(if (clickedButton.value == 1) Color.DarkGray else Color.LightGray)
                            .padding(4.dp)
                            .clickable {
                                clickedButton.value = 1
                            },
                        color = if (clickedButton.value == 1) Color.White else Color.Black,
                    )
                }

                Column(
                    Modifier
                        .weight(1f)
                        .padding(5.dp)
                ) {
                    Text(
                        text = "Subscribed subreddits",
                        Modifier
                            .clip(RoundedCornerShape(13.dp))
                            .background(if (clickedButton.value == 2) Color.DarkGray else Color.LightGray)
                            .padding(4.dp)
                            .clickable {
                                clickedButton.value = 2
                            },
                        color = if (clickedButton.value == 2) Color.White else Color.Black,
                    )
                }
            }

            if (clickedButton.value == 1 && me.value != null) {
                ShowLikedPhotos(me.value!!.name)
            } else if (clickedButton.value == 2) {
                ShowSubbedSubreddits()
            }
        }
    }

    @Composable
    fun ShowLikedPhotos(name: String) {
        val pagingItems by lazy { LikedPagingItems.pager(user = name) }
        val pagedItems = pagingItems.flow.collectAsLazyPagingItems()

        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            items(pagedItems) { post ->
                val isLiked: MutableState<Boolean?> = remember { mutableStateOf(post!!.data.likes) }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(Color.White)

                ) {
                    Row(Modifier.fillMaxWidth()) {
                        Text(
                            text = post!!.data.title.uppercase(),
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Row(Modifier.fillMaxWidth()) {

                        TextField(
                            value = post!!.data.selftext.toString(),
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp),
                            enabled = true,
                            interactionSource = remember { MutableInteractionSource() },
                            readOnly = true
                        )
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {

                        val size: Int? = post!!.data.gallery_data?.items?.size

                        if (size != null && size != 0) {
                            for (i in 0 until size) {
                                GlideImage(
                                    url = "https://i.redd.it/${post.data.gallery_data!!.items!![i].media_id}.jpg",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(2.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
                        } else if (post.data.url_overridden_by_dest != null && post.data.is_video != true) {
                            GlideImage(
                                url = post.data.url_overridden_by_dest,
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .padding(5.dp),
                                contentScale = ContentScale.Fit
                            )
                        } else if (post.data.secure_media?.reddit_video?.fallback_url != null && post.data.is_video == true) {
                            val exoPlayer = remember(context) {
                                SimpleExoPlayer.Builder(requireContext()).build().apply {
                                    val dataSourceFactory: DataSource.Factory =
                                        DefaultDataSourceFactory(
                                            requireContext(),
                                            Util.getUserAgent(
                                                requireContext(),
                                                requireContext().packageName
                                            )
                                        )

                                    val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                                        .createMediaSource(MediaItem.fromUri(Uri.parse(post.data.secure_media.reddit_video.fallback_url)))

                                    this.prepare(source)
                                }
                            }

                            AndroidView(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .height(400.dp),
                                factory = { context ->
                                    PlayerView(context).apply {
                                        player = exoPlayer
                                    }
                                })
                        } else if (post.data.thumbnail != null) GlideImage(
                            url = post.data.thumbnail,
                            modifier = Modifier
                                .fillMaxWidth(1f)
                                .padding(5.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    /*Row(Modifier.fillMaxWidth()) {
                        Text(text = "size${post!!.data.gallery_data?.items?.size.toString()} elements:${post.data.gallery_data} url${post.data.url_overridden_by_dest} ")
                    }*/

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            Modifier
                                .weight(1f)
                                .clickable {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        try {
                                            isLiked.value =
                                                if (isLiked.value == true) null else true
                                            val i = if (isLiked.value != true) {
                                                0
                                            } else {
                                                1
                                            }
                                            likeRetrofit.loadLike(
                                                id = post!!.data.name,
                                                dir = i
                                            )

                                            post.data.likes = if (i == 1) true else null
                                            Log.d("likes", post.data.likes.toString())
                                        } catch (e: Exception) {
                                            Toast
                                                .makeText(
                                                    requireContext(),
                                                    "FAILED:${e}",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                            isLiked.value =
                                                if (isLiked.value == true) null else true
                                        }
                                    }
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = if (isLiked.value == true) R.drawable.favorite_red else R.drawable.favorite),
                                contentDescription = null,
                                Modifier
                                    .size(45.dp)

                            )
                            Text(
                                text = post!!.data.ups.toString(),
                                fontFamily = FontFamily.Cursive,
                                fontSize = 35.sp,
                                maxLines = 1
                            )
                        }
                        Row(
                            Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.comment),
                                contentDescription = null,
                                Modifier
                                    .size(45.dp)
                            )
                            Text(
                                text = (if (post!!.data.num_comments == null || post.data.num_comments == 0) (0).toString() else (post.data.num_comments!! - 1).toString()),
                                fontFamily = FontFamily.Cursive,
                                fontSize = 35.sp,
                                maxLines = 1
                            )
                        }
                        Row(
                            Modifier
                                .weight(1f),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.share),
                                contentDescription = null,
                                Modifier
                                    .weight(1f)
                                    .size(45.dp)
                                    .clickable {

                                        val sendIntent = Intent()
                                        sendIntent.action = Intent.ACTION_SEND
                                        sendIntent.putExtra(
                                            Intent.EXTRA_TEXT,
                                            "https://www.reddit.com/r/${post!!.data.subreddit}/comments/${post.data.id}.json"
                                        )
                                        sendIntent.type = "text/plain"

                                        val shareIntent = Intent.createChooser(sendIntent, null)
                                        startActivity(shareIntent)

                                    }
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ShowSubbedSubreddits() {
        val pagingItems by lazy { SubredditsPagingSource.pager(2) }
        val pagedItems = pagingItems.flow.collectAsLazyPagingItems()
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            items(pagedItems) { subreddit ->
                var isSubbed by remember { mutableStateOf(value = subreddit!!.data.user_is_subscriber) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(6.dp)
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
                        .shadow(2.dp)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth(0.75f)
                                .clickable {
                                    val bundle = Bundle()
                                    bundle.putString("subreddit", Gson().toJson(subreddit))
                                    parentFragmentManager.commit {
                                        addToBackStack(null)
                                        replace<SubredditPostsFragment>(
                                            R.id.fragmentContainerView,
                                            args = bundle
                                        )
                                    }
                                }) {
                            Row(Modifier.fillMaxWidth()) {
                                if (subreddit!!.data.icon_img != null) {
                                    GlideImage(
                                        url = subreddit.data.icon_img,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(
                                                CircleShape
                                            )
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.reddit_icon),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(
                                                CircleShape
                                            )
                                    )
                                }
                                Text(
                                    text = subreddit.data.display_name,
                                    fontSize = 24.sp,
                                    modifier = Modifier
                                        .padding(8.dp),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Text(
                                text = timeConverter(subreddit!!.data.created_utc),
                                fontSize = 18.sp,
                            )
                        }
                        Column(Modifier.fillMaxWidth()) {
                            Image(
                                painter = painterResource(id = if (!isSubbed) R.drawable.person_add else R.drawable.ok),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(52.dp)
                                    .requiredSize(52.dp)
                                    .padding(start = 2.dp)
                                    .clickable {
                                        try {
                                            viewLifecycleOwner.lifecycleScope.launch {
                                                subUnsubRetrofit.subUnsub(
                                                    action = if (isSubbed) "unsub" else "sub",
                                                    st_name = subreddit!!.data.display_name
                                                )
                                                isSubbed = !isSubbed
                                                subreddit.data.user_is_subscriber = isSubbed
                                            }
                                        } catch (e: Exception) {
                                            Toast
                                                .makeText(
                                                    requireContext(),
                                                    "Check internet connection",
                                                    Toast.LENGTH_LONG
                                                )
                                                .show()
                                        }
                                    },
                                alignment = Alignment.CenterEnd
                            )
                            Image(
                                painter = painterResource(id = R.drawable.share),
                                contentDescription = null,
                                alignment = Alignment.CenterEnd,
                                modifier = Modifier
                                    .size(51.dp)
                                    .requiredSize(51.dp)
                                    .clickable {
                                        val sendIntent = Intent()
                                        sendIntent.action = Intent.ACTION_SEND
                                        sendIntent.putExtra(
                                            Intent.EXTRA_TEXT,
                                            "https://www.reddit.com/r/${subreddit!!.data.display_name}"
                                        )
                                        sendIntent.type = "text/plain"

                                        val shareIntent = Intent.createChooser(
                                            sendIntent,
                                            getString(R.string.Send_with)
                                        )
                                        startActivity(shareIntent)
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}