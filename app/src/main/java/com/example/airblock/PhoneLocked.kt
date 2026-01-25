package com.example.airblock

import androidx.compose.runtime.Composable

import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp


@Composable
fun PhoneLocked() {

    Button(
        onClick = { AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered
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
            text = "Desbloquear (prueba)"
        )
    }

}