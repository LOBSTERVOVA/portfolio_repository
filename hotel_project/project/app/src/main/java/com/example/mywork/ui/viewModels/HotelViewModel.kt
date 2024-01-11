package com.example.mywork.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mywork.framework.HotelModel
import com.example.mywork.getHotel
import kotlinx.coroutines.delay

class HotelViewModel(val context: Context) : ViewModel() {
    var hotelModel: HotelModel? = null

    suspend fun hotelInit() {
        do {
            try {
                hotelModel = getHotel.getHotel()
            } catch (e: Exception) {
                Toast.makeText(context, "check your internet connection", Toast.LENGTH_SHORT).show()
                delay(3000)
                hotelInit()
            }
        }while (hotelModel==null)
    }
}