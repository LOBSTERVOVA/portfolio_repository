package com.example.mywork.ui.viewModels

import androidx.lifecycle.ViewModel
import kotlin.random.Random

class SuccessViewModel:ViewModel() {
    val random = Random.nextInt(100000, 999999)
}