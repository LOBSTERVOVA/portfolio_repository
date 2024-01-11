package com.example.mywork.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.mywork.R
import com.example.mywork.adapters.SliderAdapter
import com.example.mywork.databinding.FragmentHotelBinding
import com.example.mywork.ui.viewModels.HotelViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HotelFragment : Fragment() {

    lateinit var adapter: SliderAdapter
    var binding: FragmentHotelBinding? = null

    private val viewModel by viewModels<HotelViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HotelViewModel(requireContext()) as? T ?: throw IllegalStateException()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHotelBinding.inflate(layoutInflater)

        viewLifecycleOwner.lifecycleScope.launch {
            if (viewModel.hotelModel == null) {
                viewModel.hotelInit()
            }
            val hotelModel = viewModel.hotelModel!!
            binding!!.placeholder.visibility = View.GONE
            Log.d("hotel downloaded", "TRUE")
            adapter = SliderAdapter(requireActivity(), hotelModel.image_urls)

            binding!!.viewPager.adapter = adapter
            binding!!.rating.text = "$STAR ${hotelModel.rating} ${hotelModel.rating_name} "
            binding!!.name.text = hotelModel.name
            binding!!.address.text = hotelModel.adress
            binding!!.price.text = hotelModel.minimal_price.toString() + "₽"
            binding!!.priceForIt.text = hotelModel.price_for_it

            binding!!.textView9.text = hotelModel.about_the_hotel.peculiarities[0]
            binding!!.textView10.text = hotelModel.about_the_hotel.peculiarities[1]
            binding!!.textView11.text = hotelModel.about_the_hotel.peculiarities[2]
            binding!!.textView12.text = hotelModel.about_the_hotel.peculiarities[3]
            binding!!.description.text = hotelModel.about_the_hotel.description
            binding!!.placeholder.visibility = View.GONE
            binding!!.button.setOnClickListener {

                parentFragmentManager.beginTransaction().apply {
                    val bundle = Bundle()
                    bundle.putString(HOTEL_ARG, Gson().toJson(hotelModel))
                    replace(R.id.fragmentContainerView, RoomFragment::class.java, bundle)
                    commit()
                }
            }
        }

        return binding!!.root
    }

}