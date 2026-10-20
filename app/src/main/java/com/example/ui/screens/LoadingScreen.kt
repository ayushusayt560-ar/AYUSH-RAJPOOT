package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

data class FloatingSymbol(
    val symbol: String,
    val initialX: Float,
    val initialY: Float,
    val sizeSp: Int,
    val color: Color,
    val speed: Float
)

@Composable
fun LoadingScreen(
    isMusicMuted: Boolean = false,
    onToggleMusic: () -> Unit = {},
    onFinishLoading: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Progress animation from 0.0 to 1.0
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3600, easing = FastOutSlowInEasing)
        )
        delay(300)
        onFinishLoading()
    }

    // Dynamic rotation for geometric rings
    val infiniteTransition = rememberInfiniteTransition(label = "loading_anim")
    val rotationOuter by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_outer"
    )
    val rotationInner by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot_inner"
    )
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    // Animated gradient colors
    val colorShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color_shift"
    )

    // Floating colorful math symbols
    val floatingSymbols = remember {
        listOf(
            FloatingSymbol("π", 0.15f, 0.18f, 26, Color(0xFFFF2A85), 1.2f),
            FloatingSymbol("∑", 0.82f, 0.15f, 28, Color(0xFF00F0FF), 0.9f),
            FloatingSymbol("√", 0.12f, 0.65f, 24, Color(0xFFFFD700), 1.4f),
            FloatingSymbol("∞", 0.85f, 0.70f, 26, Color(0xFF00FFA3), 1.1f),
            FloatingSymbol("Δ", 0.22f, 0.42f, 22, Color(0xFFB026FF), 0.8f),
            FloatingSymbol("≠", 0.78f, 0.45f, 24, Color(0xFFFF9500), 1.3f),
            FloatingSymbol("÷", 0.50f, 0.12f, 24, Color(0xFF00E5FF), 1.0f)
        )
    }

    val dynamicTip = when {
        progress.value < 0.25f -> "Initializing Brain Workout..."
        progress.value < 0.50f -> "Calibrating Neural Logic..."
        progress.value < 0.75f -> "Loading 100 Geometric Riddles..."
        progress.value < 0.95f -> "Sharpening Mathematical Patterns..."
        else -> "Ready! Tap to Start"
    }

    // Multi-color colorful gradient background
    val vibrantBackground = Brush.radialGradient(
        colors = listOf(
            Color(0xFF2E0854), // Deep radiant violet
            Color(0xFF0F1744), // Electric navy
            Color(0xFF090D24), // Midnight space
            Color(0xFF040612)  // Deep black
        ),
        center = Offset(500f, 700f),
        radius = 1200f
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(vibrantBackground)
    ) {
        // Background Floating Math Symbols Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            floatingSymbols.forEachIndexed { i, sym ->
                val floatOffset = sin(colorShift * Math.PI * 2 * sym.speed + i).toFloat() * 18f
                val x = sym.initialX * w
                val y = sym.initialY * h + floatOffset

                // Soft background particle circle
                drawCircle(
                    color = sym.color.copy(alpha = 0.18f),
                    radius = 32f,
                    center = Offset(x, y)
                )
            }
        }

        // Floating Text Symbols via Compose
        floatingSymbols.forEachIndexed { i, sym ->
            val floatOffset = sin(colorShift * Math.PI * 2 * sym.speed + i).toFloat() * 18f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = (sym.initialX * 340).dp,
                        top = (sym.initialY * 650 + floatOffset).dp
                    )
            ) {
                Text(
                    text = sym.symbol,
                    color = sym.color.copy(alpha = 0.75f),
                    fontSize = sym.sizeSp.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }

        // Music Sound Toggle (Top Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(
                onClick = onToggleMusic,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1F1B3C).copy(alpha = 0.8f))
                    .border(1.dp, Color(0xFF6B46C1), CircleShape)
                    .testTag("loading_music_toggle")
            ) {
                Icon(
                    imageVector = if (isMusicMuted) Icons.Default.MusicOff else Icons.Default.MusicNote,
                    contentDescription = "Toggle Music",
                    tint = if (isMusicMuted) Color(0xFF888888) else Color(0xFF00FFA3),
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Main Center Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Center: Rotating Glowing Neon Rings & Symbols
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Colorful Neon Gradient Ring
                    Canvas(
                        modifier = Modifier.size(220.dp * pulseGlow)
                    ) {
                        val stroke = 3.5.dp.toPx()
                        val sweep = Brush.sweepGradient(
                            listOf(
                                Color(0xFFFF2A85), // Neon Magenta
                                Color(0xFF00F0FF), // Neon Cyan
                                Color(0xFFFFD700), // Gold
                                Color(0xFF00FFA3), // Emerald
                                Color(0xFFB026FF), // Neon Purple
                                Color(0xFFFF2A85)
                            )
                        )

                        // Segmented outer ring
                        drawArc(
                            brush = sweep,
                            startAngle = rotationOuter,
                            sweepAngle = 290f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )
                    }

                    // Inner Opposite-Rotating Geometric Ring
                    Canvas(
                        modifier = Modifier.size(160.dp)
                    ) {
                        val stroke = 2.dp.toPx()
                        val sweep = Brush.sweepGradient(
                            listOf(
                                Color(0xFF00F0FF),
                                Color(0xFFFFD700),
                                Color(0xFFFF2A85),
                                Color(0xFF00F0FF)
                            )
                        )

                        drawArc(
                            brush = sweep,
                            startAngle = rotationInner,
                            sweepAngle = 240f,
                            useCenter = false,
                            style = Stroke(width = stroke, cap = StrokeCap.Round)
                        )

                        // Rotating geometric triangle inside
                        val w = size.width
                        val h = size.height
                        val cx = w / 2f
                        val cy = h / 2f
                        val rad = Math.toRadians(rotationInner.toDouble())
                        val r = w * 0.35f

                        val p1 = Offset(
                            (cx + r * cos(rad)).toFloat(),
                            (cy + r * sin(rad)).toFloat()
                        )
                        val p2 = Offset(
                            (cx + r * cos(rad + 2.094)).toFloat(),
                            (cy + r * sin(rad + 2.094)).toFloat()
                        )
                        val p3 = Offset(
                            (cx + r * cos(rad + 4.188)).toFloat(),
                            (cy + r * sin(rad + 4.188)).toFloat()
                        )

                        val triPath = Path().apply {
                            moveTo(p1.x, p1.y)
                            lineTo(p2.x, p2.y)
                            lineTo(p3.x, p3.y)
                            close()
                        }

                        drawPath(
                            path = triPath,
                            color = Color(0xFF00F0FF).copy(alpha = 0.6f),
                            style = Stroke(width = stroke)
                        )
                    }

                    // Center glowing Question Mark / Riddle icon
                    Text(
                        text = "?",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraLight,
                        fontFamily = FontFamily.SansSerif
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // App Title with Colorful Shimmering Gradient
                Text(
                    text = "MATH RIDDLES",
                    style = androidx.compose.ui.text.TextStyle(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFFFF2A85),
                                Color(0xFF00F0FF),
                                Color(0xFFFFD700),
                                Color(0xFF00FFA3)
                            )
                        ),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 4.sp,
                        fontFamily = FontFamily.SansSerif
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TRAIN BOTH PARTS OF YOUR BRAIN",
                    color = Color(0xFFD4D4E8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Progress & Status Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Tip message with subtle color
                Text(
                    text = dynamicTip,
                    color = Color(0xFF00F0FF),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center
                )

                // Colorful Neon Progress Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF1E173D))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = progress.value)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFFFF2A85),
                                        Color(0xFF9D00FF),
                                        Color(0xFF00F0FF),
                                        Color(0xFF00FFA3)
                                    )
                                )
                            )
                    )
                }

                // Percentage & Skip Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(progress.value * 100).toInt()}%",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.sp
                    )

                    // Skip / Start Button
                    Text(
                        text = if (progress.value > 0.85f) "START →" else "SKIP",
                        color = Color(0xFFFFD700),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier
                            .clickable {
                                onFinishLoading()
                            }
                            .padding(8.dp)
                            .testTag("skip_loading_button")
                    )
                }
            }
        }
    }
}
