package com.example.livepapers

import android.app.WallpaperManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri

const val IMAGE_KEY = "image_key"
const val IS_LOCK_SCREEN = "is_lock_screen"
class MyBroadcastReciever:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ONRECEIVE", "STARTED")
        val image = intent?.extras?.getString(IMAGE_KEY, "")!!
        val isLockScreen = intent.extras?.getBoolean(IS_LOCK_SCREEN, true)!!
        val bitmap = if(image!=""){
            MediaStore.Images.Media.getBitmap(context!!.contentResolver, image.toUri())
        } else {
            BitmapFactory.decodeResource(context?.resources, R.drawable.error_img)
        }

        Toast.makeText(context!!, "WALLPAPER SET", Toast.LENGTH_SHORT).show()
        if(isLockScreen){
            setLockScreenImage(bitmap, context)
        } else {
            setPhoneScreenImage(bitmap, context)
        }
        Log.d("ONRECEIVE", "OK")
    }
    private fun setLockScreenImage(bitmap: Bitmap, context: Context){
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            wallpaperManager.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
            Log.d("SETWALLPAPER", "OK")
        } catch (e: Exception) {
            Log.d("SETWALLPAPER_error", e.toString())
        }
    }
    private fun setPhoneScreenImage(bitmap: Bitmap, context: Context){
        val wallpaperManager = WallpaperManager.getInstance(context)
        try {
            wallpaperManager.setBitmap(bitmap, null, true)
            Log.d("SETWALLPAPER", "OK")
        } catch (e: Exception) {
            Log.d("SETWALLPAPER_error", e.toString())
        }
    }
}