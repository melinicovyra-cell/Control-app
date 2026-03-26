package com.trollmaster.pro.ui.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trollmaster.pro.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

val NavyDark = Color(0xFF0E1220)
val NavyCard = Color(0xFF141828)
val NavyBorder = Color(0xFF1E2840)
val BlueAccent = Color(0xFF4D7CFE)

private data class Particle(val x: Float, val y: Float, val phase: Float, val size: Float)

private val particles = List(20) {
    Particle(
        x = Random.nextFloat(),
        y = Random.nextFloat(),
        phase = Random.nextFloat(),
        size = Random.nextFloat() * 2f + 1f
    )
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }

    var eggTaps by remember { mutableIntStateOf(0) }
    var eggTriggered by remember { mutableStateOf(false) }
    val eggScale = remember { Animatable(1f) }
    val eggAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.12f, animationSpec = tween(500, easing = EaseOutBack))
        scale.animateTo(1f, animationSpec = tween(200, easing = EaseInOut))
        alpha.animateTo(1f, animationSpec = tween(300))
        glowAlpha.animateTo(0.6f, animationSpec = tween(400))
        delay(200)
        subtitleAlpha.animateTo(1f, animationSpec = tween(500))
        delay(900)
        onFinished()
    }

    LaunchedEffect(eggTriggered) {
        if (eggTriggered) {
            eggAlpha.animateTo(1f, tween(150))
            repeat(3) {
                eggScale.animateTo(1.4f, tween(120))
                eggScale.animateTo(0.9f, tween(120))
            }
            eggScale.animateTo(1f, tween(100))
            delay(1500)
            eggAlpha.animateTo(0f, tween(300))
            eggTriggered = false
            eggTaps = 0
        }
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
        // Floating particles background
        FloatingParticles()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // K logo — tap 7 times for easter egg
            Box(
                modifier = Modifier.scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                // Outer glow ring
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (eggTriggered) Color(0xFFFF4444).copy(alpha = glowAlpha.value * 0.2f)
                                    else BlueAccent.copy(alpha = glowAlpha.value * 0.12f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Icon background
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(NavyCard)
                        .border(
                            1.dp,
                            if (eggTriggered) Color(0xFFFF4444).copy(alpha = 0.5f)
                            else BlueAccent.copy(alpha = 0.3f),
                            RoundedCornerShape(24.dp)
                        )
                        .alpha(alpha.value)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            eggTaps++
                            if (eggTaps >= 7) eggTriggered = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    KLogoShape(modifier = Modifier.size(72.dp))
                }

                // Progress dots for easter egg
                if (eggTaps in 1..6) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(7) { i ->
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (i < eggTaps) BlueAccent
                                        else Divider.copy(alpha = 0.5f)
                                    )
                            )
                        }
                    }
                }
            }

            // Easter egg message
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                if (eggAlpha.value > 0.01f) {
                    Text(
                        text = "🔥 RAGE MODE ACTIVATED 🔥",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF4444),
                        modifier = Modifier
                            .scale(eggScale.value)
                            .alpha(eggAlpha.value)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

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

            Spacer(modifier = Modifier.height(56.dp))

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
fun FloatingParticles(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    val time = infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f, label = "t",
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing))
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        particles.forEach { p ->
            val t = (time.value + p.phase) % 1f
            val x = (p.x + sin((t * 2 * PI + p.phase * PI * 2).toFloat()) * 0.06f).coerceIn(0.02f, 0.98f) * size.width
            val y = ((p.y - t * 0.25f + 1f) % 1f) * size.height
            val fadeAlpha = (sin(t * PI.toFloat()) * 0.22f).coerceIn(0f, 0.25f)
            drawCircle(
                color = BlueAccent.copy(alpha = fadeAlpha),
                radius = p.size * density,
                center = Offset(x, y)
            )
        }
    }
}

@Composable
fun KLogoShape(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val white = Color.White

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
