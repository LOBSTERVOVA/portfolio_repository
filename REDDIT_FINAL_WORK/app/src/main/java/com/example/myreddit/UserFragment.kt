package com.example.myreddit

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "user"

class UserFragment : androidx.fragment.app.Fragment() {
    // TODO: Rename and change types of parameters
    private var user: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireArguments().let {
            user = it.getString(ARG_PARAM1)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = ComposeView(requireContext())
        viewLifecycleOwner.lifecycleScope.launch {
            view.setContent {
                ShowUser(name = user!!, parentFragmentManager, requireContext(), lifecycleScope)
            }
        }
        return view
    }

}

@Composable
fun ShowUser(name: String, parentFragmentManager: FragmentManager, context: Context, thread:LifecycleCoroutineScope) {
    val profile: MutableState<UserResponse?> = remember { mutableStateOf(null) }
    val isFriend:MutableState<Boolean> = remember { mutableStateOf(false) }

    LazyColumn(
        Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
    ) {
        thread.launch {
            try {
                profile.value = userRetrofit.loadUser(user = name)
                isFriend.value = profile.value!!.data.is_friend
            } catch (e: Exception) {
                Toast.makeText(context, "FAIL:${e}", Toast.LENGTH_LONG).show()
            }
        }

        item {
            if (profile.value == null) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .height(600.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(60.dp),
                        strokeWidth = 5.dp,
                        color = MaterialTheme.colors.primary,
                        backgroundColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                    )
                }
            } else {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = { parentFragmentManager.popBackStack() },
                        modifier = Modifier
                            .background(Color.White)
                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
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
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(5.dp, top = 30.dp, end = 8.dp)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlideImage(
                            url = profile.value!!.data.snoovatar_img,
                            modifier = Modifier
                                .clip(CircleShape)
                                .padding(10.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = profile.value!!.data.name.uppercase(),
                            fontSize = 36.sp,
                            fontFamily = FontFamily.Cursive,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (profile.value!!.data.created_utc != null) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = timeConverter(profile.value!!.data.created_utc!!),
                                fontSize = 22.sp,
                                color = Color.LightGray
                            )
                        }
                    }

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        /*Button(
                            onClick = {
                                          thread.launch {
                                              try {
                                                  checkFriendRetrofit.checkUser(user = profile.value!!.data.name)
                                                  Log.d("SUCCESS", "SUCCESS")
                                                  subUserRetrofit.subUser(action = "unsub", sr = profile.value!!.data.name, name = profile.value!!.data.name)


                                              } catch (e: Exception) {
                                                  Log.d("error", e.toString())
                                                  try {
                                                      subUserRetrofit.subUser(action = "sub", sr = profile.value!!.data.subreddit.name, name = profile.value!!.data.name)
                                                  }catch (e:Exception){
                                                      Log.d("ERROR", "FATAL")
                                                  }
                                              }
                                          }
                            },
                            modifier = Modifier
                                .background(Color.White)
                                .clip(RoundedCornerShape(15.dp))
                                .padding(8.dp),
                            //colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent)
                        ) {
                            Text(text = "follow", color = Color.Black)
                        }*/


                        Button(
                            onClick = {
                                thread.launch {
                                    try {
                                        if(!isFriend.value) {
                                            makeFriendRetrofit.addUser(username = profile.value!!.data.name)
                                            isFriend.value = true
                                            Log.d("isFriend", isFriend.value.toString())
                                        } else {
                                            isFriend.value = false
                                            Log.d("isFriend", isFriend.value.toString())
                                            val a = deleteFriendRetrofit.deleteUser(username = profile.value!!.data.name)
                                            Log.d("val a", a.toString())

                                        }
                                    }catch (e:Exception){
                                        //Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show()
                                        Log.d("ERROR", e.toString())
                                    }
                                }
                                      },
                            modifier = Modifier
                                .clip(RoundedCornerShape(15.dp))
                                .padding(8.dp),
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.person ),
                                contentDescription = null,
                                modifier = Modifier.size(50.dp),
                            )
                                Image(
                                    painter = painterResource(id = if(isFriend.value){R.drawable.ok}else{R.drawable.add_circle}),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(45.dp)
                                        .offset((-10).dp),
                                    )
                        }
                    }
                }
            }
        }
    }


}