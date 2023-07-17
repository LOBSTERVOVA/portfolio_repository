package com.example.myreddit

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val REDDIT_IMG =
    "https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pngwing.com%2Fru%2Fsearch%3Fq%3Dreddit&psig=AOvVaw2--iVJQk5sedGEOEm8A6j6&ust=1685431700656000&source=images&cd=vfe&ved=0CBEQjRxqFwoTCIiU_b-Bmv8CFQAAAAAdAAAAABAE"

class SubredditsFragment : Fragment() {

    private val viewModel by viewModels<SubredditsFragmentViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SubredditsFragmentViewModel(requireContext()) as? T
                    ?: throw IllegalStateException()
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            view.setContent {
                ShowSubreddits()
            }
        }
        return view
    }

    @Composable
    private fun ShowSubreddits() {
        val items = viewModel.pagingItems.flow.collectAsLazyPagingItems()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.8f)
        ) {


            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 50.dp)
                    .background(Color.White)

            ) {
                items(items) { subreddit ->
                    var isSubbed by remember {
                        mutableStateOf(value = subreddit!!.data.user_is_subscriber)
                    }
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
                                    }else{
                                        Image(painter = painterResource(id = R.drawable.reddit_icon),
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
}


@Composable
fun GlideImage(
    url: Any?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop,
    errorDrawable: Int = R.drawable.reddit_icon,
    loadingDrawable: Int = R.drawable.hourglass_top,
) {
    val context = LocalContext.current
    val bitmap = remember { mutableStateOf<Bitmap?>(null) }
    Glide.with(context)
        .asBitmap()
        .load(url ?: REDDIT_IMG)
        .apply(RequestOptions().override(Target.SIZE_ORIGINAL))
        .listener(object : RequestListener<Bitmap> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Bitmap>?,
                isFirstResource: Boolean,
            ): Boolean {
                return false
            }

            override fun onResourceReady(
                resource: Bitmap?,
                model: Any?,
                target: Target<Bitmap>?,
                dataSource: DataSource?,
                isFirstResource: Boolean,
            ): Boolean {
                bitmap.value = resource
                return false
            }
        })
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(
                resource: Bitmap,
                transition: Transition<in Bitmap>?,
            ) {
                bitmap.value = resource
            }

            override fun onLoadCleared(placeholder: Drawable?) {}
        })


    bitmap.value?.let {
        Image(
            bitmap = it.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = contentScale,
        )
    } ?: Image(
        painterResource(id = loadingDrawable),
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
    )
}

fun timeConverter(millis: Long): String {
    val timeZone = TimeZone.getDefault()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    dateFormat.timeZone = timeZone
    val date = Date(millis)
    return dateFormat.format(date)
}
