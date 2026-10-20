package com.example.data.model

enum class PuzzleType {
    EQUATION,
    GRID_COUNT,
    INVERTED_TRIANGLES,
    SPOKE_WHEEL,
    NUMBER_MATRIX,
    NUMBER_TRIANGLE,
    SEQUENCE,
    CIRCLE_SECTORS,
    DOMINO_PAIR,
    SHAPE_MATH
}

data class SpokeWheelData(
    val centerNumber: Int? = null,
    val outerNumbers: List<String>, // 8 outer numbers, one may be "?"
    val innerNumbers: List<Int>     // 8 inner numbers
)

data class InvertedTriangle(
    val topLeft: String,
    val topRight: String,
    val bottom: String
)

data class PuzzleLevel(
    val id: Int,
    val type: PuzzleType,
    val title: String = "Level $id",
    val equationLines: List<String> = emptyList(),
    val gridCountData: Pair<Int, Int>? = null, // e.g. 2 for 2x2, 3 for 3x3
    val trianglesData: List<InvertedTriangle> = emptyList(),
    val spokeWheelData: SpokeWheelData? = null,
    val matrixData: List<List<String>> = emptyList(),
    val triangleVertexData: List<String> = emptyList(), // top, left, right, center
    val sequenceData: List<String> = emptyList(),
    val circleSectors: List<String> = emptyList(),
    val answer: String,
    val hint: String,
    val explanation: String
)
