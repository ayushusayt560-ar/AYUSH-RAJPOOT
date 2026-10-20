package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AdRewardReason
import kotlinx.coroutines.delay

private data class AdCreative(
    val title: String,
    val subtitle: String,
    val description: String,
    val rating: String,
    val icon: ImageVector,
    val badgeColor: Color,
    val gradientColors: List<Color>
)

private val SAMPLE_CREATIVES = listOf(
    AdCreative(
        title = "BRAIN MASTER IQ",
        subtitle = "Logic Puzzles & Mind Training",
        description = "200+ Math challenges designed to increase concentration and logic skills.",
        rating = "4.9 ★ (1M+ Players)",
        icon = Icons.Default.Psychology,
        badgeColor = Color(0xFF38BDF8),
        gradientColors = listOf(Color(0xFF1E3A8A), Color(0xFF0F172A))
    ),
    AdCreative(
        title = "SPEED MATH HERO",
        subtitle = "Fast Mental Calculation Workout",
        description = "Beat the clock with lightning-fast mental math drills and unlock achievements.",
        rating = "4.8 ★ (750K+ Players)",
        icon = Icons.Default.Bolt,
        badgeColor = Color(0xFFF59E0B),
        gradientColors = listOf(Color(0xFF854D0E), Color(0xFF1E1B4B))
    ),
    AdCreative(
        title = "LOGIC RIDDLE CHAMPION",
        subtitle = "Daily Brain Teasers & Riddles",
        description = "Solve intriguing geometry and pattern riddles every single day.",
        rating = "4.9 ★ (500K+ Players)",
        icon = Icons.Default.Lightbulb,
        badgeColor = Color(0xFF10B981),
        gradientColors = listOf(Color(0xFF065F46), Color(0xFF0F172A))
    )
)

/**
 * High-quality offline-capable Rewarded Video Ad overlay.
 *
 * Ensures players cannot bypass ad requirements by turning off Wi-Fi or mobile data.
 * Even when the user is completely offline, this rich interactive sponsor ad runs with:
 * - Real 10-second non-skippable rewarded timer
 * - Clear "ऑफ़लाइन विज्ञापन • Offline Ad" badge
 * - Engaging rotating app sponsor creative
 * - Safe reward delivery (+1 Hint or +3 Lives Revive)
 */
@Composable
fun RewardedAdOverlay(
    rewardReason: AdRewardReason = AdRewardReason.HINT,
    onRewardGranted: () -> Unit,
    onCloseWithoutReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    val durationSeconds = 10
    var secondsRemaining by remember { mutableIntStateOf(durationSeconds) }
    var isCompleted by remember { mutableStateOf(false) }
    var showExitWarning by remember { mutableStateOf(false) }
    var clickedInstall by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }

    // Pick a sponsor creative for this ad session
    val creative = remember { SAMPLE_CREATIVES.random() }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = durationSeconds * 1000, easing = LinearEasing)
        )
    }

    LaunchedEffect(Unit) {
        while (secondsRemaining > 0) {
            delay(1000)
            secondsRemaining--
        }
        isCompleted = true
        showExitWarning = false
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712).copy(alpha = 0.96f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                )
                .border(1.5.dp, Color(0xFF38BDF8).copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Ad Tag + Offline badge + Timer/Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFF59E0B))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "AD",
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF334155))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "ऑफ़लाइन मोड",
                                color = Color(0xFFE2E8F0),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (isCompleted) {
                    IconButton(
                        onClick = onRewardGranted,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .testTag("ad_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Ad and claim reward",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF475569), CircleShape)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Reward in ${secondsRemaining}s",
                                color = Color(0xFFFBBF24),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Close button with warning before ad finishes
                        IconButton(
                            onClick = { showExitWarning = true },
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF334155))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Skip Ad",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            // Early exit warning alert
            AnimatedVisibility(visible = showExitWarning && !isCompleted) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF450A0A))
                        .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "चेतावनी: ऐड बीच में बंद करने पर रिवॉर्ड नहीं मिलेगा!",
                            color = Color(0xFFFCA5A5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = onCloseWithoutReward,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("बंद करें (Skip)", fontSize = 11.sp)
                            }
                            Button(
                                onClick = { showExitWarning = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                modifier = Modifier.height(32.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text("ऐड देखें (Continue)", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Creative Visual Card (Simulates dynamic rich video banner)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(creative.gradientColors))
                    .border(1.dp, creative.badgeColor.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(creative.badgeColor.copy(alpha = 0.2f))
                            .border(1.dp, creative.badgeColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = creative.icon,
                            contentDescription = null,
                            tint = creative.badgeColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Text(
                        text = creative.title,
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontFamily = FontFamily.SansSerif
                    )

                    Text(
                        text = creative.subtitle,
                        color = Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = creative.rating,
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Short Description
            Text(
                text = creative.description,
                color = Color(0xFF94A3B8),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            // Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LinearProgressIndicator(
                    progress = { progress.value },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8),
                    trackColor = Color(0xFF334155)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isCompleted) "ऐड पूरा हुआ!" else "ऑफ़लाइन स्पॉन्सर विज्ञापन...",
                        color = if (isCompleted) Color(0xFF10B981) else Color(0xFF64748B),
                        fontSize = 11.sp
                    )
                    Text(
                        text = if (isCompleted) "100%" else "${(progress.value * 100).toInt()}%",
                        color = if (isCompleted) Color(0xFF10B981) else Color(0xFF38BDF8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Action Button: Either Claim Reward or Simulated Install
            if (isCompleted) {
                Button(
                    onClick = onRewardGranted,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("claim_reward_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981),
                        contentColor = Color.White
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (rewardReason == AdRewardReason.REVIVE_LIVES) Icons.Default.Favorite else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (rewardReason == AdRewardReason.REVIVE_LIVES) {
                                "रिवॉर्ड लें (+3 LIVES) ❤️"
                            } else {
                                "रिवॉर्ड लें (+1 HINT) 💡"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                // Interactive CTA button for user during ad playback
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (clickedInstall) Color(0xFF10B981) else Color(0xFF2563EB))
                        .clickable { clickedInstall = !clickedInstall }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (clickedInstall) "✓ INTEREST RECORDED" else "INSTALL FREE / डाउनलोड करें",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Text(
                    text = "इंटरनेट बंद होने के बावजूद भी ऐड पूरा देखने पर रिवॉर्ड मिलेगा।",
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
