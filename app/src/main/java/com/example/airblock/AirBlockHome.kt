package com.example.airblock

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.airblock.ui.theme.AirBlockTheme


@Composable
fun AirBlockHomeScreen() {

    // Vectors
    val finalPainter = if (!AirBlockState.hasTagRegistered) {
        // CASO 1: Sensor
        painterResource(id = R.drawable.sensors_24)
    } else {
        // CASO 2: lock open
        painterResource(id = R.drawable.lock_open_24)
    }

    val iconColor = if (!AirBlockState.hasTagRegistered) {
        colorResource(id = R.color.no_tag_yellow)
    } else {
        colorResource(id = R.color.unlock_red)
    }

// 1. CONFIGURACIÓN DEL PARPADEO (ALERTA)
    val infiniteTransition = rememberInfiniteTransition(label = "screen_flash")

    val flashAlpha by infiniteTransition.animateFloat(
        initialValue = 0.05f, // min intensity
        targetValue = 0.25f,  // max intensity
        animationSpec = infiniteRepeatable(
            // expansion time
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse // Sube y baja suavemente
        ),
        label = "flash_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.dark_background))
            .background(iconColor.copy(alpha = 0.04f))
    ) {

// 2. EL BOX PRINCIPAL CON EL FONDO ANIMADO
        Box(
            modifier = Modifier
                .fillMaxSize()
                // Capa 1: Fondo Negro Sólido
                .background(Color.Black)
                // Capa 2: El tinte que parpadea
                .background(
                    // 👇 Aquí usamos el valor animado 'flashAlpha'
                    color = iconColor.copy(alpha = flashAlpha)
                ),
            contentAlignment = Alignment.Center
        ) {

            Icon(
                painter = finalPainter,
                contentDescription = "mode icon",
                modifier = Modifier
                    .size(240.dp)
                    .align(Alignment.Center)
                    .offset(y = (-150).dp)
                    .drawBehind { // Neon efect

                        val shadowSizeMultiplier = 1.8f
                        val shadowRadius = (size.maxDimension) / 2 * shadowSizeMultiplier

                        val shadowBrush = Brush.radialGradient(
                            colorStops = arrayOf(

                                0.0f to iconColor.copy(alpha = 0.2f),

                                0.5f to iconColor.copy(alpha = 0.1f),

                                0.85f to iconColor.copy(alpha = 0.05f),

                                // Borde final transparente
                                1.0f to Color.Transparent
                            ),
                            center = center,
                            radius = shadowRadius
                        )

                        drawCircle(
                            brush = shadowBrush,
                            radius = shadowRadius,
                            center = center
                        )
                    },

                tint = iconColor
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 100.dp)
        ) {

            Button(
                onClick = { AirBlockState.hasTagRegistered = !AirBlockState.hasTagRegistered },
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
                    text = if (!AirBlockState.hasTagRegistered) stringResource(id = R.string.bts_scan)
                    else stringResource(id = R.string.btn_manage)
                )
            }

            if (!AirBlockState.hasTagRegistered) {
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

}

@Preview(showBackground = true)
@Composable
fun AirBlockHome() {
    AirBlockTheme {
        AirBlockHomeScreen()
    }
}