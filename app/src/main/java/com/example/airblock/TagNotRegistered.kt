package com.example.airblock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagNotRegistered(
    finalIcon: Painter,
    iconColor: Color,
    textColor: Color,
    pulseDuration: Int,
    iconText: String,
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

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {

            Button(
                onClick = { /*AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered*/
                    when {

                        !AirBlockState.hasTagRegistered -> AirBlockState.hasTagRegistered =
                            !AirBlockState.hasTagRegistered
                        // lock open
                        AirBlockState.hasTagRegistered && !AirBlockState.isLocked -> AirBlockState.isLocked =
                            !AirBlockState.isLocked
                        // lock
                        else -> {
                            AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered
                            AirBlockState.isLocked = !AirBlockState.isLocked
                        }

                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(
                        id = R.color.button_surface
                    )
                ),
                modifier = Modifier
                    .padding(bottom = 30.dp)
                    .width(250.dp)
                    .height(50.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = stringResource(id = R.string.bts_scan)
                )
            }

            Text( // Falta un on click listener para link amazon
                text = stringResource(id = R.string.buy_tag),
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(bottom = 40.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = stringResource(id = R.string.any_tag_info),
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )


        }


    }

}

@Preview
@Composable
fun TagNotRegisteredPreview() {

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
        else -> 4000
    }

    val iconText = when {
        !AirBlockState.hasTagRegistered -> stringResource(id = R.string.no_tag)
        // lock open
        !AirBlockState.isLocked -> stringResource(id = R.string.unlocked)
        // lock
        else -> stringResource(id = R.string.locked)
    }

    TagNotRegistered(
        finalIcon = finalIcon,
        iconColor = iconColor,
        textColor = textColor,
        pulseDuration = pulseDuration,
        iconText = iconText
    )
}
