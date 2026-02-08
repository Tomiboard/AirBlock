package com.example.airblock.ui.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.airblock.R
import com.example.airblock.state.AirBlockState


@Composable
fun PhoneLocked(timerText: String) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        // 2. Centrar los elementos (Timer y Botón) horizontalmente
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(50.dp)
    ) {
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
                .padding(bottom = 30.dp)
                .width(250.dp)
                .height(50.dp)


        ) {
            Text(
                text = "unblock",
                color = Color.White
            )
        }
    }
}


@Preview
@Composable
fun PhoneLockedPreview() {
    PhoneLocked(timerText = "00:00")
}