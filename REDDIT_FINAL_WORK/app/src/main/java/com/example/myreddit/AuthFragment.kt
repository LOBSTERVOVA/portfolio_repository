package com.example.myreddit

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myreddit.databinding.FragmentAuthBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "uri"

class AuthFragment : Fragment() {

    private var binding: FragmentAuthBinding? = null
    private var clicked = mutableStateOf(false)
    private var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uri = it.getString(ARG_PARAM1)?.toUri()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthBinding.inflate(layoutInflater)

        if (uri != null) clicked.value = true
        val authorizationCode = uri?.getQueryParameter("code")
        val state = uri?.getQueryParameter("state")
        Log.d("params", uri.toString())
        Log.d("code", authorizationCode.toString())
        Log.d("state", state.toString())
        if (authorizationCode != null) {
            lifecycleScope.launch {
                val a = getToken(authorizationCode)
                Log.d("TOKEN", a.toString())
                if (a == null) clicked.value = false
                else {
                    val intent = Intent(requireContext(), MainActivity::class.java)
                    intent.putExtra("token", a)
                    clicked.value = false
                    startActivity(intent)
                    delay(1000)
                    activity?.finish()
                }
                delay(2000)
                clicked.value = false
            }
        } else clicked.value = false

        binding!!.enterButton.setOnClickListener {
            openBrowser()
            clicked.value = true
        }

        lifecycleScope.launch {
            binding!!.enterButton.isClickable = !clicked.value
        }
        return binding!!.root
    }

    private fun openBrowser() {
        val intent = Intent(Intent.ACTION_VIEW, composeUrl())
        this.startActivity(intent)
    }

    private fun composeUrl(): Uri =
        Uri.parse("https://www.reddit.com/api/v1/authorize")
            .buildUpon()
            .appendQueryParameter(
                "client_id",
                "2CqfPB-vHiKS-9jG4KrHfg"
            )
            .appendQueryParameter(
                "redirect_uri",
                "com.example.myreddit://auth"
            )
            .appendQueryParameter("response_type", "code")
            .appendQueryParameter(
                "scope",
                "identity edit flair history modconfig modflair modlog modposts modwiki mysubreddits privatemessages read report save submit subscribe vote wikiedit wikiread"
            )
            .appendQueryParameter("state", "mystring1234")
            .appendQueryParameter("duration", "permanent")
            .build()

}