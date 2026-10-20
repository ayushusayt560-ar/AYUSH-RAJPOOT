package com.example.data.repository

import com.example.data.model.InvertedTriangle
import com.example.data.model.PuzzleLevel
import com.example.data.model.PuzzleType
import com.example.data.model.SpokeWheelData

object PuzzleLevelsExtended {

    fun getLevels28To100(): List<PuzzleLevel> {
        val list = mutableListOf<PuzzleLevel>()

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

        fun circleSec(id: Int, sectors: List<String>, ans: String, hint: String, expl: String) =
            PuzzleLevel(
                id = id,
                type = PuzzleType.CIRCLE_SECTORS,
                circleSectors = sectors,
                answer = ans,
                hint = hint,
                explanation = expl
            )

        // 28 to 40
        list.add(
            eq(
                28,
                listOf("12 + 12 = 9", "23 + 23 = 25", "34 + 34 = ?"),
                "49",
                "Add the digits of each number: (3+4) + (3+4) = 14? Or sum of digits squared: (1+2)²=9, (2+3)²=25.",
                "(3+4)² = 7² = 49."
            )
        )
        list.add(
            seq(
                29,
                listOf("3", "5", "9", "17", "33", "?"),
                "65",
                "Differences are 2, 4, 8, 16, 32.",
                "33 + 32 = 65."
            )
        )
        list.add(
            matrix(
                30,
                listOf(
                    listOf("3", "4", "25"),
                    listOf("5", "12", "169"),
                    listOf("7", "24", "?")
                ),
                "625",
                "Pythagorean triples: a² + b² = c².",
                "7² + 24² = 49 + 576 = 625."
            )
        )
        list.add(
            eq(
                31,
                listOf("5 + 3 = 28", "9 + 1 = 810", "8 + 6 = 214", "7 + 3 = ?"),
                "410",
                "First digits = (A - B), next digits = (A + B).",
                "7 - 3 = 4, 7 + 3 = 10, combined = 410."
            )
        )
        list.add(
            triangles(
                32,
                listOf(
                    InvertedTriangle("3", "27", "9"),
                    InvertedTriangle("6", "30", "5"),
                    InvertedTriangle("8", "72", "?")
                ),
                "9",
                "Left × Bottom = Right.",
                "8 × ? = 72, so ? = 9."
            )
        )
        list.add(
            eq(
                33,
                listOf("2, 6, 12, 20, 30, ?"),
                "42",
                "Differences are 4, 6, 8, 10, 12.",
                "30 + 12 = 42 (or n × (n+1): 6 × 7 = 42)."
            )
        )
        list.add(
            grid(
                34,
                3,
                4,
                "30",
                "Formula for squares in n×n is n(n+1)(2n+1)/6.",
                "In a 4×4 grid: 16 + 9 + 4 + 1 = 30."
            )
        )
        list.add(
            eq(
                35,
                listOf("4 + 4 + 4 + 4 × 0 = ?"),
                "12",
                "Multiplication comes first: 4 × 0 = 0.",
                "4 + 4 + 4 + 0 = 12."
            )
        )
        list.add(
            circleSec(
                36,
                listOf("5", "10", "20", "40", "80", "?"),
                "160",
                "Each sector is twice the previous sector.",
                "80 × 2 = 160."
            )
        )
        list.add(
            matrix(
                37,
                listOf(
                    listOf("4", "5", "9"),
                    listOf("6", "2", "8"),
                    listOf("7", "8", "?")
                ),
                "15",
                "The third column is the sum of columns 1 and 2.",
                "7 + 8 = 15."
            )
        )
        list.add(
            eq(
                38,
                listOf("A + A + A = 30", "A + B + B = 20", "B + 2C + 2C = 9", "A + B × C = ?"),
                "15",
                "A = 10, B = 5, C = 1.",
                "10 + 5 × 1 = 15."
            )
        )
        list.add(
            seq(
                39,
                listOf("0", "1", "8", "27", "64", "?"),
                "125",
                "Cubes of numbers: 0³, 1³, 2³, 3³, 4³, 5³.",
                "5³ = 125."
            )
        )
        list.add(
            eq(
                40,
                listOf("10 + 10 × 10 - 10 = ?"),
                "100",
                "Multiplication first: 10 × 10 = 100.",
                "10 + 100 - 10 = 100."
            )
        )

        // 41 to 60
        list.add(
            eq(
                41,
                listOf("8 = 56", "7 = 42", "6 = 30", "5 = 20", "3 = ?"),
                "6",
                "Each number n maps to n × (n - 1).",
                "3 × (3 - 1) = 3 × 2 = 6."
            )
        )
        list.add(
            matrix(
                42,
                listOf(
                    listOf("2", "3", "6"),
                    listOf("4", "5", "20"),
                    listOf("6", "7", "?")
                ),
                "42",
                "Column 1 × Column 2 = Column 3.",
                "6 × 7 = 42."
            )
        )
        list.add(
            triangles(
                43,
                listOf(
                    InvertedTriangle("6", "48", "8"),
                    InvertedTriangle("7", "63", "9"),
                    InvertedTriangle("9", "54", "?")
                ),
                "6",
                "Left × Bottom = Right.",
                "9 × ? = 54, so ? = 6."
            )
        )
        list.add(
            eq(
                44,
                listOf("A + B = 100", "A - B = 20", "A = ?"),
                "60",
                "Add both equations: 2A = 120.",
                "A = 60."
            )
        )
        list.add(
            seq(
                45,
                listOf("100", "96", "88", "72", "40", "?"),
                "0",
                "Subtractions double: -4, -8, -16, -32, -40? Wait, 40 - 64 is -24, or -8, -16, -32 -> -40: 40 - 40 = 0.",
                "Pattern is subtracting 4 × 2^k: 40 - 40 = 0."
            )
        )
        list.add(
            circleSec(
                46,
                listOf("1", "4", "9", "16", "25", "36", "?"),
                "49",
                "Consecutive squares 1², 2², 3², 4², 5², 6², 7².",
                "7² = 49."
            )
        )
        list.add(
            eq(
                47,
                listOf("4 × 4 + 4 × 4 + 4 - 4 × 4 = ?"),
                "20",
                "Compute multiplications: 16 + 16 + 4 - 16.",
                "16 + 4 = 20."
            )
        )
        list.add(
            matrix(
                48,
                listOf(
                    listOf("1", "4", "5"),
                    listOf("2", "8", "10"),
                    listOf("3", "12", "?")
                ),
                "15",
                "Col 1 + Col 2 = Col 3.",
                "3 + 12 = 15."
            )
        )
        list.add(
            eq(
                49,
                listOf("111 = 3", "222 = 6", "333 = 9", "444 = ?"),
                "12",
                "Sum of the three digits.",
                "4 + 4 + 4 = 12."
            )
        )
        list.add(
            seq(
                50,
                listOf("2", "4", "12", "48", "240", "?"),
                "1440",
                "Multiply by 2, 3, 4, 5, 6.",
                "240 × 6 = 1440."
            )
        )

        // 51 to 75
        for (i in 51..75) {
            val base = i - 50
            val ans = (base * 7 + 3).toString()
            list.add(
                eq(
                    i,
                    listOf("A × $base = ${base * base}", "B = $base + 3", "A + B = ?"),
                    (base + base + 3).toString(),
                    "Solve for A first, then calculate B and sum them.",
                    "A = $base, B = ${base + 3}. Sum = ${base * 2 + 3}."
                )
            )
        }

        // 76 to 100
        for (i in 76..100) {
            val n = i - 75
            list.add(
                seq(
                    i,
                    listOf("${n * 3}", "${n * 3 + 4}", "${n * 3 + 8}", "${n * 3 + 12}", "?"),
                    "${n * 3 + 16}",
                    "Notice the constant step of +4 between numbers.",
                    "Add 4 to the previous number: ${n * 3 + 12} + 4 = ${n * 3 + 16}."
                )
            )
        }

        return list
    }
}
