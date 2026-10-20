package com.example.ui.viewmodel

import android.app.Application
import android.media.AudioManager
import android.media.ToneGenerator
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.PuzzleLevel
import com.example.data.preferences.GamePreferences
import com.example.data.repository.PuzzleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface AppScreen {
    object Loading : AppScreen
    object Home : AppScreen
    data class Gameplay(val levelId: Int) : AppScreen
    object Levels : AppScreen
}

enum class AdRewardReason {
    HINT,
    REVIVE_LIVES
}

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.Loading,
    val currentLevelId: Int = 1,
    val inputText: String = "",
    val completedLevels: Set<Int> = emptySet(),
    val chancesLeft: Int = 3,
    val freeHintsRemaining: Int = 2,
    val showWatchAdDialog: Boolean = false,
    val isWatchingAd: Boolean = false,
    val pendingAdRewardReason: AdRewardReason = AdRewardReason.HINT,
    val showGameOverDialog: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showHintDialog: Boolean = false,
    val isShakeError: Boolean = false,
    val errorMessage: String? = null,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
) {
    val currentPuzzle: PuzzleLevel
        get() = PuzzleRepository.getLevel(currentLevelId) ?: PuzzleRepository.getAllLevels().first()

    val totalLevels: Int
        get() = PuzzleRepository.getTotalLevelsCount()

    val hasNextLevel: Boolean
        get() = currentLevelId < totalLevels
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = GamePreferences(application.applicationContext)

    private val musicManager = com.example.audio.BackgroundMusicManager()

    private val _uiState = MutableStateFlow(
        GameUiState(
            currentLevelId = preferences.getCurrentLevel(),
            completedLevels = preferences.getCompletedLevels(),
            freeHintsRemaining = preferences.getFreeHintsRemaining(),
            soundEnabled = preferences.isSoundEnabled(),
            hapticsEnabled = preferences.isHapticsEnabled()
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
        } catch (_: Exception) {
            toneGenerator = null
        }
        // Start loading theme music
        musicManager.playTheme(
            com.example.audio.MusicTheme.LOADING_ARPEGGIO,
            muted = !preferences.isSoundEnabled()
        )
    }

    override fun onCleared() {
        super.onCleared()
        toneGenerator?.release()
        toneGenerator = null
        musicManager.stop()
    }

    fun onAppResume() {
        if (_uiState.value.soundEnabled) {
            when (_uiState.value.currentScreen) {
                is AppScreen.Loading -> musicManager.playTheme(com.example.audio.MusicTheme.LOADING_ARPEGGIO, muted = false)
                is AppScreen.Home, is AppScreen.Levels -> musicManager.playTheme(com.example.audio.MusicTheme.HOME_AMBIENT, muted = false)
                is AppScreen.Gameplay -> musicManager.stop() // Keep music OFF in gameplay
            }
        }
    }

    fun onAppPause() {
        musicManager.stop()
    }

    fun navigateTo(screen: AppScreen) {
        when (screen) {
            is AppScreen.Home, is AppScreen.Levels -> {
                // Cheerful ambient music on Home and Levels screen
                musicManager.playTheme(
                    com.example.audio.MusicTheme.HOME_AMBIENT,
                    muted = !_uiState.value.soundEnabled
                )
            }
            is AppScreen.Loading -> {
                musicManager.playTheme(
                    com.example.audio.MusicTheme.LOADING_ARPEGGIO,
                    muted = !_uiState.value.soundEnabled
                )
            }
            is AppScreen.Gameplay -> {
                // Turn music OFF when game starts!
                musicManager.stop()
            }
        }

        _uiState.update {
            it.copy(
                currentScreen = screen,
                inputText = "",
                showSuccessDialog = false,
                showHintDialog = false,
                isShakeError = false,
                errorMessage = null
            )
        }
    }

    fun startPlaying() {
        // If highest level is completed, resume next unlocked level
        val completed = _uiState.value.completedLevels
        var targetLevel = 1
        for (i in 1.._uiState.value.totalLevels) {
            if (!completed.contains(i)) {
                targetLevel = i
                break
            }
        }
        selectLevel(targetLevel)
    }

    fun selectLevel(levelId: Int) {
        // Turn music OFF as soon as game/level starts!
        musicManager.stop()
        val bounded = levelId.coerceIn(1, _uiState.value.totalLevels)
        preferences.setCurrentLevel(bounded)
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Gameplay(bounded),
                currentLevelId = bounded,
                inputText = "",
                chancesLeft = 3,
                showGameOverDialog = false,
                showSuccessDialog = false,
                showHintDialog = false,
                isShakeError = false,
                errorMessage = null
            )
        }
    }

    fun onDigitInput(digit: String, view: View?) {
        playKeyFeedback(view)
        val current = _uiState.value.inputText
        if (current.length < 7) {
            _uiState.update { it.copy(inputText = current + digit, errorMessage = null) }
        }
    }

    fun onClearInput(view: View?) {
        playKeyFeedback(view)
        _uiState.update { it.copy(inputText = "", errorMessage = null) }
    }

    fun onSubmitAnswer(view: View?) {
        val state = _uiState.value
        val puzzle = state.currentPuzzle
        val entered = state.inputText.trim()

        if (entered.isEmpty()) return

        if (entered == puzzle.answer.trim()) {
            // Correct Answer / Level Won!
            preferences.markLevelCompleted(puzzle.id)
            val updatedCompleted = preferences.getCompletedLevels()

            // Play distinct celebratory victory music!
            if (_uiState.value.soundEnabled) {
                musicManager.playVictoryJingle()
            }
            if (_uiState.value.hapticsEnabled) {
                view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
            }

            _uiState.update {
                it.copy(
                    completedLevels = updatedCompleted,
                    showSuccessDialog = true,
                    chancesLeft = 3, // Reset chances on success
                    errorMessage = null
                )
            }
        } else {
            // Wrong Answer!
            val newChances = (_uiState.value.chancesLeft - 1).coerceAtLeast(0)
            playErrorFeedback(view)

            if (newChances <= 0) {
                // Out of chances! 3 wrong answers -> Game Over, must restart from beginning
                _uiState.update {
                    it.copy(
                        chancesLeft = 0,
                        showGameOverDialog = true,
                        errorMessage = "OUT OF CHANCES! (3 WRONG ANSWERS)"
                    )
                }
            } else {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(
                            chancesLeft = newChances,
                            isShakeError = true,
                            errorMessage = "WRONG ANSWER! $newChances CHANCE${if (newChances > 1) "S" else ""} LEFT"
                        )
                    }
                    delay(600)
                    _uiState.update { it.copy(isShakeError = false) }
                }
            }
        }
    }

    fun onRestartFromBeginning() {
        musicManager.stop()
        preferences.setCurrentLevel(1)
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Gameplay(1),
                currentLevelId = 1,
                inputText = "",
                chancesLeft = 3,
                showGameOverDialog = false,
                showSuccessDialog = false,
                showHintDialog = false,
                isShakeError = false,
                errorMessage = null
            )
        }
    }

    fun onGameOverExitToHome() {
        musicManager.stop()
        _uiState.update {
            it.copy(
                currentScreen = AppScreen.Home,
                currentLevelId = 1,
                inputText = "",
                chancesLeft = 3,
                showGameOverDialog = false,
                showSuccessDialog = false,
                showHintDialog = false,
                isShakeError = false,
                errorMessage = null
            )
        }
    }

    fun onNextLevel() {
        musicManager.stop()
        val nextId = _uiState.value.currentLevelId + 1
        if (nextId <= _uiState.value.totalLevels) {
            selectLevel(nextId)
        } else {
            navigateTo(AppScreen.Levels)
        }
    }

    fun onOpenHint() {
        val levelId = _uiState.value.currentLevelId
        val unlockedHints = preferences.getUnlockedHints()

        if (unlockedHints.contains(levelId)) {
            // Already unlocked for this level, open hint directly
            _uiState.update { it.copy(showHintDialog = true) }
        } else {
            val remaining = preferences.getFreeHintsRemaining()
            if (remaining > 0) {
                // Consume 1 free hint
                preferences.decrementFreeHints()
                preferences.unlockHintForLevel(levelId)
                val newRemaining = preferences.getFreeHintsRemaining()
                _uiState.update {
                    it.copy(
                        freeHintsRemaining = newRemaining,
                        showHintDialog = true
                    )
                }
            } else {
                // Free hints exhausted! Prompt to watch a rewarded video ad
                _uiState.update {
                    it.copy(
                        pendingAdRewardReason = AdRewardReason.HINT,
                        showWatchAdDialog = true
                    )
                }
            }
        }
    }

    fun onReviveWithAdRequested() {
        _uiState.update {
            it.copy(
                pendingAdRewardReason = AdRewardReason.REVIVE_LIVES,
                showGameOverDialog = false
            )
        }
    }

    fun onStartWatchingAd() {
        _uiState.update {
            it.copy(
                showWatchAdDialog = false,
                isWatchingAd = true
            )
        }
    }

    fun onAdRewardGranted() {
        val currentReason = _uiState.value.pendingAdRewardReason
        if (_uiState.value.soundEnabled) {
            playSuccessFeedback(null)
        }
        if (currentReason == AdRewardReason.HINT) {
            val levelId = _uiState.value.currentLevelId
            preferences.unlockHintForLevel(levelId)
            _uiState.update {
                it.copy(
                    isWatchingAd = false,
                    showHintDialog = true
                )
            }
        } else {
            // Revive with full 3 lives and stay on current level
            _uiState.update {
                it.copy(
                    chancesLeft = 3,
                    isWatchingAd = false,
                    showGameOverDialog = false,
                    errorMessage = null,
                    isShakeError = false
                )
            }
        }
    }

    fun onDismissWatchAdDialog() {
        _uiState.update { it.copy(showWatchAdDialog = false) }
    }

    fun onCancelWatchingAd() {
        val currentReason = _uiState.value.pendingAdRewardReason
        _uiState.update {
            it.copy(
                isWatchingAd = false,
                showGameOverDialog = (currentReason == AdRewardReason.REVIVE_LIVES)
            )
        }
    }

    fun onCloseHint() {
        _uiState.update { it.copy(showHintDialog = false) }
    }

    fun onDismissSuccessDialog() {
        musicManager.stop()
        _uiState.update { it.copy(showSuccessDialog = false) }
    }

    fun toggleSound() {
        val newValue = !_uiState.value.soundEnabled
        preferences.setSoundEnabled(newValue)
        musicManager.setMuted(!newValue)
        if (newValue) {
            when (_uiState.value.currentScreen) {
                is AppScreen.Loading -> musicManager.playTheme(com.example.audio.MusicTheme.LOADING_ARPEGGIO, muted = false)
                is AppScreen.Home, is AppScreen.Levels -> musicManager.playTheme(com.example.audio.MusicTheme.HOME_AMBIENT, muted = false)
                is AppScreen.Gameplay -> musicManager.stop() // Keep music OFF during gameplay
            }
        } else {
            musicManager.stop()
        }
        _uiState.update { it.copy(soundEnabled = newValue) }
    }

    fun toggleHaptics() {
        val newValue = !_uiState.value.hapticsEnabled
        preferences.setHapticsEnabled(newValue)
        _uiState.update { it.copy(hapticsEnabled = newValue) }
    }

    fun resetAllProgress() {
        preferences.resetProgress()
        _uiState.update {
            it.copy(
                completedLevels = emptySet(),
                currentLevelId = 1,
                inputText = "",
                chancesLeft = 3,
                freeHintsRemaining = 2,
                showGameOverDialog = false,
                showSuccessDialog = false,
                showHintDialog = false,
                showWatchAdDialog = false,
                isWatchingAd = false
            )
        }
    }

    private fun playKeyFeedback(view: View?) {
        if (_uiState.value.hapticsEnabled) {
            view?.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
        if (_uiState.value.soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
            } catch (_: Exception) {}
        }
    }

    private fun playSuccessFeedback(view: View?) {
        if (_uiState.value.hapticsEnabled) {
            view?.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        }
        if (_uiState.value.soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 120)
            } catch (_: Exception) {}
        }
    }

    private fun playErrorFeedback(view: View?) {
        if (_uiState.value.hapticsEnabled) {
            view?.performHapticFeedback(HapticFeedbackConstants.REJECT)
        }
        if (_uiState.value.soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 120)
            } catch (_: Exception) {}
        }
    }
}
