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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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


private const val ARG_PARAM1 = "subreddit"


class SubredditPostsFragment : Fragment() {
    private var subreddit: Subreddit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            subreddit = Gson().fromJson(it.getString(ARG_PARAM1), Subreddit::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = ComposeView(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            view.setContent {
                ShowSubreddit()
            }
        }

        return view
    }

    @Composable
    fun ShowSubreddit() {
        var isSubbed by remember { mutableStateOf(value = subreddit!!.data.user_is_subscriber) }
        var text by remember { mutableStateOf(value = if (isSubbed) "Unjoin" else "Join") }
        val pagingItems by lazy { PostsPagingSource.pager(title = subreddit!!.data.display_name) }
        val pagedItems = pagingItems.flow.collectAsLazyPagingItems()


        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp, bottom = 25.dp)
                        .background(Color.White)
                ) {
                    Row(
                        Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(90.dp)
                                .padding(5.dp)
                        ) {
                            GlideImage(
                                url = subreddit!!.data.icon_img,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .size(90.dp)
                            )
                        }
                        Column(Modifier.fillMaxWidth()) {
                            Text(
                                text = subreddit!!.data.display_name,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(5.dp),
                                fontFamily = FontFamily.Cursive
                            )
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${subreddit!!.data.subscribers} members",
                                    fontSize = 18.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.padding(5.dp)
                                )
                                Button(
                                    onClick = {
                                        viewLifecycleOwner.lifecycleScope.launch {
                                            try {
                                                subUnsubRetrofit.subUnsub(
                                                    action = if (isSubbed) "unsub" else "sub",
                                                    st_name = subreddit!!.data.display_name
                                                )
                                                isSubbed = !isSubbed
                                                text = if (isSubbed) "Unjoin" else "Join"
                                            } catch (e: Exception) {
                                                Toast.makeText(
                                                    requireContext(),
                                                    "Check internet connection",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                    }, modifier = Modifier
                                        .padding(end = 5.dp)
                                        .requiredWidth(80.dp), shape = CircleShape
                                ) {
                                    Text(
                                        text = text,
                                        color = if (isSubbed) Color.Red else Color.White,
                                        maxLines = 1,
                                        modifier = Modifier.weight(1f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                    }
                }
            }

            items(pagedItems) { post ->
                val isLiked: MutableState<Boolean?> = remember { mutableStateOf(post!!.data.likes) }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .background(Color.White)
                        .clickable {
                            val bundle = Bundle()
                            bundle.putString("subreddit", Gson().toJson(subreddit))
                            bundle.putString("post", Gson().toJson(post))
                            parentFragmentManager.commit {
                                addToBackStack(null)
                                replace<SinglePostFragment>(
                                    R.id.fragmentContainerView,
                                    args = bundle
                                )
                            }
                        }
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
                                            isLiked.value = if(isLiked.value == true)null else true
                                            val i = if(isLiked.value != true){0}else{1}
                                            likeRetrofit.loadLike(id = post!!.data.name,
                                                dir = i
                                            )

                                            post.data.likes = if(i == 1)true else null
                                            Log.d("likes", post.data.likes.toString())
                                        }catch (e:Exception){
                                            Toast.makeText(requireContext(), "FAILED:${e}", Toast.LENGTH_LONG).show()
                                            isLiked.value = if(isLiked.value == true)null else true
                                        }
                                    }
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = if(isLiked.value == true)R.drawable.favorite_red else R.drawable.favorite),
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
                                .weight(1f)
                                .clickable {
                                    parentFragmentManager.commit {
                                        val bundle = Bundle()
                                        bundle.putString("subreddit", Gson().toJson(subreddit))
                                        bundle.putString("post", Gson().toJson(post))
                                        replace<SinglePostFragment>(
                                            R.id.fragmentContainerView,
                                            args = bundle
                                        )
                                    }
                                },
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.comment),
                                contentDescription = null,
                                Modifier
                                    .size(45.dp)
                            )
                            Text(
                                text = (if(post!!.data.num_comments == null||post.data.num_comments==0) (0).toString() else (post.data.num_comments!!-1).toString()),
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
                                            "https://www.reddit.com/r/${subreddit!!.data.display_name}/comments/${post!!.data.id}.json"
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
}