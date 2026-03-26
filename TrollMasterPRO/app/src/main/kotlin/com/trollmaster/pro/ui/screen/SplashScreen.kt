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

private val bgParticles = List(22) {
    Particle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 2f + 0.8f)
}
private val bgStars = List(80) {
    Particle(Random.nextFloat(), Random.nextFloat(), Random.nextFloat(), Random.nextFloat() * 1.4f + 0.4f)
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val glowAlpha = remember { Animatable(0f) }
    val shakeX = remember { Animatable(0f) }

    var eggTaps by remember { mutableIntStateOf(0) }
    var eggTriggered by remember { mutableStateOf(false) }
    val eggLogoScale = remember { Animatable(1f) }
    val eggMsgAlpha = remember { Animatable(0f) }
    var rageMode by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        scale.animateTo(1.15f, tween(480, easing = EaseOutBack))
        scale.animateTo(1f, tween(180, easing = EaseInOut))
        alpha.animateTo(1f, tween(280))
        glowAlpha.animateTo(0.7f, tween(380))
        delay(180)
        subtitleAlpha.animateTo(1f, tween(480))
        delay(900)
        onFinished()
    }

    LaunchedEffect(eggTriggered) {
        if (!eggTriggered) return@LaunchedEffect
        rageMode = true
        eggMsgAlpha.animateTo(1f, tween(120))
        // Screen shake
        for (i in 0 until 10) {
            shakeX.animateTo(if (i % 2 == 0) 14f else -14f, tween(45))
        }
        shakeX.animateTo(0f, tween(80))
        // Logo bounce
        repeat(3) {
            eggLogoScale.animateTo(1.55f, tween(110))
            eggLogoScale.animateTo(0.82f, tween(110))
        }
        eggLogoScale.animateTo(1f, tween(90))
        delay(1800)
        eggMsgAlpha.animateTo(0f, tween(350))
        rageMode = false
        eggTriggered = false
        eggTaps = 0
    }

    val bgColors = if (rageMode)
        listOf(Color(0xFF3D0000), Color(0xFF1A0A0A), BgDark)
    else
        listOf(Color(0xFF141E3A), Color(0xFF0D1228), BgDark)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(bgColors, radius = 1100f)),
        contentAlignment = Alignment.Center
    ) {
        // Twinkling star field
        StarFieldCanvas()

        // Rising blue particles
        FloatingParticles()

        // Main content — shake offset applied here
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.offset(x = shakeX.value.dp)
        ) {

            // ── K Logo ──────────────────────────────────────────────
            Box(
                modifier = Modifier.scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                // Animated outer glow ring
                val glowPulse = rememberInfiniteTransition(label = "gp")
                val glowRing = glowPulse.animateFloat(
                    120f, 134f, label = "gr",
                    animationSpec = infiniteRepeatable(tween(1600), RepeatMode.Reverse)
                )
                Box(
                    modifier = Modifier
                        .size(glowRing.value.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    if (rageMode) Color(0xFFFF2020).copy(alpha = 0.28f)
                                    else BlueAccent.copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )
                // Icon card
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    NavyCard,
                                    if (rageMode) Color(0xFF280000) else Color(0xFF182244)
                                )
                            )
                        )
                        .border(
                            2.dp,
                            if (rageMode) Color(0xFFFF2020).copy(alpha = 0.8f)
                            else BlueAccent.copy(alpha = 0.45f),
                            RoundedCornerShape(24.dp)
                        )
                        .alpha(alpha.value)
                        .scale(eggLogoScale.value)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            eggTaps++
                            if (eggTaps >= 7) eggTriggered = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    KLogoShape(
                        modifier = Modifier.size(72.dp),
                        tint = if (rageMode) Color(0xFFFF4444) else Color.White
                    )
                }

                // Easter egg tap progress dots
                if (eggTaps in 1..6) {
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 18.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        repeat(7) { i ->
                            val filled = i < eggTaps
                            Box(
                                modifier = Modifier
                                    .size(if (filled) 6.dp else 4.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        if (filled) BlueAccent else Divider.copy(alpha = 0.35f)
                                    )
                            )
                        }
                    }
                }
            }

            // ── Easter egg message slot ──────────────────────────────
            Box(modifier = Modifier.height(38.dp), contentAlignment = Alignment.Center) {
                if (eggMsgAlpha.value > 0.01f) {
                    Text(
                        "🔥  RAGE MODE  🔥",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF3030),
                        letterSpacing = 3.sp,
                        modifier = Modifier.alpha(eggMsgAlpha.value)
                    )
                }
            }

            // ── App title ────────────────────────────────────────────
            Text(
                "TrollMaster",
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = if (rageMode) Color(0xFFFF4444) else TextPrimary,
                letterSpacing = 1.5.sp,
                modifier = Modifier.scale(scale.value).alpha(alpha.value)
            )
            Text(
                "PRO",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (rageMode) Color(0xFFFF6666) else BlueAccent,
                letterSpacing = 12.sp,
                modifier = Modifier.scale(scale.value).alpha(alpha.value)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "v4.0",
                fontSize = 12.sp,
                color = TextSecondary,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )

            Spacer(Modifier.height(60.dp))

            LoadingDots(
                modifier = Modifier.alpha(subtitleAlpha.value),
                rage = rageMode
            )

            Spacer(Modifier.height(10.dp))

            Text(
                if (rageMode) "АКТИВАЦИЯ ЯРОСТИ..." else "Инициализация...",
                fontSize = 12.sp,
                color = if (rageMode) Color(0xFFFF6666) else TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }
    }
}

