package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GameUiState

@Composable
fun HomeScreen(
    uiState: GameUiState,
    onPlay: () -> Unit,
    onViewLevels: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleHaptics: () -> Unit,
    onResetProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    val cosmicBackground = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(
            Color(0xFF0F172A), // Slate 900
            Color(0xFF131D3B), // Midnight Blue
            Color(0xFF1E1B4B)  // Deep Indigo
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(cosmicBackground)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top branding & subtle geometric emblem
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 28.dp)
            ) {
                // Minimalist geometric math logo
                Canvas(modifier = Modifier.size(72.dp)) {
                    val stroke = 2.dp.toPx()
                    val w = size.width
                    val h = size.height

                    // Triangle
                    val triPath = Path().apply {
                        moveTo(w * 0.25f, h * 0.7f)
                        lineTo(w * 0.45f, h * 0.3f)
                        lineTo(w * 0.65f, h * 0.7f)
                        close()
                    }
                    drawPath(triPath, color = Color(0xFF38BDF8), style = Stroke(width = stroke))

                    // Square overlay
                    drawRect(
                        color = Color(0xFF818CF8).copy(alpha = 0.85f),
                        topLeft = Offset(w * 0.45f, h * 0.4f),
                        size = androidx.compose.ui.geometry.Size(w * 0.35f, h * 0.35f),
                        style = Stroke(width = stroke)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "MATH RIDDLES",
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraLight,
                    letterSpacing = 4.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "TRAIN BOTH PARTS OF YOUR BRAIN",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Progress Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PROGRESS",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "${uiState.completedLevels.size} / ${uiState.totalLevels} Solved",
                            color = Color(0xFF38BDF8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    val progressFraction =
                        uiState.completedLevels.size.toFloat() / uiState.totalLevels.coerceAtLeast(1)

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = Color(0xFF38BDF8),
                        trackColor = Color(0xFF334155)
                    )
                }
            }

            // Primary Navigation Buttons
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Play Button
                Button(
                    onClick = onPlay,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("play_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1E293B),
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.6f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAY",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Light,
                            letterSpacing = 3.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }

                // Levels Button
                OutlinedButton(
                    onClick = onViewLevels,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("levels_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Text(
                        text = "LEVELS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Light,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.SansSerif
                    )
                }
            }

            // Bottom Settings Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Sound & Music toggle with badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1E1E))
                            .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(8.dp))
                            .clickable(onClick = onToggleSound)
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                            .testTag("home_music_toggle"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Sound and Music",
                            tint = if (uiState.soundEnabled) Color(0xFF00FFA3) else Color(0xFF666666),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = if (uiState.soundEnabled) "Music ON" else "Music OFF",
                            color = if (uiState.soundEnabled) Color.White else Color(0xFF777777),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Haptics toggle
                    IconButton(
                        onClick = onToggleHaptics,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E1E1E))
                            .border(1.dp, Color(0xFF2E2E2E), RoundedCornerShape(8.dp))
                            .testTag("home_haptics_toggle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Vibration",
                            tint = if (uiState.hapticsEnabled) Color(0xFFCCCCCC) else Color(0xFF555555),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Reset Progress option
                TextButton(
                    onClick = { showResetConfirm = true }
                ) {
                    Text(
                        text = "Reset Progress",
                        color = Color(0xFF777777),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetConfirm) {
            AlertDialog(
                onDismissRequest = { showResetConfirm = false },
                containerColor = Color(0xFF202020),
                title = {
                    Text(
                        text = "Reset Progress?",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                },
                text = {
                    Text(
                        text = "Are you sure you want to reset all solved levels and progress? This cannot be undone.",
                        color = Color(0xFFB0B0B0),
                        fontSize = 14.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onResetProgress()
                            showResetConfirm = false
                        }
                    ) {
                        Text("Reset", color = Color(0xFFFF453A))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showResetConfirm = false }
                    ) {
                        Text("Cancel", color = Color.White)
                    }
                }
            )
        }
    }
}
