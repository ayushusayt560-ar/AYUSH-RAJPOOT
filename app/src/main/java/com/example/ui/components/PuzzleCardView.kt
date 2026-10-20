package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvertedTriangle
import com.example.data.model.PuzzleLevel
import com.example.data.model.PuzzleType
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PuzzleCardView(
    puzzle: PuzzleLevel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    listOf(Color(0xFF1E293B), Color(0xFF141C2E))
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(1.dp, Color(0xFF334155), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (puzzle.type) {
            PuzzleType.EQUATION -> {
                EquationView(lines = puzzle.equationLines)
            }
            PuzzleType.GRID_COUNT -> {
                GridCountPuzzleView()
            }
            PuzzleType.INVERTED_TRIANGLES -> {
                InvertedTrianglesView(triangles = puzzle.trianglesData)
            }
            PuzzleType.SPOKE_WHEEL -> {
                puzzle.spokeWheelData?.let { wheelData ->
                    SpokeWheelPuzzleView(
                        outerNumbers = wheelData.outerNumbers,
                        innerNumbers = wheelData.innerNumbers
                    )
                } ?: EquationView(lines = puzzle.equationLines)
            }
            PuzzleType.NUMBER_MATRIX -> {
                NumberMatrixView(rows = puzzle.matrixData)
            }
            PuzzleType.NUMBER_TRIANGLE -> {
                SingleNumberTriangleView(vertices = puzzle.triangleVertexData)
            }
            PuzzleType.SEQUENCE -> {
                SequencePuzzleView(items = puzzle.sequenceData)
            }
            PuzzleType.CIRCLE_SECTORS -> {
                CircleSectorsPuzzleView(sectors = puzzle.circleSectors)
            }
            else -> {
                EquationView(lines = puzzle.equationLines)
            }
        }
    }
}