// ── Twinkling star field ─────────────────────────────────────────────────────

@Composable
fun StarFieldCanvas(modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "stars")
    val time = tr.animateFloat(
        0f, 1f, label = "t",
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing))
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        bgStars.forEach { s ->
            val a = (sin((time.value + s.phase) * 2f * PI.toFloat()) * 0.5f + 0.5f) * 0.55f
            drawCircle(Color.White.copy(alpha = a), s.size * density, Offset(s.x * size.width, s.y * size.height))
        }
    }
}

// ── Rising particles ─────────────────────────────────────────────────────────

@Composable
fun FloatingParticles(modifier: Modifier = Modifier) {
    val tr = rememberInfiniteTransition(label = "fp")
    val time = tr.animateFloat(
        0f, 1f, label = "t",
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing))
    )
    Canvas(modifier = modifier.fillMaxSize()) {
        bgParticles.forEach { p ->
            val t = (time.value + p.phase) % 1f
            val x = (p.x + sin((t * 2f * PI + p.phase * PI * 2f).toFloat()) * 0.06f).coerceIn(0.02f, 0.98f) * size.width
            val y = ((p.y - t * 0.25f + 1f) % 1f) * size.height
            val a = (sin(t * PI.toFloat()) * 0.22f).coerceIn(0f, 0.25f)
            drawCircle(BlueAccent.copy(alpha = a), p.size * density, Offset(x, y))
        }
    }
}

// ── K logo shape ─────────────────────────────────────────────────────────────

@Composable
fun KLogoShape(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Box(modifier = modifier) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            val sx = w / 108f; val sy = h / 108f
            fun px(x: Float) = x * sx
            fun py(y: Float) = y * sy

            val barPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(24f), py(20f)); lineTo(px(34f), py(20f))
                lineTo(px(34f), py(88f)); lineTo(px(24f), py(88f)); close()
            }
            drawPath(barPath, tint)

            val uInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(36f), py(32f)); lineTo(px(36f), py(54f))
                lineTo(px(58f), py(47f)); lineTo(px(58f), py(27f)); close()
            }
            drawPath(uInner, tint)

            val uOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(60f), py(26f)); lineTo(px(60f), py(46f))
                lineTo(px(84f), py(38f)); lineTo(px(84f), py(20f)); close()
            }
            drawPath(uOuter, tint)

            val lInner = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(36f), py(54f)); lineTo(px(36f), py(76f))
                lineTo(px(58f), py(81f)); lineTo(px(58f), py(61f)); close()
            }
            drawPath(lInner, tint)

            val lOuter = androidx.compose.ui.graphics.Path().apply {
                moveTo(px(60f), py(62f)); lineTo(px(60f), py(82f))
                lineTo(px(84f), py(88f)); lineTo(px(84f), py(70f)); close()
            }
            drawPath(lOuter, tint)
        }
    }
}

// ── Animated loading dots ────────────────────────────────────────────────────

@Composable
fun LoadingDots(modifier: Modifier = Modifier, rage: Boolean = false) {
    val tr = rememberInfiniteTransition(label = "dots")
    val d1 = tr.animateFloat(0.3f, 1f, label = "d1", animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse))
    val d2 = tr.animateFloat(0.3f, 1f, label = "d2", animationSpec = infiniteRepeatable(tween(600, delayMillis = 200), RepeatMode.Reverse))
    val d3 = tr.animateFloat(0.3f, 1f, label = "d3", animationSpec = infiniteRepeatable(tween(600, delayMillis = 400), RepeatMode.Reverse))
    val color = if (rage) Color(0xFFFF4444) else BlueAccent
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Text("●", fontSize = 10.sp, color = color.copy(alpha = d1.value))
        Text("●", fontSize = 10.sp, color = color.copy(alpha = d2.value))
        Text("●", fontSize = 10.sp, color = color.copy(alpha = d3.value))
    }
}
