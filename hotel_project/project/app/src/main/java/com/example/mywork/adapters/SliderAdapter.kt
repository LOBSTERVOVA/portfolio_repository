package com.example.mywork.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mywork.ui.SLIDER_ARG
import com.example.mywork.ui.SliderFragment

class SliderAdapter(fragment: FragmentActivity, val uris: List<String>): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return uris.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = SliderFragment()
        fragment.arguments = Bundle().apply {
            putString(SLIDER_ARG, uris[position])
        }
        return fragment
    }
}