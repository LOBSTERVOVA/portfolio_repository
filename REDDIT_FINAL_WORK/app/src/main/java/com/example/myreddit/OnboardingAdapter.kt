package com.example.myreddit

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class OnboardingAdapter(fragment:FragmentActivity): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        val frag = OnboardingElementFragment()
        frag.arguments = Bundle().apply {
            putString("param1", when(position){
                0->"Welcome to My Reddit!!!"
                1->"Main screen. Subreddits"
                2->"Favourites"
                else -> {"My profile"}
            })
            putString("param2", when(position){
                0->"Check out app's functions..."
                1->"Here you can see a list of subreddits, if you click on it you will see posts of it"
                2->"Here you can check your liked photos and subreddits you subscribed"
                else -> {"A short info about your profile"}
            })
            putInt("param3", when(position){
                0->R.drawable.reddit_icon
                1->R.drawable.collections
                2->R.drawable.favorite
                else -> {R.drawable.person}
            })


        }
        return frag
    }
}