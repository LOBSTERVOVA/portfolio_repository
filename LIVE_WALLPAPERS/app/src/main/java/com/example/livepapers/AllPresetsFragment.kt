package com.example.livepapers

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.example.livepapers.databinding.FragmentAllPresetsBinding
import com.google.gson.Gson

class AllPresetsFragment : Fragment() {
    var binding:FragmentAllPresetsBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentAllPresetsBinding.inflate(layoutInflater)

        binding!!.recycler.layoutManager = GridLayoutManager(requireContext(), 2)

        val presets:PresetList? = try {
            val inputStream = requireActivity().openFileInput(ALL_PRESETS_KEY)
            val textFile = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()

            Gson().fromJson(textFile, PresetList::class.java)
        }catch (e:Exception){
            null
        }
        val adapter = AllPhotosAdapter(presets)
        binding!!.recycler.adapter = adapter

        return binding!!.root
    }


}