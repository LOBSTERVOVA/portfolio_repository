package com.example.myreddit

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myreddit.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private var binding: ActivityAuthBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uri = intent?.data

        binding = ActivityAuthBinding.inflate(layoutInflater)
        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)

        if (sharedPref.getBoolean("launched", false)) {
            val bundle = Bundle()
            bundle.putString("uri", uri.toString())
            supportFragmentManager.commit {
                replace<AuthFragment>(R.id.authFragment, args = bundle)
            }
        } else {
            val editor = sharedPref.edit()
            editor.putBoolean("launched", true)
            editor.apply()

            supportFragmentManager.commit {
                replace<BoardingFragment>(R.id.authFragment)
            }
        }
        setContentView(binding!!.root)
    }
}