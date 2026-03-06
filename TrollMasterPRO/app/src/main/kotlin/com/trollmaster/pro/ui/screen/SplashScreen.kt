package com.trollmaster.pro.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.ui.theme.*
import kotlinx.coroutines.delay

val NavyDark = Color(0xFF0E1220)
val NavyCard = Color(0xFF141828)
val NavyBorder = Color(0xFF1E2840)
val BlueAccent = Color(0xFF4D7CFE)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.12f, animationSpec = tween(500, easing = EaseOutBack))
        scale.animateTo(1f, animationSpec = tween(200, easing = EaseInOut))
        alpha.animateTo(1f, animationSpec = tween(300))
        glowAlpha.animateTo(0.6f, animationSpec = tween(400))
        delay(200)
        subtitleAlpha.animateTo(1f, animationSpec = tween(500))
        delay(800)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(NavyCard, BgDark),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // K logo box
            Box(
                modifier = Modifier.scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(BlueAccent.copy(alpha = glowAlpha.value * 0.15f))
                )
                // Icon background
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(NavyCard)
                        .alpha(alpha.value),
                    contentAlignment = Alignment.Center
                ) {
                    KLogoShape(modifier = Modifier.size(72.dp))
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name
            Text(
                text = "TrollMaster",
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )
            Text(
                text = "PRO",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BlueAccent,
                letterSpacing = 10.sp,
                modifier = Modifier
                    .scale(scale.value)
                    .alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "v4.0",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(modifier = Modifier.height(64.dp))

            LoadingDots(modifier = Modifier.alpha(subtitleAlpha.value))

            Spacer(modifier = Modifier.height(10.dp))

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
fun KLogoShape(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val white = android.graphics.Color.WHITE.let {
                androidx.compose.ui.graphics.Color(it)
            }

            // Scale from 108 viewport to actual size
            val sx = w / 108f
            val sy = h / 108f

            fun px(x: Float) = x * sx
            fun py(y: Float) = y * sy

            // Left bar
            val barPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(24f), py(20f)); lineTo(px(34f), py(20f))
                lineTo(px(34f), py(88f)); lineTo(px(24f), py(88f)); close()
            }
            drawPath(barPath, white)

            // Upper inner
            val uInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(36f), py(32f)); lineTo(px(36f), py(54f))
                lineTo(px(58f), py(47f)); lineTo(px(58f), py(27f)); close()
            }
            drawPath(uInner, white)

            // Upper outer
            val uOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(60f), py(26f)); lineTo(px(60f), py(46f))
                lineTo(px(84f), py(38f)); lineTo(px(84f), py(20f)); close()
            }
            drawPath(uOuter, white)

            // Lower inner
            val lInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(36f), py(54f)); lineTo(px(36f), py(76f))
                lineTo(px(58f), py(81f)); lineTo(px(58f), py(61f)); close()
            }
            drawPath(lInner, white)

            // Lower outer
            val lOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(60f), py(62f)); lineTo(px(60f), py(82f))
                lineTo(px(84f), py(88f)); lineTo(px(84f), py(70f)); close()
            }
            drawPath(lOuter, white)
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
        Text("●", fontSize = 10.sp, color = BlueAccent.copy(alpha = dot1.value))
        Text("●", fontSize = 10.sp, color = BlueAccent.copy(alpha = dot2.value))
        Text("●", fontSize = 10.sp, color = BlueAccent.copy(alpha = dot3.value))
    }
}
