package com.example.airblock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AirBlockState   {
    var hasTagRegistered by mutableStateOf(false)
    var isLocked by mutableStateOf(false)

    var timerActivated by mutableStateOf(false)

}