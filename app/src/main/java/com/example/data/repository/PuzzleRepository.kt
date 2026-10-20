package com.example.data.repository

import com.example.data.model.InvertedTriangle
import com.example.data.model.PuzzleLevel
import com.example.data.model.PuzzleType
import com.example.data.model.SpokeWheelData

object PuzzleRepository {

    private val levelsList: List<PuzzleLevel> by lazy {
        loadLevels()
    }

    fun getAllLevels(): List<PuzzleLevel> = levelsList

    fun getLevel(id: Int): PuzzleLevel? {
        return levelsList.find { it.id == id } ?: levelsList.firstOrNull()
    }

    fun getTotalLevelsCount(): Int = levelsList.size

    private fun loadLevels(): List<PuzzleLevel> {
        val list = mutableListOf<PuzzleLevel>()

        // Helper functions
        fun eq(id: Int, lines: List<String>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.EQUATION,
                equationLines = lines,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun triangles(id: Int, items: List<InvertedTriangle>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.INVERTED_TRIANGLES,
                trianglesData = items,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun grid(id: Int, small: Int, target: Int, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.GRID_COUNT,
                gridCountData = Pair(small, target),
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun wheel(id: Int, outers: List<String>, inners: List<Int>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.SPOKE_WHEEL,
                spokeWheelData = SpokeWheelData(outerNumbers = outers, innerNumbers = inners),
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun matrix(id: Int, rows: List<List<String>>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.NUMBER_MATRIX,
                matrixData = rows,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun seq(id: Int, items: List<String>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.SEQUENCE,
                sequenceData = items,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun triVertex(id: Int, vertices: List<String>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.NUMBER_TRIANGLE,
                triangleVertexData = vertices,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        fun circleSec(id: Int, sectors: List<String>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.CIRCLE_SECTORS,
                circleSectors = sectors,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        // Level 1 to 10
        list.add(
            eq(
                id = 1,
                lines = listOf("2 + 2 × 2 = ?"),
                ans = "6",
                hint = "Remember the order of operations: multiplication precedes addition.",
                expl = "Multiply first: 2 × 2 = 4, then add 2: 2 + 4 = 6."
            )
        )
        list.add(
            seq(
                id = 2,
                items = listOf("4", "8", "16", "32", "?"),
                ans = "64",
                hint = "Look at how each number grows relative to the previous one.",
                expl = "Each number is doubled: 32 × 2 = 64."
            )
        )
        list.add(
            eq(
                id = 3,
                lines = listOf("1 = 4", "2 = 8", "3 = 12", "4 = ?"),
                ans = "16",
                hint = "Each input is multiplied by 4.",
                expl = "4 × 4 = 16 (or since 1 = 4, 4 = 1 is also a classic riddle, but mathematically 4 × 4 = 16)."
            )
        )
        list.add(
            eq(
                id = 4,
                lines = listOf("3 + 3 × 3 - 3 = ?"),
                ans = "9",
                hint = "Calculate multiplication before addition and subtraction.",
                expl = "3 × 3 = 9. Then 3 + 9 - 3 = 9."
            )
        )
        list.add(
            matrix(
                id = 5,
                rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("2", "4", "6"),
                    listOf("3", "6", "?")
                ),
                ans = "9",
                hint = "Examine the relationship between rows and columns (multiplication table).",
                expl = "Row 3 column 3 is 3 × 3 = 9."
            )
        )
        list.add(
            eq(
                id = 6,
                lines = listOf("1 + 4 = 5", "2 + 5 = 12", "3 + 6 = 21", "8 + 11 = ?"),
                ans = "96",
                hint = "Formula: a + (a × b) or adding previous sum.",
                expl = "8 + (8 × 11) = 8 + 88 = 96 (or 40 if chaining previous row, but standard formula is a + a×b = 96)."
            )
        )
        list.add(
            circleSec(
                id = 7,
                sectors = listOf("2", "4", "8", "?"),
                ans = "16",
                hint = "Powers of 2 moving clockwise.",
                expl = "2¹ = 2, 2² = 4, 2³ = 8, 2⁴ = 16."
            )
        )
        // Level 8 (From Screenshot 4!)
        list.add(
            eq(
                id = 8,
                lines = listOf("A + B = 60", "A - B = 40", "A / B = ?"),
                ans = "5",
                hint = "Add the two equations together to solve for A first.",
                expl = "Adding both gives 2A = 100, so A = 50. Then B = 10. Therefore A / B = 50 / 10 = 5."
            )
        )
        list.add(
            triVertex(
                id = 9,
                vertices = listOf("3", "4", "5", "12"), // top, left, right, center
                ans = "18",
                hint = "The center is the sum of the three vertex numbers.",
                expl = "Vertices 5, 6, 7 give 5 + 6 + 7 = 18."
            )
        )
        list.add(
            eq(
                id = 10,
                lines = listOf("6 ( 30 ) 5", "7 ( 42 ) 6", "8 ( ? ) 7"),
                ans = "56",
                hint = "Multiply the outer numbers to get the number inside parentheses.",
                expl = "8 × 7 = 56."
            )
        )

        // Level 11 to 20
        list.add(
            seq(
                id = 11,
                items = listOf("1", "1", "2", "3", "5", "8", "13", "?"),
                ans = "21",
                hint = "Each number is the sum of the two preceding numbers.",
                expl = "Fibonacci sequence: 8 + 13 = 21."
            )
        )
        list.add(
            eq(
                id = 12,
                lines = listOf("3 -> 18", "4 -> 32", "5 -> 50", "6 -> ?"),
                ans = "72",
                hint = "Notice n × (2 × n) or 2 × n².",
                expl = "For 6: 2 × 6² = 2 × 36 = 72."
            )
        )
        list.add(
            seq(
                id = 13,
                items = listOf("1", "3", "7", "15", "31", "?"),
                ans = "63",
                hint = "Each term is (Previous × 2) + 1.",
                expl = "31 × 2 + 1 = 63."
            )
        )
        list.add(
            eq(
                id = 14,
                lines = listOf("7 + 7 ÷ 7 + 7 × 7 - 7 = ?"),
                ans = "50",
                hint = "Division and multiplication first: 7/7 = 1, 7×7 = 49.",
                expl = "7 + 1 + 49 - 7 = 50."
            )
        )
        list.add(
            matrix(
                id = 15,
                rows = listOf(
                    listOf("2", "3", "13"),
                    listOf("4", "1", "17"),
                    listOf("5", "2", "?")
                ),
                ans = "29",
                hint = "Square the first two numbers and add them together.",
                expl = "5² + 2² = 25 + 4 = 29."
            )
        )
        list.add(
            eq(
                id = 16,
                lines = listOf("11 × 11 = 4", "22 × 22 = 16", "33 × 33 = ?"),
                ans = "36",
                hint = "Sum the digits of each factor before multiplying.",
                expl = "(3 + 3) × (3 + 3) = 6 × 6 = 36."
            )
        )
        // Level 17 (From Screenshot 2!)
        list.add(
            triangles(
                id = 17,
                items = listOf(
                    InvertedTriangle(topLeft = "5", topRight = "10", bottom = "2"),
                    InvertedTriangle(topLeft = "4", topRight = "32", bottom = "8"),
                    InvertedTriangle(topLeft = "4", topRight = "20", bottom = "?")
                ),
                ans = "5",
                hint = "In each triangle, Left Number × Bottom Number = Right Number.",
                expl = "5 × 2 = 10, 4 × 8 = 32. For the third triangle: 4 × ? = 20, therefore ? = 5."
            )
        )
        list.add(
            eq(
                id = 18,
                lines = listOf("1 = 5", "2 = 25", "3 = 125", "4 = 625", "5 = ?"),
                ans = "1",
                hint = "Look at the very first line of the puzzle carefully.",
                expl = "If 1 = 5, then 5 = 1!"
            )
        )
        list.add(
            triVertex(
                id = 19,
                vertices = listOf("2", "4", "6", "48"),
                ans = "120",
                hint = "Multiply the three vertices together to find the center.",
                expl = "Vertices 4, 5, 6 give 4 × 5 × 6 = 120."
            )
        )
        list.add(
            seq(
                id = 20,
                items = listOf("2", "3", "5", "7", "11", "13", "?"),
                ans = "17",
                hint = "These are prime numbers in ascending order.",
                expl = "The next prime number after 13 is 17."
            )
        )

        // Level 21 (From Screenshot 1!)
        list.add(
            grid(
                id = 21,
                small = 2, // 2x2 = 5
                target = 3, // 3x3 = ?
                ans = "14",
                hint = "Count all possible squares of any size (1x1, 2x2, 3x3).",
                expl = "In 2x2: 4 (1x1) + 1 (2x2) = 5. In 3x3: 9 (1x1) + 4 (2x2) + 1 (3x3) = 14 total squares."
            )
        )
        list.add(
            eq(
                id = 22,
                lines = listOf("9 = 90", "8 = 72", "7 = 56", "6 = 42", "3 = ?"),
                ans = "12",
                hint = "Each number n is multiplied by (n + 1).",
                expl = "9×10=90, 8×9=72, 7×8=56, 6×7=42, so 3×4 = 12."
            )
        )
        list.add(
            eq(
                id = 23,
                lines = listOf("2 + 3 = 10", "7 + 2 = 63", "6 + 5 = 66", "8 + 4 = 96", "9 + 7 = ?"),
                ans = "144",
                hint = "Multiply the first number by the sum of both numbers: a × (a + b).",
                expl = "9 × (9 + 7) = 9 × 16 = 144."
            )
        )
        list.add(
            circleSec(
                id = 24,
                sectors = listOf("3", "9", "4", "16", "5", "25", "7", "?"),
                ans = "49",
                hint = "Look at the number paired across or next to it: each is n².",
                expl = "7² = 49."
            )
        )
        list.add(
            eq(
                id = 25,
                lines = listOf("A × B = 24", "B × C = 32", "A × C = 48", "A × B × C = ?"),
                ans = "192",
                hint = "Multiply all three equations: (A × B × C)² = 24 × 32 × 48.",
                expl = "(A × B × C)² = 36,864. The square root of 36,864 is 192."
            )
        )
        list.add(
            triVertex(
                id = 26,
                vertices = listOf("7", "8", "9", "24"),
                ans = "30",
                hint = "Center is the sum of all 3 vertices.",
                expl = "Triangle with vertices 9, 10, 11 gives 9 + 10 + 11 = 30."
            )
        )

        // Level 27 (From Screenshot 5!)
        list.add(
            wheel(
                id = 27,
                outers = listOf("20", "16", "?", "15", "17", "7", "4", "10"),
                inners = listOf(9, 11, 5, 2, 13, 4, 3, 1),
                ans = "7",
                hint = "Each outer number is the sum of the two inner numbers flanking it.",
                expl = "The spoke with '?' is flanked by inner numbers 5 and 2: 5 + 2 = 7."
            )
        )

        // Levels 28 - 100 loaded from supplemental definitions
        list.addAll(PuzzleLevelsExtended.getLevels28To100())

        return list
    }
}
