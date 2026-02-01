package com.example.airblock

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue


object AirBlockState   {
    var hasTagRegistered by mutableStateOf(false)
    var isLocked by mutableStateOf(false)

    private val _timerActivated = mutableStateOf(false)
    var timerActivated: Boolean
        get() = _timerActivated.value // Al leer, te damos el valor
        set(value) {
            _timerActivated.value = value // Guardamos el nuevo valor

            // 👇 AQUÍ ESTÁ EL TRUCO AUTOMÁTICO
            // Si el valor nuevo es 'false' (apagado), reseteamos el tiempo a 0
            if (!value) {
                startTime = 0L
            }
        }

    var registeredTagId: String? = null

    var startTime by mutableLongStateOf(0L)

}