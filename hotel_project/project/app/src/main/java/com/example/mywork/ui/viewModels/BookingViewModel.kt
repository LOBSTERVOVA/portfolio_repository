package com.example.mywork.ui.viewModels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.example.mywork.adapters.BookingRecyclerAdapter
import com.example.mywork.framework.BookingInfo
import com.example.mywork.framework.DaggerMyComponent
import com.example.mywork.framework.MyDaggerModule
import com.example.mywork.getHotel
import kotlinx.coroutines.delay

class BookingViewModel(val context:Context):ViewModel() {
    var adapter:BookingRecyclerAdapter? = null
    var bookingInfo: BookingInfo? = null
    var numberTextFieldText = "+7 (***) ***-**-**"
    var emailTextFieldText = ""

    suspend fun bookingInit() {
        do {
            try {
                bookingInfo = getHotel.getBooking()
            } catch (e: Exception) {
                Toast.makeText(context, "check your internet connection", Toast.LENGTH_SHORT).show()
                delay(3000)
                bookingInit()
            }
        } while (bookingInfo == null)
    }
}