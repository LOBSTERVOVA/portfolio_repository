package com.example.myreddit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.example.myreddit.databinding.FragmentBoardingBinding
import androidx.fragment.app.replace
import androidx.viewpager2.widget.ViewPager2


class BoardingFragment : Fragment() {

    private var binding:FragmentBoardingBinding? = null
    private lateinit var adapter:OnboardingAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBoardingBinding.inflate(layoutInflater)

        adapter = OnboardingAdapter(requireActivity())
        binding!!.viewPager2.adapter = adapter

        binding!!.button.setOnClickListener {
            parentFragmentManager.commit {
                replace<AuthFragment>(R.id.authFragment)
            }
        }

        return binding!!.root
    }
}