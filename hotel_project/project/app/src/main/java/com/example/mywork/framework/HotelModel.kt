package com.example.mywork.framework

data class HotelModel(
    val id: Int,
    val name:String,
    val adress:String,
    val minimal_price:Int,
    val price_for_it:String,
    val rating:Int,
    val rating_name:String,
    val image_urls:MutableList<String>,
    val about_the_hotel:AboutTheHotel
)
data class AboutTheHotel(
    val description:String,
    val peculiarities:MutableList<String>
)