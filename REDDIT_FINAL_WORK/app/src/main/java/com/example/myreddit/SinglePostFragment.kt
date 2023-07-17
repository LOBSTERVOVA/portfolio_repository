package com.example.myreddit

import android.content.Context
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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.google.gson.Gson
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "post"
private const val ARG_PARAM2 = "subreddit"

class SinglePostFragment : Fragment() {
    private var post: Posts? = null
    private var subreddit: Subreddit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            post = Gson().fromJson(it.getString(ARG_PARAM1), Posts::class.java)
            subreddit = Gson().fromJson(it.getString(ARG_PARAM2), Subreddit::class.java)
            Log.d("subreddit", subreddit!!.data.display_name)
            Log.d("post", post!!.data.title)
            Log.d("permalink", post!!.data.permalink!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            val comments = commentsRetrofit.loadComments(
                id = post!!.data.id
            )
            view.setContent {
                ShowSinglePost(
                    post = post!!,
                    subreddit = subreddit!!,
                    parentFragmentManager = parentFragmentManager,
                    context = requireContext(),
                    comments = comments,
                    thread = viewLifecycleOwner.lifecycleScope
                )
            }
        }

        return view
    }
}

@Composable
fun ShowSinglePost(
    post: Posts,
    subreddit: Subreddit,
    parentFragmentManager: FragmentManager,
    context: Context,
    comments: List<CommentResponse>,
    thread: LifecycleCoroutineScope
) {
    val newCommentCreated = remember { mutableStateOf(false) }
    val isLiked: MutableState<Boolean?> = remember { mutableStateOf(post.data.likes) }
    val mComments: MutableState<List<CommentResponse>> = remember { mutableStateOf(comments) }
    val commentText: MutableState<String> = remember { mutableStateOf("") }
    LazyColumn(
        Modifier
            .fillMaxWidth()
            .background(Color.White)
    ) {
        item {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Row(Modifier.padding(5.dp)) {
                    Button(
                        onClick = { parentFragmentManager.popBackStack() },
                        Modifier.background(Color.White),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                        )
                    }
                }
                Row(Modifier.fillMaxWidth()) {
                    if (subreddit.data.icon_img != null) {
                        GlideImage(
                            url = subreddit.data.icon_img,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.reddit_icon),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp),
                            contentScale = ContentScale.Crop
                        )
                    }


                    SelectionContainer(
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Column(Modifier.fillMaxWidth()) {
                            Text(
                                text = subreddit.data.display_name,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Cursive
                            )
                            if (post.data.created_utc != null)
                                Text(text = timeConverter(post.data.created_utc))
                        }
                    }
                }
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp, top = 15.dp)
                ) {
                    //SelectionContainer(Modifier.fillMaxWidth()) {
                    Text(text = post.data.title, fontWeight = FontWeight.Bold, fontSize = 30.sp)
                    if (post.data.selftext != null) Text(
                        text = post.data.selftext,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    //}


                    val size: Int? = post.data.gallery_data?.items?.size

                    if (size != null && size != 0) {
                        Row(Modifier.fillMaxWidth()) {
                            for (i in 0 until size) {
                                GlideImage(
                                    url = "https://i.redd.it/${post.data.gallery_data.items[i].media_id}.jpg",
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(2.dp),
                                    contentScale = ContentScale.Fit
                                )
                            }
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
                            SimpleExoPlayer.Builder(context).build().apply {
                                val dataSourceFactory: DataSource.Factory =
                                    DefaultDataSourceFactory(
                                        context,
                                        Util.getUserAgent(
                                            context,
                                            context.packageName
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


                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(5.dp)
                    ) {
                        Row(
                            Modifier
                                .weight(1f)
                                .clickable {
                                    thread.launch {
                                        try {
                                            isLiked.value =
                                                if (isLiked.value == true) null else true
                                            val i = if (isLiked.value == true) {
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
                                                .makeText(context, "FAILED:${e}", Toast.LENGTH_LONG)
                                                .show()
                                            isLiked.value =
                                                if (isLiked.value != true) null else true
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
                                text = post.data.ups.toString(),
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
                                text = ((post.data.num_comments ?: 1) - 1).toString(),
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
                                            "https://www.reddit.com/r/${subreddit.data.display_name}/comments/${post.data.id}.json"
                                        )
                                        sendIntent.type = "text/plain"

                                        val shareIntent = Intent.createChooser(sendIntent, null)
                                        startActivity(context, shareIntent, null)
                                    }
                            )
                        }
                    }

                }
                /*Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    TextField(
                        value = commentText.value,
                        onValueChange = {
                            commentText.value = it
                        },
                        Modifier.fillMaxWidth(0.85f)
                    )
                    Button(onClick = {
                        thread.launch {
                            try {
                                sendRetrofit.sendComment(
                                    text = commentText.value,
                                    thing_id = post.data.id
                                )
                                mComments.value = commentsRetrofit.loadComments(id = post.data.id)
                                newCommentCreated.value = true
                            } catch (e: Exception) {
                                Toast.makeText(context, "ERROR:${e}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }) {
                        Image(
                            painter = painterResource(id = R.drawable.send),
                            contentDescription = null,
                            Modifier.size(40.dp)
                        )
                    }
                }*/
            }
        }
        if (!newCommentCreated.value) {
            mComments.value.forEach {
                items(it.data.children) { comment ->
                    if (comment.data?.body != null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(5.dp)
                                .clickable {
                                    thread.launch {
                                        //val a = userRetrofit.loadUser(user = comment.data.author!!)
                                        //Log.d("user", a.toString())
                                        val bundle = Bundle()
                                        bundle.putString("user", comment.data.author)
                                        parentFragmentManager.commit {
                                            addToBackStack(null)
                                            replace<UserFragment>(
                                                R.id.fragmentContainerView,
                                                args = bundle
                                            )
                                        }
                                    }
                                }) {
                            Column() {
                                Row(Modifier.fillMaxWidth()) {
                                    Image(
                                        painter = painterResource(id = R.drawable.reddit_icon),
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .size(40.dp)
                                            .padding(4.dp),
                                        contentScale = ContentScale.Fit,
                                        contentDescription = null
                                    )
                                    Text(
                                        text = comment.data?.author.toString(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 30.sp,
                                        fontFamily = FontFamily.Cursive
                                    )
                                }
                                Text(text = comment.data?.body.toString())
                            }
                        }
                    }
                }
            }
        } else {
            newCommentCreated.value = false
        }
    }
}