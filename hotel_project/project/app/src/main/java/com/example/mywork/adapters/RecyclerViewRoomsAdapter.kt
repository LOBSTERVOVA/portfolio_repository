package com.example.mywork.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mywork.R
import com.example.mywork.databinding.RoomLayoutBinding
import com.example.mywork.framework.HotelModel
import com.example.mywork.framework.Rooms
import com.example.mywork.ui.BookingFragment
import com.example.mywork.ui.HOTEL_ARG
import com.example.mywork.ui.ROOM_ARG
import com.google.gson.Gson

class RecyclerViewRoomsAdapter(val rooms: Rooms, val activity:FragmentActivity, val fragmentManager: FragmentManager, val hotelModel: HotelModel): RecyclerView.Adapter<RoomViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = RoomLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return RoomViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return rooms.rooms.size
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        val currentRoom = rooms.rooms[position]
        with(holder.binding){
            val adapter = SliderAdapter(activity, currentRoom.image_urls)
            viewPager2.adapter = adapter
            roomName.text = currentRoom.name
            pec1.text = currentRoom.peculiarities[0]
            pec2.text = currentRoom.peculiarities[1]
            price1.text = currentRoom.price.toString()
            priceFor.text = currentRoom.price_per
            buttonBook.setOnClickListener {

                fragmentManager.beginTransaction().apply {
                    val bundle = Bundle()
                    bundle.putString(HOTEL_ARG, Gson().toJson(hotelModel))
                    bundle.putString(ROOM_ARG, Gson().toJson(currentRoom))
                    replace(R.id.fragmentContainerView, BookingFragment::class.java, bundle)
                    commit()
                }

            }
        }
    }

}

class RoomViewHolder(val binding: RoomLayoutBinding) : RecyclerView.ViewHolder(binding.root)