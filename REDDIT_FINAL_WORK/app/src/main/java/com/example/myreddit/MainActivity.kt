package com.example.myreddit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.example.myreddit.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var token:String
    private var binding:ActivityMainBinding? = null

    override fun onBackPressed() {

        supportFragmentManager.popBackStack()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        token = intent.getStringExtra("token")!!
        TokenKeeper.token = "Bearer $token"

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        binding!!.subredditsImageButton.setOnClickListener {
            binding!!.subredditsImageButton.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.favouritesImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.profileImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            supportFragmentManager.commit {
                replace<SubredditsFragment>(R.id.fragmentContainerView)
            }
        }
        binding!!.favouritesImageButton.setOnClickListener {
            binding!!.subredditsImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.favouritesImageButton.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.profileImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            supportFragmentManager.commit {
                replace<FavouritesFragment>(R.id.fragmentContainerView)
            }
        }
        binding!!.profileImageButton.setOnClickListener {
            binding!!.subredditsImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.favouritesImageButton.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
            binding!!.profileImageButton.setColorFilter(ContextCompat.getColor(this, R.color.red), android.graphics.PorterDuff.Mode.SRC_IN)
            supportFragmentManager.commit {
                replace<MyProfileFragment>(R.id.fragmentContainerView)
            }
        }

    }
}