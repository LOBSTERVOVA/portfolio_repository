package com.example.mywork.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mywork.adapters.RecyclerViewRoomsAdapter
import com.example.mywork.databinding.FragmentRoomsBinding
import com.example.mywork.framework.DaggerMyRoomComponent
import com.example.mywork.framework.HotelModel
import com.example.mywork.framework.MyDaggerRoomModule
import com.example.mywork.ui.viewModels.RoomsViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import javax.inject.Inject


class RoomFragment : Fragment() {

    @Inject
    lateinit var adapter: RecyclerViewRoomsAdapter
    lateinit var hotelModel: HotelModel
    private val viewModel by viewModels<RoomsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RoomsViewModel(requireContext()) as? T ?: throw IllegalStateException()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentRoomsBinding.inflate(layoutInflater)

        binding.imageView5.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        hotelModel = Gson().fromJson(arguments?.getString(HOTEL_ARG), HotelModel::class.java)
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.hotelName.text = hotelModel.name
        lifecycleScope.launch {
            if (viewModel.rooms == null) {
                viewModel.roomsInit()
            }
            binding.placeholder.visibility = View.GONE

            if (viewModel.adapter == null) {
                DaggerMyRoomComponent.builder()
                    .myDaggerRoomModule(
                        MyDaggerRoomModule(
                            viewModel.rooms!!,
                            requireActivity(),
                            parentFragmentManager,
                            hotelModel
                        )
                    )
                    .build()
                    .inject(this@RoomFragment)
                viewModel.adapter = adapter
            }
            binding.recyclerView.adapter = viewModel.adapter
        }

        return binding.root
    }

}