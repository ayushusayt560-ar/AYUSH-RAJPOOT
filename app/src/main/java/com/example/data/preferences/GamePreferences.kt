package com.example.data.preferences

import android.content.Context
import android.content.SharedPreferences

class GamePreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("math_riddles_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_COMPLETED_LEVELS = "completed_levels"
        private const val KEY_CURRENT_LEVEL = "current_level"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_UNLOCKED_HINTS = "unlocked_hints"
        private const val KEY_FREE_HINTS = "free_hints_remaining"
    }

    fun getFreeHintsRemaining(): Int {
        return prefs.getInt(KEY_FREE_HINTS, 2)
    }

    fun setFreeHintsRemaining(count: Int) {
        prefs.edit().putInt(KEY_FREE_HINTS, count.coerceAtLeast(0)).apply()
    }

    fun decrementFreeHints() {
        val current = getFreeHintsRemaining()
        setFreeHintsRemaining(current - 1)
    }

    fun addFreeHints(amount: Int) {
        val current = getFreeHintsRemaining()
        setFreeHintsRemaining(current + amount)
    }

    fun getCompletedLevels(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_COMPLETED_LEVELS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun markLevelCompleted(levelId: Int) {
        val currentSet = getCompletedLevels().toMutableSet()
        currentSet.add(levelId)
        prefs.edit()
            .putStringSet(KEY_COMPLETED_LEVELS, currentSet.map { it.toString() }.toSet())
            .apply()
    }

    fun getCurrentLevel(): Int {
        return prefs.getInt(KEY_CURRENT_LEVEL, 1)
    }

    fun setCurrentLevel(level: Int) {
        prefs.edit().putInt(KEY_CURRENT_LEVEL, level).apply()
    }

    fun isSoundEnabled(): Boolean {
        return prefs.getBoolean(KEY_SOUND_ENABLED, true)
    }

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isHapticsEnabled(): Boolean {
        return prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
    }

    fun setHapticsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, enabled).apply()
    }

    fun getUnlockedHints(): Set<Int> {
        val stringSet = prefs.getStringSet(KEY_UNLOCKED_HINTS, emptySet()) ?: emptySet()
        return stringSet.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun unlockHintForLevel(levelId: Int) {
        val currentSet = getUnlockedHints().toMutableSet()
        currentSet.add(levelId)
        prefs.edit()
            .putStringSet(KEY_UNLOCKED_HINTS, currentSet.map { it.toString() }.toSet())
            .apply()
    }

    fun resetProgress() {
        prefs.edit()
            .remove(KEY_COMPLETED_LEVELS)
            .putInt(KEY_CURRENT_LEVEL, 1)
            .putInt(KEY_FREE_HINTS, 2)
            .remove(KEY_UNLOCKED_HINTS)
            .apply()
    }
}
