package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HintDialog
import com.example.ui.components.KeypadView
import com.example.ui.components.PuzzleCardView
import com.example.ui.components.RewardedAdOverlay
import com.example.ui.components.SuccessDialog
import com.example.ui.components.WatchAdPromptDialog
import com.example.ui.viewmodel.GameUiState
import kotlin.math.roundToInt

@Composable
fun GameplayScreen(
    uiState: GameUiState,
    onBack: () -> Unit,
    onDigitInput: (String, android.view.View?) -> Unit,
    onClearInput: (android.view.View?) -> Unit,
    onSubmitAnswer: (android.view.View?) -> Unit,
    onOpenHint: () -> Unit,
    onCloseHint: () -> Unit,
    onNextLevel: () -> Unit,
    onPreviousLevel: () -> Unit,
    onViewLevels: () -> Unit,
    onDismissSuccessDialog: () -> Unit,
    onToggleSound: () -> Unit,
    onRestartFromBeginning: () -> Unit,
    onExitToHome: () -> Unit,
    onReviveWithAd: () -> Unit,
    onStartWatchingAd: () -> Unit,
    onDismissWatchAdDialog: () -> Unit,
    onAdRewardGranted: () -> Unit,
    onCancelWatchingAd: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentView = LocalView.current
    val shakeOffset = remember { Animatable(0f) }

    BackHandler {
        onBack()
    }

    LaunchedEffect(uiState.isShakeError) {
        if (uiState.isShakeError) {
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 400
                    0f at 0
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    10f at 300
                    -5f at 350
                    0f at 400
                }
            )
        }
    }

    val cosmicBackground = Brush.verticalGradient(
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
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = "Level ${uiState.currentLevelId}",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraLight,
                        fontFamily = FontFamily.SansSerif,
                        modifier = Modifier.testTag("level_title")
                    )
                }

                // Header right: Chances / Lives indicator & Sound Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 3 Chances Hearts Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF1E293B))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("chances_indicator")
                    ) {
                        for (i in 1..3) {
                            val isAlive = i <= uiState.chancesLeft
                            Icon(
                                imageVector = if (isAlive) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Chance $i",
                                tint = if (isAlive) Color(0xFFEF4444) else Color(0xFF475569),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onToggleSound,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (uiState.soundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "Toggle Sound",
                            tint = if (uiState.soundEnabled) Color(0xFFAAAAAA) else Color(0xFF555555),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Main Puzzle View Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PuzzleCardView(
                        puzzle = uiState.currentPuzzle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(340.dp)
                    )

                    // Error text if wrong answer
                    if (uiState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = uiState.errorMessage,
                            color = Color(0xFFEF4444),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.SansSerif
                        )
                    }
                }
            }

            // Bottom Keypad
            KeypadView(
                inputText = uiState.inputText,
                freeHintsRemaining = uiState.freeHintsRemaining,
                onDigitClick = { digit -> onDigitInput(digit, currentView) },
                onClearClick = { onClearInput(currentView) },
                onHintClick = onOpenHint,
                onEnterClick = { onSubmitAnswer(currentView) }
            )
        }

        // Hint Dialog
        if (uiState.showHintDialog) {
            HintDialog(
                puzzle = uiState.currentPuzzle,
                onDismiss = onCloseHint
            )
        }

        // Watch Ad Prompt Dialog (When free hints are exhausted)
        if (uiState.showWatchAdDialog) {
            WatchAdPromptDialog(
                onWatchAd = onStartWatchingAd,
                onDismiss = onDismissWatchAdDialog
            )
        }

        // Success Dialog
        if (uiState.showSuccessDialog) {
            SuccessDialog(
                puzzle = uiState.currentPuzzle,
                hasNextLevel = uiState.hasNextLevel,
                onNextLevel = onNextLevel,
                onViewLevels = onViewLevels,
                onDismiss = onDismissSuccessDialog
            )
        }

        // Game Over Dialog (When 3 wrong answers are given)
        if (uiState.showGameOverDialog) {
            GameOverDialog(
                currentLevel = uiState.currentLevelId,
                onReviveWithAd = onReviveWithAd,
                onRestartFromBeginning = onRestartFromBeginning,
                onExitToHome = onExitToHome
            )
        }

        // Rewarded Ad Overlay (Interactive ad video countdown)
        if (uiState.isWatchingAd) {
            RewardedAdOverlay(
                rewardReason = uiState.pendingAdRewardReason,
                onRewardGranted = onAdRewardGranted,
                onCloseWithoutReward = onCancelWatchingAd
            )
        }
    }
}