@Composable
fun EquationView(lines: List<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        lines.forEach { line ->
            Text(
                text = line,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraLight,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Renders the 2x2 = 5 and 3x3 = ? puzzle (Level 21, Screenshot 1)
 */
@Composable
fun GridCountPuzzleView() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        // Top: 2x2 grid = 5
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            DrawGrid(gridSize = 2, cellSize = 42.dp)
            Text(
                text = "=  5",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraLight,
                fontFamily = FontFamily.SansSerif
            )
        }

        // Bottom: 3x3 grid = ?
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            DrawGrid(gridSize = 3, cellSize = 38.dp)
            Text(
                text = "=  ?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraLight,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}

@Composable
fun DrawGrid(gridSize: Int, cellSize: androidx.compose.ui.unit.Dp) {
    val totalSize = cellSize * gridSize
    Canvas(modifier = Modifier.size(totalSize)) {
        val strokeWidthPx = 1.5.dp.toPx()
        val w = size.width
        val h = size.height

        // Outer border
        drawRect(
            color = Color.White,
            topLeft = Offset(0f, 0f),
            size = size,
            style = Stroke(width = strokeWidthPx)
        )

        // Internal grid lines
        val stepX = w / gridSize
        val stepY = h / gridSize
        for (i in 1 until gridSize) {
            drawLine(
                color = Color.White,
                start = Offset(stepX * i, 0f),
                end = Offset(stepX * i, h),
                strokeWidth = strokeWidthPx
            )
            drawLine(
                color = Color.White,
                start = Offset(0f, stepY * i),
                end = Offset(w, stepY * i),
                strokeWidth = strokeWidthPx
            )
        }
    }
}

/**
 * Inverted Triangles (Level 17, Screenshot 2)
 * Two on top, one in bottom center.
 */
@Composable
fun InvertedTrianglesView(triangles: List<InvertedTriangle>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        if (triangles.size >= 2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                SingleInvertedTriangle(triangle = triangles[0])
                SingleInvertedTriangle(triangle = triangles[1])
            }
        }
        if (triangles.size >= 3) {
            SingleInvertedTriangle(triangle = triangles[2])
        }
    }
}

@Composable
fun SingleInvertedTriangle(triangle: InvertedTriangle) {
    Box(
        modifier = Modifier
            .width(130.dp)
            .height(100.dp)
    ) {
        // Top-left text
        Text(
            text = triangle.topLeft,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.align(Alignment.TopStart)
        )

        // Top-right text
        Text(
            text = triangle.topRight,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.align(Alignment.TopEnd)
        )

        // Inverted triangle shape in center
        Canvas(
            modifier = Modifier
                .size(width = 56.dp, height = 48.dp)
                .align(Alignment.Center)
        ) {
            val strokeWidthPx = 1.5.dp.toPx()
            val w = size.width
            val h = size.height

            val path = Path().apply {
                moveTo(0f, 0f)       // Top-left
                lineTo(w, 0f)        // Top-right
                lineTo(w / 2f, h)    // Bottom point
                close()
            }
            drawPath(path = path, color = Color.White, style = Stroke(width = strokeWidthPx))
        }

        // Bottom text
        Text(
            text = triangle.bottom,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraLight,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Spoke Wheel (Level 27, Screenshot 5)
 * 8 spokes wheel with outer and inner numbers.
 */
@Composable
fun SpokeWheelPuzzleView(
    outerNumbers: List<String>,
    innerNumbers: List<Int>
) {
    BoxWithConstraints(
        modifier = Modifier
            .size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Center lines
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 1.2.dp.toPx()
            val cx = size.width / 2f
            val cy = size.height / 2f
            val r = size.width * 0.36f

            // Draw 4 diameter lines (8 spokes)
            for (i in 0 until 4) {
                val angleRad = Math.toRadians((i * 45.0) - 90.0)
                val x1 = (cx + r * cos(angleRad)).toFloat()
                val y1 = (cy + r * sin(angleRad)).toFloat()
                val x2 = (cx - r * cos(angleRad)).toFloat()
                val y2 = (cy - r * sin(angleRad)).toFloat()
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = strokePx
                )
            }
        }

        // 8 Outer Numbers positioned at radius ~122dp
        val outerRadiusDp = 115.dp
        for (i in 0 until 8) {
            val angleDeg = (i * 45.0) - 90.0
            val angleRad = Math.toRadians(angleDeg)
            val num = outerNumbers.getOrElse(i) { "" }

            val offsetX = (outerRadiusDp.value * cos(angleRad)).dp
            val offsetY = (outerRadiusDp.value * sin(angleRad)).dp

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 0.dp)
            ) {
                Text(
                    text = num,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraLight,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Overlay with custom layout using Canvas / Box
        WheelNumbersOverlay(outerNumbers = outerNumbers, innerNumbers = innerNumbers)
    }
}

@Composable
fun WheelNumbersOverlay(outerNumbers: List<String>, innerNumbers: List<Int>) {
    Box(
        modifier = Modifier.size(280.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer numbers (top: 0, top-right: 1, right: 2, etc.)
        val outerR = 118f
        for (i in 0 until 8) {
            val angle = Math.toRadians((i * 45.0) - 90.0)
            val x = (outerR * cos(angle)).toFloat()
            val y = (outerR * sin(angle)).toFloat()
            val text = outerNumbers.getOrElse(i) { "" }

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(40.dp)
                    .height(30.dp),
                contentAlignment = Alignment.Center
            ) {
                // Apply offset
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(start = (x * 2).dp, top = (y * 2).dp)
                )
            }
        }

        // Let's draw the numbers accurately via Canvas Text or absolute layout
        WheelCustomLabels(outerNumbers = outerNumbers, innerNumbers = innerNumbers)
    }
}

@Composable
fun WheelCustomLabels(outerNumbers: List<String>, innerNumbers: List<Int>) {
    // 8 outer positions: Top(20), TR(16), R(?), BR(15), B(17), BL(7), L(4), TL(10)
    // 8 inner positions in sectors:
    // top sector left=9, right=11; etc.
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top outer: 20
        Text(text = outerNumbers.getOrElse(0) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)

        // Upper section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = outerNumbers.getOrElse(7) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(text = "${innerNumbers.getOrElse(0) { 9 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
                Text(text = "${innerNumbers.getOrElse(1) { 11 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
            }
            Text(text = outerNumbers.getOrElse(1) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
        }

        // Mid section (Left, Center inners, Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = outerNumbers.getOrElse(6) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
            Row(
                modifier = Modifier.width(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${innerNumbers.getOrElse(7) { 1 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
                Text(text = "${innerNumbers.getOrElse(2) { 5 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
            }
            Text(text = outerNumbers.getOrElse(2) { "" }, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Light)
        }

        // Lower middle section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(20.dp))
            Row(
                modifier = Modifier.width(180.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${innerNumbers.getOrElse(6) { 3 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
                Text(text = "${innerNumbers.getOrElse(3) { 2 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
            }
            Spacer(modifier = Modifier.width(20.dp))
        }

        // Lower section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = outerNumbers.getOrElse(5) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
            Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Text(text = "${innerNumbers.getOrElse(5) { 4 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
                Text(text = "${innerNumbers.getOrElse(4) { 13 }}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraLight)
            }
            Text(text = outerNumbers.getOrElse(3) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
        }

        // Bottom outer: 17
        Text(text = outerNumbers.getOrElse(4) { "" }, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Light)
    }
}

/**
 * 3x3 Number Matrix with clean dividers
 */
@Composable
fun NumberMatrixView(rows: List<List<String>>) {
    Column(
        modifier = Modifier
            .background(Color(0xFF1E1E1E), shape = RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF333333), shape = RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        rows.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                row.forEach { cell ->
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFF262626), shape = RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cell,
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Light
                        )
                    }
                }
            }
        }
    }
}

/**
 * Single triangle with numbers at vertices and center
 */
@Composable
fun SingleNumberTriangleView(vertices: List<String>) {
    Box(
        modifier = Modifier
            .size(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidthPx = 1.5.dp.toPx()
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w / 2f, 10f)
                lineTo(10f, h - 10f)
                lineTo(w - 10f, h - 10f)
                close()
            }
            drawPath(path = path, color = Color.White, style = Stroke(width = strokeWidthPx))
        }

        // Vertices
        if (vertices.size >= 4) {
            // Top
            Text(
                text = vertices[0],
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.TopCenter)
            )
            // Left
            Text(
                text = vertices[1],
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.BottomStart)
            )
            // Right
            Text(
                text = vertices[2],
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Light,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
            // Center
            Text(
                text = vertices[3],
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * Sequence cards: [ 4 ] -> [ 8 ] -> [ 16 ] -> ...
 */
@Composable
fun SequencePuzzleView(items: List<String>) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(0xFF242424), shape = RoundedCornerShape(6.dp))
                        .border(1.dp, Color(0xFF383838), shape = RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        color = if (item == "?") Color(0xFFFFD60A) else Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Light
                    )
                }
                if (index < items.size - 1) {
                    Text(
                        text = "•",
                        color = Color(0xFF666666),
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

/**
 * Circle divided into sectors with numbers
 */
@Composable
fun CircleSectorsPuzzleView(sectors: List<String>) {
    Box(
        modifier = Modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = 1.5.dp.toPx()
            val r = size.width / 2f - 10f
            val cx = size.width / 2f
            val cy = size.height / 2f

            // Outer circle
            drawCircle(
                color = Color.White,
                radius = r,
                center = center,
                style = Stroke(width = strokePx)
            )

            // Dividers
            val count = sectors.size.coerceAtLeast(4)
            val step = 360.0 / count
            for (i in 0 until count / 2) {
                val rad = Math.toRadians(i * step)
                val x1 = (cx + r * cos(rad)).toFloat()
                val y1 = (cy + r * sin(rad)).toFloat()
                val x2 = (cx - r * cos(rad)).toFloat()
                val y2 = (cy - r * sin(rad)).toFloat()
                drawLine(
                    color = Color.White,
                    start = Offset(x1, y1),
                    end = Offset(x2, y2),
                    strokeWidth = strokePx
                )
            }
        }

        // Numbers in sectors
        val rDp = 65.dp
        sectors.forEachIndexed { i, s ->
            val step = 360.0 / sectors.size
            val rad = Math.toRadians((i * step) + (step / 2.0) - 90.0)
            val offX = (rDp.value * cos(rad)).dp
            val offY = (rDp.value * sin(rad)).dp

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = offX * 1.8f, top = offY * 1.8f)
            ) {
                Text(
                    text = s,
                    color = if (s == "?") Color(0xFFFFD60A) else Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}
