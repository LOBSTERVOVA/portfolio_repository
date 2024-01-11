package com.example.mywork.framework

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.example.mywork.adapters.BookingRecyclerAdapter
import com.example.mywork.adapters.RecyclerViewRoomsAdapter
import com.example.mywork.ui.BookingFragment
import com.example.mywork.ui.RoomFragment
import dagger.Component
import dagger.Module
import dagger.Provides

@Module
class MyDaggerModule(private val context: Context) {
    @Provides
    fun provideAdapter(): BookingRecyclerAdapter {
        return BookingRecyclerAdapter(context)
    }
}

@Component(modules = [MyDaggerModule::class])
interface MyComponent {
    fun inject(fragment: BookingFragment)
}

@Module
class MyDaggerRoomModule(
    private val rooms: Rooms,
    private val activity: FragmentActivity,
    private val fragmentManager: FragmentManager,
    private val hotelModel: HotelModel
) {
    @Provides
    fun provideAdapter(): RecyclerViewRoomsAdapter {
        return RecyclerViewRoomsAdapter(rooms, activity, fragmentManager, hotelModel)
    }
}

@Component(modules = [MyDaggerRoomModule::class])
interface MyRoomComponent {
    fun inject(fragment: RoomFragment)
}