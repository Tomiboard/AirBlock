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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ModeIcons(
    finalIcon: Painter,
    iconColor: Color,
    textColor: Color,
    pulseDuration: Int,
    iconText: String,

) {

// 1. CONFIGURACIÓN DEL PARPADEO (ALERTA)
    key(pulseDuration) {
        val infiniteTransition = rememberInfiniteTransition(label = "screen_flash")

        val flashAlpha by infiniteTransition.animateFloat(
            initialValue = 0.05f, // min intensity
            targetValue = 0.25f,  // max intensity
            animationSpec = infiniteRepeatable(
                // expansion time
                animation = tween(pulseDuration, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse // Sube y baja suavemente
            ),
            label = "flash_alpha"
        )

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

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-150).dp)
        ) {
            Icon(
                painter = finalIcon,
                contentDescription = "mode icon",
                modifier = Modifier
                    .size(240.dp)
                    //.align(Alignment.Center)
                    //.offset(y = (-150).dp)
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
            Text(
                text = iconText,
                color = textColor,
                fontSize = 34.sp,
                textAlign = TextAlign.Center,
                style = TextStyle(
                    fontFamily = FontFamily.Default, // Esto usa Roboto en Android
                    fontWeight = FontWeight.Black,   // O FontWeight.Bold
                    fontSize = 24.sp,
                    letterSpacing = 2.sp, // 👈 Esto le da el toque "Premium/Cine"
                    // El efecto de brillo/borroso de la imagen:
                    shadow = Shadow(
                        color = iconColor,
                        blurRadius = 15f
                    )
                ),
                modifier = Modifier
                    .padding(top = 30.dp)
                    .align(Alignment.CenterHorizontally)
                    .width(240.dp),
            )
        }
    }

}
}

/*
@Preview
@Composable
fun ModeIconsPreview() {
    ModeIcons()

}*/