package com.example.airblock.ui.screens

import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.airblock.R

@Composable
fun PhoneUnLocked(
    onEditAppsClicked: () -> Unit
) {
    // to move from unlocked to locked without a tag
    //Column() {
        Button(
            onClick = {
                //AirBlockState.isLocked = !AirBlockState.isLocked
                onEditAppsClicked()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(
                    id = R.color.button_surface
                )
            ),
            modifier = Modifier
                .padding(bottom = 130.dp)
                .width(250.dp)
                .height(50.dp)


        ) {
            Text(
                text = stringResource(id = R.string.btn_manage),
                color = Color.White
            )
        } /*
        Button(
            onClick = { AirBlockState.isLocked = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(
                    id = R.color.button_surface
                )
            ),
            modifier = Modifier
                .padding(bottom = 130.dp)
                .width(250.dp)
                .height(50.dp),
        ) {
            Text("Test")
        }*/
    //}
}




