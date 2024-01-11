package com.example.mywork.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.mywork.adapters.RecyclerViewRoomsAdapter
import com.example.mywork.framework.Rooms
import com.example.mywork.getHotel
import kotlinx.coroutines.delay

class RoomsViewModel(val context: Context) : ViewModel() {
    var rooms: Rooms? = null
    var adapter: RecyclerViewRoomsAdapter? = null
    suspend fun roomsInit() {
        do {
            try {
                rooms = getHotel.getRoom()
            } catch (e: Exception) {
                Toast.makeText(context, "check your internet connection", Toast.LENGTH_SHORT).show()
                delay(3000)
                roomsInit()
            }
        } while (rooms == null)
    }
}