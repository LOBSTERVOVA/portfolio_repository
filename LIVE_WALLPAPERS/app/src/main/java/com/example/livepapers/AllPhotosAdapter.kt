package com.example.livepapers

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.livepapers.databinding.AllPhotosSingleItemBinding

class AllPhotosAdapter(var presetList: PresetList?):RecyclerView.Adapter<AllPhotosViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllPhotosViewHolder {
        val binding = AllPhotosSingleItemBinding.inflate(LayoutInflater.from(parent.context))
        return AllPhotosViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return try{
            presetList!!.presetMappedList.keys.size
        } catch (e:Exception){
            0
        }
    }

    override fun onBindViewHolder(holder: AllPhotosViewHolder, position: Int) {
        if(presetList!=null){
            val keys = presetList!!.presetMappedList.keys.toMutableList()
            val imageStr = keys[position]
            val uri = imageStr.replace("SLASH", "/").replace("COLON", ":").replace("SPACE", " ")

            Glide.with(holder.binding.imageView3)
                .load(uri)
                .centerCrop()
                .into(holder.binding.imageView3)
            holder.binding.imageView3.setOnClickListener {

            }
        }
    }
}

class AllPhotosViewHolder(val binding: AllPhotosSingleItemBinding):RecyclerView.ViewHolder(binding.root)