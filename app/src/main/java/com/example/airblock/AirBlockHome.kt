package com.example.airblock


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.airblock.ui.theme.AirBlockTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.time.delay


@Composable
fun AirBlockHomeScreen() {

    // Vectors
    val finalIcon = when {
        // Sensor
        !AirBlockState.hasTagRegistered -> painterResource(id = R.drawable.sensors_24)
        // lock open
        !AirBlockState.isLocked -> painterResource(id = R.drawable.lock_open_24)
        // lock
        else -> painterResource(id = R.drawable.lock_closed_24)
    }

    val iconColor = when {
        // Sensor
        !AirBlockState.hasTagRegistered -> colorResource(id = R.color.no_tag_yellow)
        // lock open
        !AirBlockState.isLocked -> colorResource(id = R.color.unlock_red)
        // lock
        else -> colorResource(id = R.color.lock_green)

    }

    val textColor = when {
        // Sensor
        !AirBlockState.hasTagRegistered -> colorResource(id = R.color.no_tag_yellow_text)
        // lock open
        !AirBlockState.isLocked -> colorResource(id = R.color.unlock_red_text)
        // lock
        else -> colorResource(id = R.color.lock_green_text)

    }

    val pulseDuration = when {
        !AirBlockState.hasTagRegistered -> 2000
        // lock open
        !AirBlockState.isLocked -> 1500
        // lock
        else -> 3000
    }

    val iconText = when {
        !AirBlockState.hasTagRegistered -> stringResource(id = R.string.no_tag)
        // lock open
        !AirBlockState.isLocked -> stringResource(id = R.string.unlocked)
        // lock
        else -> stringResource(id = R.string.locked)
    }

    val timerText = rememberStopwatch(AirBlockState.timerActivated)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.dark_background))
            .background(iconColor.copy(alpha = 0.04f))
    ) {

        ModeIcons(
            finalIcon = finalIcon,
            iconColor = iconColor,
            textColor = textColor,
            pulseDuration = pulseDuration,
            iconText = iconText
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {
            when {
                !AirBlockState.hasTagRegistered -> {
                    AirBlockState.timerActivated = false
                    TagNotRegistered()
                }
                // lock open
                !AirBlockState.isLocked -> PhoneUnLocked()
                // lock
                else -> {
                    AirBlockState.timerActivated = true
                    PhoneLocked(timerText)
                }

            }
        }

    }

}

@Composable
fun rememberStopwatch(isRunning: Boolean): String {
    // 1. ESTADO: Aquí guardamos el tiempo actual acumulado
    var timeMillis by remember { mutableLongStateOf(0L) }

    // 2. MOTOR: Se enciende cuando 'isRunning' cambia
    LaunchedEffect(isRunning) {
        if (isRunning) {
            // A. Marca el momento exacto de inicio
            val startTime = System.currentTimeMillis()

            // B. Bucle infinito (mientras isRunning siga siendo true)
            while (true) {
                // Calculamos la diferencia: (Hora Actual - Hora Inicio)
                // Esto es más preciso que sumar +1 cada segundo
                timeMillis = System.currentTimeMillis() - startTime

                // Esperamos un poquito para no quemar el procesador
                // 100ms es suficiente para que se sienta fluido
                delay(100)
            }
        } else {
            // C. Si se apaga (isRunning = false), reseteamos a 0
            timeMillis = 0L
        }
    }

    // 3. SALIDA: Usamos la función matemática de arriba
    return formatTime(timeMillis)
}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    // %02d significa: "número de 2 dígitos, rellena con 0 si hace falta"
    return String.format("%02d:%02d", minutes, seconds)
}


@Preview(showBackground = true)
@Composable
fun AirBlockHome() {
    AirBlockTheme {
        AirBlockHomeScreen()
    }
}