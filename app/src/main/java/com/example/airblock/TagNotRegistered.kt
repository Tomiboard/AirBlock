package com.example.airblock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TagNotRegistered() {

    Column() {


        Button(
            onClick = { AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(
                    id = R.color.button_surface
                )
            ),
            modifier = Modifier
                .padding(bottom = 30.dp)
                .widthIn(min = 250.dp)
                .height(50.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = stringResource(id = R.string.bts_scan),
                color = Color.White
            )
        }

        Text( // Falta un on click listener para link amazon
            text = stringResource(id = R.string.buy_tag),
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(bottom = 40.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Text(
            text = stringResource(id = R.string.any_tag_info),
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )


    }


}

@Preview
@Composable
fun TagNotRegisteredPreview() {


    TagNotRegistered()
}
