package com.trollmaster.pro.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.ui.theme.BgDark
import com.trollmaster.pro.ui.theme.GreenAccent
import com.trollmaster.pro.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val pulseScale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.1f, animationSpec = tween(600, easing = EaseOutBack))
        scale.animateTo(1f, animationSpec = tween(200))
        alpha.animateTo(1f, animationSpec = tween(300))
        delay(200)
        subtitleAlpha.animateTo(1f, animationSpec = tween(400))
        delay(300)
        // Pulse effect
        repeat(2) {
            pulseScale.animateTo(1.05f, animationSpec = tween(300))
            pulseScale.animateTo(1f, animationSpec = tween(300))
        }
        delay(400)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(Color(0xFF0D1A0D), BgDark),
                    radius = 800f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Box(
                modifier = Modifier
                    .scale(scale.value * pulseScale.value),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "☠",
                    fontSize = 96.sp,
                    color = GreenAccent,
                    modifier = Modifier.alpha(scale.value)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App name
            Text(
                text = "TrollMaster",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = GreenAccent,
                letterSpacing = 2.sp,
                modifier = Modifier.scale(scale.value)
            )
            Text(
                text = "PRO",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = GreenAccent.copy(alpha = 0.7f),
                letterSpacing = 8.sp,
                modifier = Modifier.scale(scale.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "v4.0",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Loading dots
            LoadingDots(modifier = Modifier.alpha(subtitleAlpha.value))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Инициализация...",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

@Composable
fun LoadingDots(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dot1 = infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f, label = "d1",
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse)
    )
    val dot2 = infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f, label = "d2",
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse)
    )
    val dot3 = infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f, label = "d3",
        animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse)
    )

    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("●", fontSize = 10.sp, color = GreenAccent.copy(alpha = dot1.value))
        Text("●", fontSize = 10.sp, color = GreenAccent.copy(alpha = dot2.value))
        Text("●", fontSize = 10.sp, color = GreenAccent.copy(alpha = dot3.value))
    }
}
