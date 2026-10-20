package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ads.AdMobManager
import com.example.ui.screens.GameplayScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LevelsScreen
import com.example.ui.screens.LoadingScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: GameViewModel by viewModels()
    private lateinit var adMobManager: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adMobManager = AdMobManager(this)

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0F172A))
                        .safeDrawingPadding(),
                    color = Color(0xFF0F172A)
                ) {
                    MathRiddlesApp(
                        viewModel = viewModel,
                        onShowRewardedAd = { onRewardGranted ->
                            viewModel.onDismissWatchAdDialog()
                            if (adMobManager.isAdReady) {
                                adMobManager.showRewardedAd(
                                    activity = this@MainActivity,
                                    onRewardEarned = { _, _ -> onRewardGranted() },
                                    onAdDismissed = { },
                                    onAdFailedToShow = {
                                        // Gracefully fallback to interactive offline ad overlay
                                        viewModel.onStartWatchingAd()
                                    }
                                )
                            } else {
                                // If ad is loading, offline, or network disabled, run rich in-app offline ad system
                                viewModel.onStartWatchingAd()
                            }
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onAppResume()
    }

    override fun onPause() {
        super.onPause()
        viewModel.onAppPause()
    }
}

@Composable
fun MathRiddlesApp(
    viewModel: GameViewModel,
    onShowRewardedAd: (onRewardGranted: () -> Unit) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when (val screen = uiState.currentScreen) {
        is AppScreen.Loading -> {
            LoadingScreen(
                isMusicMuted = !uiState.soundEnabled,
                onToggleMusic = { viewModel.toggleSound() },
                onFinishLoading = { viewModel.navigateTo(AppScreen.Home) }
            )
        }
        is AppScreen.Home -> {
            HomeScreen(
                uiState = uiState,
                onPlay = { viewModel.startPlaying() },
                onViewLevels = { viewModel.navigateTo(AppScreen.Levels) },
                onToggleSound = { viewModel.toggleSound() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onResetProgress = { viewModel.resetAllProgress() }
            )
        }
        is AppScreen.Levels -> {
            LevelsScreen(
                uiState = uiState,
                onBack = { viewModel.navigateTo(AppScreen.Home) },
                onSelectLevel = { levelId -> viewModel.selectLevel(levelId) }
            )
        }
        is AppScreen.Gameplay -> {
            GameplayScreen(
                uiState = uiState,
                onBack = { viewModel.navigateTo(AppScreen.Levels) },
                onDigitInput = { digit, view -> viewModel.onDigitInput(digit, view) },
                onClearInput = { view -> viewModel.onClearInput(view) },
                onSubmitAnswer = { view -> viewModel.onSubmitAnswer(view) },
                onOpenHint = { viewModel.onOpenHint() },
                onCloseHint = { viewModel.onCloseHint() },
                onNextLevel = { viewModel.onNextLevel() },
                onPreviousLevel = {
                    if (uiState.currentLevelId > 1) {
                        viewModel.selectLevel(uiState.currentLevelId - 1)
                    }
                },
                onViewLevels = { viewModel.navigateTo(AppScreen.Levels) },
                onDismissSuccessDialog = { viewModel.onDismissSuccessDialog() },
                onToggleSound = { viewModel.toggleSound() },
                onRestartFromBeginning = { viewModel.onRestartFromBeginning() },
                onExitToHome = { viewModel.onGameOverExitToHome() },
                onReviveWithAd = {
                    viewModel.onReviveWithAdRequested()
                    onShowRewardedAd {
                        viewModel.onAdRewardGranted()
                    }
                },
                onStartWatchingAd = {
                    onShowRewardedAd {
                        viewModel.onAdRewardGranted()
                    }
                },
                onDismissWatchAdDialog = { viewModel.onDismissWatchAdDialog() },
                onAdRewardGranted = { viewModel.onAdRewardGranted() },
                onCancelWatchingAd = { viewModel.onCancelWatchingAd() }
            )
        }
    }
}
