package com.example.mywork.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mywork.R
import com.example.mywork.adapters.BookingRecyclerAdapter
import com.example.mywork.databinding.FragmentBookingBinding
import com.example.mywork.framework.DaggerMyComponent
import com.example.mywork.framework.MyDaggerModule
import com.example.mywork.ui.viewModels.BookingViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val NUM_SYMBOLS = "1234567890"

class BookingFragment : Fragment() {

    private val viewModel by viewModels<BookingViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BookingViewModel(requireContext()) as? T ?: throw IllegalStateException()
            }
        }
    }

    @Inject
    lateinit var adapter: BookingRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentBookingBinding.inflate(layoutInflater)

        if(viewModel.adapter == null){
            DaggerMyComponent.builder()
                .myDaggerModule(MyDaggerModule(requireContext()))
                .build()
                .inject(this)
            viewModel.adapter = adapter
        }

        binding.imageView5.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.recyclerView2.layoutManager = GridLayoutManager(requireContext(), 1)
        binding.recyclerView2.adapter = viewModel.adapter
        binding.imagePlus.setOnClickListener {
            val error = viewModel.adapter!!.addUser()
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }

        lifecycleScope.launch {

            if(viewModel.bookingInfo==null){
                viewModel.bookingInit()
            }
            val bookInfo = viewModel.bookingInfo!!

            with(binding) {
                placeholder.visibility = View.GONE
                name2.text = bookInfo.hotel_name
                rating2.text = "$STAR ${bookInfo.horating} ${bookInfo.rating_name}"
                address2.text = bookInfo.hotel_adress
                deploy.text = bookInfo.departure
                city.text = bookInfo.arrival_country
                dates.text = "${bookInfo.tour_date_start}-${bookInfo.tour_date_stop}"
                nights.text = "${bookInfo.number_of_nights.toString()} ночей"
                hotelText.text = bookInfo.hotel_name
                roomHotel.text = bookInfo.room
                food.text = bookInfo.nutrition
                tourT.text = bookInfo.tour_price.toString()+" ₽"
                fuelPriceT.text = bookInfo.fuel_charge.toString()+" ₽"
                servicePriceT.text = bookInfo.service_charge.toString()+" ₽"
                totalPriceT.text = (bookInfo.tour_price+bookInfo.fuel_charge+bookInfo.service_charge).toString()+" ₽"
                buttonPay.text = "Оплатить "+totalPriceT.text
                editTextTextEmailAddress.setText(viewModel.emailTextFieldText)
                editTextPhone.setText(viewModel.numberTextFieldText)
                var phoneText = binding.editTextPhone.text.toString()
                editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                editTextPhone.setOnClickListener {
                    editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                    editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                }

                buttonPay.setOnClickListener {
                    var touristsCheckOk = viewModel.adapter!!.checkFields()
                    if (editTextPhone.text.contains("*")) {
                        editTextPhone.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.error_color
                            )
                        )
                        touristsCheckOk = false
                    }
                    if (!isValidEmail(editTextTextEmailAddress.text.toString())) {
                        editTextTextEmailAddress.backgroundTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.error_color
                            )
                        )
                        touristsCheckOk = false
                    }
                    if(touristsCheckOk){
                        val intent = Intent(requireContext(), SuccessActivity::class.java)
                        startActivity(intent)
                    }
                }

                editTextTextEmailAddress.setOnFocusChangeListener { _, hasFocus ->
                    if (!hasFocus) {
                        if (!isValidEmail(editTextTextEmailAddress.text.toString())) {
                            editTextTextEmailAddress.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.error_color
                                )
                            )

                        }
                    } else {
                        editTextTextEmailAddress.backgroundTintList = null

                    }
                }

                editTextPhone.setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                        Log.d("BACKSPACE", "pressed")
                        for (i in phoneText.length - 1 downTo 3) {
                            if (NUM_SYMBOLS.contains(phoneText[i])) {
                                Log.d("FOUND", phoneText[i].toString())
                                phoneText = phoneText.reversed()
                                    .replaceFirst(phoneText[i].toString(), "*").reversed()
                                editTextPhone.setText(phoneText)
                                editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                                break
                            }
                        }
                        return@setOnKeyListener true
                    }
                    return@setOnKeyListener false
                }
                editTextTextEmailAddress.addTextChangedListener {
                    viewModel.emailTextFieldText = it.toString()
                }
                editTextPhone.addTextChangedListener {
                    editTextPhone.backgroundTintList = null

                    Log.d("EditText", editTextPhone.text.toString())
                    Log.d("PhoneText", phoneText)
                    if (editTextPhone.text.length > ("+7 (***) ***-**-**").length && !editTextPhone.text.contains(
                            "*"
                        )
                    ) {
                        editTextPhone.setText(phoneText)
                        editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                        return@addTextChangedListener
                    }
                    if (editTextPhone.text.length > phoneText.length) {
                        for (i in 0 until phoneText.length) {
                            if (phoneText[i].toString() == "*") {
                                Log.d("FOUND", i.toString())
                                phoneText = phoneText.replaceFirst(
                                    "*",
                                    editTextPhone.text.toString()[i].toString()
                                )
                                editTextPhone.setText(phoneText)
                                viewModel.numberTextFieldText=phoneText
                                editTextPhone.setSelection(getPos(editTextPhone.text.toString()))
                                break
                            }
                        }
                        Log.d("PhoneTextReplaced", phoneText)
                    }
                }
            }
        }
        return binding.root
    }

    private fun getPos(s: String): Int {
        var a = 0
        for (i in 0 until s.length) {
            if (s[i].toString() == "*") return a else a++
        }
        return s.length
    }

    fun isValidEmail(email: String): Boolean {
        Log.d("EMAIL CHECKING", Patterns.EMAIL_ADDRESS.matcher(email).matches().toString())
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}