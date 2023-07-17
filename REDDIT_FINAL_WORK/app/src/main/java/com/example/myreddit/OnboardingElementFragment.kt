package com.example.myreddit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myreddit.databinding.FragmentOnboardingElementBinding


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"

class OnboardingElementFragment : Fragment() {
    var binding:FragmentOnboardingElementBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOnboardingElementBinding.inflate(layoutInflater)

        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.takeIf { it.containsKey(ARG_PARAM1)&&it.containsKey(ARG_PARAM2)&&it.containsKey(ARG_PARAM3) }?.apply {
            binding!!.textView1.text = getString(ARG_PARAM1)
            binding!!.textView2.text = getString(ARG_PARAM2)
            binding!!.image.setImageResource(getInt(ARG_PARAM3))
            if(binding!!.textView1.text != "Welcome to My Reddit!!!")binding!!.image.setColorFilter(
                ContextCompat.getColor(requireContext(), R.color.red))
        }
    }
}