package com.example.airblock.ui


import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.airblock.MainActivity
import com.example.airblock.ui.screens.ModeIcons
import com.example.airblock.R
import com.example.airblock.data.TagStorage
import com.example.airblock.isAccessibilityServiceEnabled
import com.example.airblock.openAccessibilitySettings
import com.example.airblock.state.AirBlockState
import com.example.airblock.ui.screens.PhoneLocked
import com.example.airblock.ui.screens.PhoneUnLocked
import com.example.airblock.ui.screens.TagNotRegistered
import com.example.airblock.ui.screens.AppBlockingScreen


/**
 * [RESUMEN]: Main UI entry point that orchestrates the screen navigation.
 *  * Observes the [AirBlockState] and decides which screen to render
 *  * (Locked, Unlocked, or Registration) based on the current app status
 *
 */
@Composable
fun AirBlockHomeScreen() {


    val context = LocalContext.current

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


    val timerText = rememberStopwatch(AirBlockState.timerActivated, context)

    var hasPermission by remember {
        mutableStateOf(isAccessibilityServiceEnabled(context))
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {

                hasPermission = isAccessibilityServiceEnabled(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

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

        IconButton(
            onClick = { resetNfcTag(context) },
            modifier = Modifier
                .align(Alignment.TopEnd) // Posición en la esquina (asumiendo que está en un Box)
                .padding(10.dp)
                .size(50.dp) // Tamaño del área clicable
        ) {
            Icon(
                painter = painterResource(id = R.drawable.settings_24dp),
                contentDescription = "mode icon",
                modifier = Modifier.size(50.dp),
            )
        }


        if (!hasPermission) {
            AccessibilityPermissionDialog(
                onAccept = { openAccessibilitySettings(context) },
                onDismiss = { }
            )
        } else {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                //.padding(bottom = 100.dp)
            ) {
                when {
                    AirBlockState.isEditingApps -> {
                        AppBlockingScreen(
                            onBack = {
                                AirBlockState.isEditingApps = false
                            } // Al volver, APAGAMOS el interruptor
                        )
                    }

                    !AirBlockState.hasTagRegistered -> {
                        AirBlockState.timerActivated = false
                        TagNotRegistered()
                    }
                    // lock open
                    !AirBlockState.isLocked -> {
                        PhoneUnLocked(onEditAppsClicked = {
                            // Esta es la lógica real: Cambiar el estado a true
                            AirBlockState.isEditingApps = true
                        })
                        AirBlockState.timerActivated = false
                    }
                    // lock closed
                    else -> {
                        AirBlockState.timerActivated = true
                        PhoneLocked(timerText)
                    }

                }
            }
        }

    }

}

// Calculos para el temporizador
@Composable
fun rememberStopwatch(isRunning: Boolean, context: Context): String {
    var timeMillis by remember { mutableLongStateOf(0L) }

    LaunchedEffect(isRunning) {
        if (isRunning) {

            // if there is alredy a start time saved we dont need to save current time
            if (AirBlockState.startTime == 0L) {
                AirBlockState.startTime = System.currentTimeMillis()
                TagStorage.saveStartTime(context, AirBlockState.startTime)
            }

            while (true) {
                timeMillis = System.currentTimeMillis() - AirBlockState.startTime

                delay(100)
            }
        } else {
            timeMillis = 0L
        }
    }

    return formatTime(timeMillis)
}

// sacar los segundos y miutos del tiempo
@SuppressLint("DefaultLocale")
fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Preview(showBackground = true)
@Composable
fun AirBlockHome() {
    AirBlockTheme {
        AirBlockHomeScreen()
    }
}


fun resetNfcTag(context: Context) {

    AirBlockState.registeredTagId = null
    AirBlockState.hasTagRegistered = false
    AirBlockState.isLocked = false
    TagStorage.clearTag(context)
    Toast.makeText(context, context.getString(R.string.tag_eliminated), Toast.LENGTH_SHORT).show()
}

@Composable
fun AccessibilityPermissionDialog(
    onDismiss: () -> Unit,
    onAccept: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { },
        title = {
            Text(
                text = stringResource(id = R.string.dialog_title),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = colorResource(id = R.color.unlock_red)
            )
        },
        text = {
            Text(
                text = stringResource(id = R.string.dialog_content),
                fontFamily = FontFamily.Monospace,
                color = androidx.compose.ui.graphics.Color.White
            )
        },

        confirmButton = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TextButton(onClick = onAccept) {
                    Text(
                        stringResource(id = R.string.dialog_button),
                        color = colorResource(id = R.color.unlock_red),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        containerColor = colorResource(id = R.color.dark_background),
        textContentColor = androidx.compose.ui.graphics.Color.White
    )
}
