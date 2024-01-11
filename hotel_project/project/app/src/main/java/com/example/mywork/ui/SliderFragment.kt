package com.example.mywork.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.mywork.R
import com.example.mywork.databinding.FragmentSliderBinding

const val SLIDER_ARG = "sliderargument"

class SliderFragment : Fragment() {
    var binding:FragmentSliderBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSliderBinding.inflate(layoutInflater)
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments.let {
            val uri = it?.getString(SLIDER_ARG)!!.toUri()
            Glide.with(requireContext())
                .load(uri)
                .placeholder(R.drawable.image)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(40)))
                .error(R.drawable.wifi_off)
                .into(binding!!.sliderPhoto)
        }
    }
}