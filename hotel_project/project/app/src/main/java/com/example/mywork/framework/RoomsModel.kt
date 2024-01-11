package com.example.mywork.framework

data class Rooms(
    val rooms:List<RoomModel>
)
data class RoomModel(
    val id:Int,
    val name:String,
    val price:Int,
    val price_per:String,
    val peculiarities:List<String>,
    val image_urls: MutableList<String>
)