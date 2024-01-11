package com.example.mywork.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mywork.R
import com.example.mywork.databinding.ActivityMainBinding

const val STAR = "★"
const val HOTEL_ARG = "hotel_argument"
const val ROOM_ARG = "room_arg"

class HotelActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fragmentContainerView, HotelFragment())
            commit()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        supportFragmentManager.popBackStack()
    }
}
