package com.example.myreddit

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch


class MyProfileFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        view.setContent {
            ShowMyProfile(context = requireContext(), thread = viewLifecycleOwner.lifecycleScope)
        }
        return view
    }
}

@Composable
fun ShowMyProfile(context: Context, thread: LifecycleCoroutineScope) {
    val me: MutableState<MyProfile?> = remember { mutableStateOf(null) }

    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)) {


        LazyColumn(Modifier.fillMaxWidth().background(Color.LightGray)) {
            thread.launch{
                me.value = myProfileRetrofit.loadMe()
            }
            item {
                if(me.value == null){
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp)
                            .background(Color.White),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(60.dp).padding(20.dp),
                            strokeWidth = 5.dp,
                            color = MaterialTheme.colors.primary,
                            backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                        )
                    }
                }else{
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White)
                            .clip(RoundedCornerShape(10.dp)),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(Modifier.fillMaxWidth().padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                            ) {
                            GlideImage(
                                url = me.value!!.icon_img,
                                modifier = Modifier.size(160.dp).clip(RoundedCornerShape(15.dp))
                            )
                        }
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = me.value!!.name,
                                fontSize = 40.sp,
                                fontFamily = FontFamily.Cursive,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Start) {
                            Text(text = "Friends: ${me.value!!.num_friends}")
                        }
                        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.Start) {
                            Text(text = "Created: ${timeConverter(me.value!!.created_utc)}")
                        }
                    }
                }
            }
        }
    }
}