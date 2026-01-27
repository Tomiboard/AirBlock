package com.example.airblock


import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun PhoneLocked(timerText: String) {

    Column() {
        TimerScreen(timerText)


        Button(
            onClick = {
                AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered
                AirBlockState.isLocked = !AirBlockState.isLocked

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(
                    id = R.color.button_surface
                )
            ),
            modifier = Modifier
                .padding(bottom = 30.dp, top = 130.dp)
                .width(250.dp)
                .height(50.dp)


        ) {
            Text(
                text = "unblock"
            )
        }
    }
}



@Preview
@Composable
fun PhoneLockedPreview() {
    PhoneLocked(timerText = "00:00")
}