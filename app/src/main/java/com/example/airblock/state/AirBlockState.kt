package com.example.airblock.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.airblock.data.TagStorage

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

    // 👇 La lista viva de apps bloqueadas
    // Usamos una lista observable para que la UI se actualice al marcar/desmarcar
    val blockedApps = mutableStateListOf<String>()

    // Función auxiliar para cargar lo guardado al iniciar la app
    fun loadBlockedApps(context: Context) {
        val saved = TagStorage.getBlockedApps(context)
        blockedApps.clear()
        blockedApps.addAll(saved)
    }

    var isEditingApps by mutableStateOf(false)
}
