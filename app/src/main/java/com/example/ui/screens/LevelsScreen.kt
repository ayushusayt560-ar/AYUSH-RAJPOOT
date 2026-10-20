package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.GameUiState

@Composable
fun LevelsScreen(
    uiState: GameUiState,
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    // Determine highest unlocked level: completed levels + 1 (or allow full access / browsing)
    val maxUnlocked = (uiState.completedLevels.maxOrNull() ?: 0) + 1

    val cosmicBackground = androidx.compose.ui.graphics.Brush.verticalGradient(
        listOf(
            Color(0xFF0F172A), // Slate 900
            Color(0xFF131D3B), // Midnight Blue
            Color(0xFF1E1B4B)  // Deep Indigo
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(cosmicBackground)
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("levels_back_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "LEVELS",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Light,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.SansSerif
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${uiState.completedLevels.size} / ${uiState.totalLevels}",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                fontWeight = FontWeight.Light
            )
        }

        // 5-Column Grid exactly like Screenshot 3
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .testTag("levels_grid")
        ) {
            items(uiState.totalLevels) { index ->
                val levelNum = index + 1
                val isCompleted = uiState.completedLevels.contains(levelNum)
                val isCurrent = levelNum == uiState.currentLevelId
                val isUnlocked = levelNum <= maxUnlocked

                LevelGridItem(
                    levelNumber = levelNum,
                    isCompleted = isCompleted,
                    isCurrent = isCurrent,
                    isUnlocked = isUnlocked,
                    onClick = {
                        if (isUnlocked) {
                            onSelectLevel(levelNum)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun LevelGridItem(
    levelNumber: Int,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    val bgColor = when {
        isCurrent -> Color(0xFF2563EB)
        isCompleted -> Color(0xFF1E293B)
        isUnlocked -> Color(0xFF1E293B)
        else -> Color(0xFF0F172A)
    }

    val borderColor = when {
        isCurrent -> Color.White
        isCompleted -> Color(0xFF38BDF8).copy(alpha = 0.6f)
        isUnlocked -> Color(0xFF334155)
        else -> Color(0xFF1E293B)
    }

    val textColor = when {
        isCurrent -> Color.White
        isCompleted -> Color(0xFF38BDF8)
        isUnlocked -> Color(0xFFF1F5F9)
        else -> Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(
                enabled = isUnlocked,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Color.White.copy(alpha = 0.2f)),
                onClick = onClick
            )
            .testTag("level_cell_$levelNumber"),
        contentAlignment = Alignment.Center
    ) {
        if (!isUnlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = Color(0xFF444444),
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                text = "$levelNumber",
                color = textColor,
                fontSize = 18.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Normal else FontWeight.Light,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
