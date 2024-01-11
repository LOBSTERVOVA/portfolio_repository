package com.example.mywork.adapters

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mywork.R
import com.example.mywork.databinding.TouristLayoutBinding
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.*

val numbers =
    listOf("Первый турист", "Второй турист", "Третий турист", "Четвертый турист", "Пятый турист")

class BookingRecyclerAdapter(val context:Context) : RecyclerView.Adapter<BookingViewHolder>() {

    var tourNum = 1
    val editTextsList = mutableListOf<TextInputEditText>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = TouristLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return BookingViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tourNum
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        with(holder.binding) {

            editTextsList.add(name)
            editTextsList.add(secName)
            editTextsList.add(dob)
            editTextsList.add(country)
            editTextsList.add(pasNum)
            editTextsList.add(pasDate)

            tourNum.text = numbers[position]
            imageDown.setOnClickListener {
                Log.d("VISIBILITY", "changed on visible")
                val wasHidden = (hiddenInfo.visibility == View.GONE)
                holder.binding.hiddenInfo.visibility = View.VISIBLE
                val rotationAnimation = if(wasHidden)
                    ObjectAnimator.ofFloat(holder.binding.imageDown, "rotation", 0f, -180f) else
                    ObjectAnimator.ofFloat(holder.binding.imageDown, "rotation", -180f, 0f)
                rotationAnimation.duration = 200
                rotationAnimation.interpolator = AccelerateDecelerateInterpolator()

                val alphaAnimation = if(!wasHidden)
                    ObjectAnimator.ofFloat(holder.binding.hiddenInfo, "alpha", 1f, 0f) else
                    ObjectAnimator.ofFloat(holder.binding.hiddenInfo, "alpha", 0f, 1f)
                alphaAnimation.duration = 200
                alphaAnimation.interpolator = AccelerateDecelerateInterpolator()
                val animatorSet = AnimatorSet()
                animatorSet.playTogether(rotationAnimation, alphaAnimation)
                animatorSet.start()

                if(!wasHidden){
                    CoroutineScope(Dispatchers.Default).launch {
                        delay(200)
                        withContext(Dispatchers.Main){
                            holder.binding.hiddenInfo.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }

    fun checkFields():Boolean{
        Log.d("checkFields", "started")
        Log.d("edisTExtList", editTextsList.toString())
        var a = true
        editTextsList.forEach {
            Log.d("text = ", "\'"+it.text.toString()+"\'")
            if(it.text.toString()==""||it.text == null){
                Log.d(it.toString(), "is empty")
                it.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context,
                    R.color.error_color
                ))
                a = false
            } else {
                it.backgroundTintList = null
            }
        }
        return a
    }

    fun addUser():String? {
        if(tourNum==5)return "Можно добавить только 5 туристов"
        tourNum++
        notifyItemInserted(tourNum - 1)
        return null
    }
}

class BookingViewHolder(val binding: TouristLayoutBinding) : RecyclerView.ViewHolder(binding.root)