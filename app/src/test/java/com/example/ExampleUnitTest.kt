package com.example

import com.example.data.repository.PuzzleRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun repository_loadsAllLevels() {
        val levels = PuzzleRepository.getAllLevels()
        assertEquals(100, levels.size)
    }

    @Test
    fun screenshotLevels_haveExactAnswers() {
        // Screenshot 4: Level 8 (A + B = 60, A - B = 40, A / B = ?)
        val level8 = PuzzleRepository.getLevel(8)
        assertNotNull(level8)
        assertEquals("5", level8?.answer)

        // Screenshot 2: Level 17 (Inverted Triangles)
        val level17 = PuzzleRepository.getLevel(17)
        assertNotNull(level17)
        assertEquals("5", level17?.answer)

        // Screenshot 1: Level 21 (Grid count 2x2=5, 3x3=?)
        val level21 = PuzzleRepository.getLevel(21)
        assertNotNull(level21)
        assertEquals("14", level21?.answer)

        // Screenshot 5: Level 27 (Spoke wheel)
        val level27 = PuzzleRepository.getLevel(27)
        assertNotNull(level27)
        assertEquals("7", level27?.answer)
    }

    @Test
    fun allPuzzles_haveValidData() {
        val levels = PuzzleRepository.getAllLevels()
        for (puzzle in levels) {
            assertTrue("Level ${puzzle.id} has empty answer", puzzle.answer.isNotBlank())
            assertTrue("Level ${puzzle.id} has empty hint", puzzle.hint.isNotBlank())
            assertTrue("Level ${puzzle.id} has empty explanation", puzzle.explanation.isNotBlank())
        }
    }

    @Test
    fun appScreen_initialStateIsLoading() {
        val state = com.example.ui.viewmodel.GameUiState()
        assertEquals(com.example.ui.viewmodel.AppScreen.Loading, state.currentScreen)
        assertEquals(3, state.chancesLeft)
        assertFalse(state.showGameOverDialog)
    }
}
