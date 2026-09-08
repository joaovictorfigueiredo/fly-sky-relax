package com.example.ui.renderers

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import com.example.model.BlackHole
import com.example.model.MatrixCodeStream
import com.example.model.Predator
import com.example.model.PredatorCategory
import com.example.model.PredatorType
import com.example.model.Realm
import com.example.viewmodel.GameUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * Renders cosmic anomalies, black holes, light-speed hyperspace warp,
 * matrix code streams, and cute & ugly predators.
 */
object CosmicEntitiesRenderer {

    fun DrawScope.drawBlackHoles(blackHoles: List<BlackHole>) {
        blackHoles.forEach { bh ->
            val center = Offset(bh.x, bh.y)
            val r = bh.radius

            // 1. Gravitational pull aura ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF7C4DFF).copy(alpha = 0.22f),
                        Color(0xFF00E5FF).copy(alpha = 0.10f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = bh.pullRadius
                ),
                radius = bh.pullRadius,
                center = center
            )

            // 2. Swirling Accretion Disk Arms
            rotate(bh.rotation, pivot = center) {
                val armCount = 6
                for (i in 0 until armCount) {
                    val angle = (i * 360f / armCount)
                    rotate(angle, pivot = center) {
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    Color(0xFFFF5252).copy(alpha = 0.85f),
                                    Color(0xFFFFAB40).copy(alpha = 0.95f),
                                    Color(0xFF00E5FF).copy(alpha = 0.75f),
                                    Color.Transparent
                                ),
                                center = center
                            ),
                            startAngle = 0f,
                            sweepAngle = 140f,
                            useCenter = false,
                            topLeft = Offset(center.x - r * 1.6f, center.y - r * 1.6f),
                            size = Size(r * 3.2f, r * 3.2f),
                            style = Stroke(width = 8f + sin(bh.phase + i) * 3f, cap = StrokeCap.Round)
                        )
                    }
                }
            }

            // 3. Gravitational Lensing Halo (White-hot photon sphere)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.95f),
                        Color(0xFF00E5FF).copy(alpha = 0.75f),
                        Color(0xFF651FFF).copy(alpha = 0.35f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = r * 1.15f
                ),
                radius = r * 1.15f,
                center = center
            )

            // 4. Absolute Singularity (Deep pitch-black core)
            drawCircle(
                color = Color.Black,
                radius = r * 0.85f,
                center = center
            )

            // 5. Inward horizon suction lines
            for (k in 0 until 8) {
                val inAngle = k * (PI.toFloat() * 2f / 8f) + bh.phase
                val outerDist = r * (1.2f + (k % 3) * 0.25f)
                val innerDist = r * 0.85f
                drawLine(
                    color = Color.White.copy(alpha = 0.45f),
                    start = Offset(center.x + cos(inAngle) * outerDist, center.y + sin(inAngle) * outerDist),
                    end = Offset(center.x + cos(inAngle) * innerDist, center.y + sin(inAngle) * innerDist),
                    strokeWidth = 2f
                )
            }
        }
    }

    fun DrawScope.drawPredators(predators: List<Predator>) {
        predators.forEach { pred ->
            val center = Offset(pred.x, pred.y)
            val isCute = pred.type.category == PredatorCategory.CUTE
            val isPacified = pred.isPacified

            if (isCute) {
                drawCutePredator(pred, center, isPacified)
            } else {
                drawUglyPredator(pred, center, isPacified)
            }
        }
    }

    private fun DrawScope.drawCutePredator(pred: Predator, center: Offset, isPacified: Boolean) {
        val r = pred.size
        val wingFlap = sin(pred.phase * 5f)

        // Pacified or happy halo
        if (isPacified) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD54F).copy(alpha = 0.5f), Color.Transparent),
                    center = center,
                    radius = r * 2.2f
                ),
                radius = r * 2.2f,
                center = center
            )
            // Golden halo ring above head
            drawOval(
                color = Color(0xFFFFD54F),
                topLeft = Offset(center.x - r * 0.6f, center.y - r * 1.35f),
                size = Size(r * 1.2f, r * 0.4f),
                style = Stroke(width = 3f)
            )
        }

        when (pred.type) {
            PredatorType.CUTE_BAT -> {
                // Little bat wings
                val wingPathLeft = Path().apply {
                    moveTo(center.x - r * 0.4f, center.y)
                    quadraticBezierTo(
                        center.x - r * 1.8f,
                        center.y - r * 0.9f * wingFlap,
                        center.x - r * 1.6f,
                        center.y + r * 0.3f
                    )
                    quadraticBezierTo(
                        center.x - r * 1.0f,
                        center.y + r * 0.1f,
                        center.x - r * 0.3f,
                        center.y + r * 0.5f
                    )
                    close()
                }
                val wingPathRight = Path().apply {
                    moveTo(center.x + r * 0.4f, center.y)
                    quadraticBezierTo(
                        center.x + r * 1.8f,
                        center.y - r * 0.9f * wingFlap,
                        center.x + r * 1.6f,
                        center.y + r * 0.3f
                    )
                    quadraticBezierTo(
                        center.x + r * 1.0f,
                        center.y + r * 0.1f,
                        center.x + r * 0.3f,
                        center.y + r * 0.5f
                    )
                    close()
                }
                drawPath(wingPathLeft, color = pred.type.secondaryColor.copy(alpha = 0.85f))
                drawPath(wingPathRight, color = pred.type.secondaryColor.copy(alpha = 0.85f))

                // Fluffy purple body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pred.type.primaryColor, Color(0xFF4A148C)),
                        center = center,
                        radius = r
                    ),
                    radius = r,
                    center = center
                )

                // Cute pointy ears
                val leftEar = Path().apply {
                    moveTo(center.x - r * 0.6f, center.y - r * 0.5f)
                    lineTo(center.x - r * 0.8f, center.y - r * 1.25f)
                    lineTo(center.x - r * 0.2f, center.y - r * 0.8f)
                    close()
                }
                val rightEar = Path().apply {
                    moveTo(center.x + r * 0.6f, center.y - r * 0.5f)
                    lineTo(center.x + r * 0.8f, center.y - r * 1.25f)
                    lineTo(center.x + r * 0.2f, center.y - r * 0.8f)
                    close()
                }
                drawPath(leftEar, color = pred.type.secondaryColor)
                drawPath(rightEar, color = pred.type.secondaryColor)
            }

            PredatorType.CUTE_JELLY -> {
                // Pulsing jellyfish dome
                val pulseScale = 1f + sin(pred.phase * 3.5f) * 0.12f
                drawArc(
                    brush = Brush.verticalGradient(
                        colors = listOf(pred.type.primaryColor, pred.type.secondaryColor),
                        startY = center.y - r,
                        endY = center.y + r * 0.4f
                    ),
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(center.x - r * pulseScale, center.y - r * pulseScale),
                    size = Size(r * 2f * pulseScale, r * 1.8f * pulseScale)
                )

                // Gentle wavy tentacles
                for (t in -2..2) {
                    val tX = center.x + t * (r * 0.35f)
                    val tentaclePath = Path().apply {
                        moveTo(tX, center.y + r * 0.3f)
                        quadraticBezierTo(
                            tX + sin(pred.phase * 3f + t) * 16f,
                            center.y + r * 1.1f,
                            tX - sin(pred.phase * 3f + t) * 12f,
                            center.y + r * 1.8f
                        )
                    }
                    drawPath(
                        tentaclePath,
                        color = pred.type.secondaryColor.copy(alpha = 0.75f),
                        style = Stroke(width = 3.5f, cap = StrokeCap.Round)
                    )
                }
            }

            PredatorType.CUTE_BABY_DRAGON -> {
                // Mint dragon body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(pred.type.primaryColor, Color(0xFF00897B)),
                        center = center,
                        radius = r
                    ),
                    radius = r,
                    center = center
                )

                // Tiny golden horns
                drawOval(
                    color = Color(0xFFFFD54F),
                    topLeft = Offset(center.x - r * 0.65f, center.y - r * 1.15f),
                    size = Size(r * 0.35f, r * 0.6f)
                )
                drawOval(
                    color = Color(0xFFFFD54F),
                    topLeft = Offset(center.x + r * 0.3f, center.y - r * 1.15f),
                    size = Size(r * 0.35f, r * 0.6f)
                )

                // Tiny fairy dragon wings
                drawCircle(
                    color = pred.type.secondaryColor.copy(alpha = 0.75f),
                    radius = r * 0.65f,
                    center = Offset(center.x - r * 0.9f, center.y - r * 0.2f * wingFlap)
                )
                drawCircle(
                    color = pred.type.secondaryColor.copy(alpha = 0.75f),
                    radius = r * 0.65f,
                    center = Offset(center.x + r * 0.9f, center.y - r * 0.2f * wingFlap)
                )
            }

            else -> {}
        }

        // Big Anime / Kawaii Eyes
        val eyeOffsetY = -r * 0.1f
        val eyeSpacing = r * 0.36f
        // Left Eye
        drawCircle(color = Color(0xFF212121), radius = r * 0.22f, center = Offset(center.x - eyeSpacing, center.y + eyeOffsetY))
        drawCircle(color = Color.White, radius = r * 0.08f, center = Offset(center.x - eyeSpacing - r * 0.05f, center.y + eyeOffsetY - r * 0.06f))
        // Right Eye
        drawCircle(color = Color(0xFF212121), radius = r * 0.22f, center = Offset(center.x + eyeSpacing, center.y + eyeOffsetY))
        drawCircle(color = Color.White, radius = r * 0.08f, center = Offset(center.x + eyeSpacing - r * 0.05f, center.y + eyeOffsetY - r * 0.06f))

        // Cute pink blush cheeks
        drawCircle(color = Color(0xFFFF4081).copy(alpha = 0.65f), radius = r * 0.14f, center = Offset(center.x - eyeSpacing * 1.4f, center.y + r * 0.2f))
        drawCircle(color = Color(0xFFFF4081).copy(alpha = 0.65f), radius = r * 0.14f, center = Offset(center.x + eyeSpacing * 1.4f, center.y + r * 0.2f))

        // Tiny cute mouth
        val mouthPath = Path().apply {
            moveTo(center.x - r * 0.12f, center.y + r * 0.25f)
            quadraticBezierTo(center.x, center.y + r * 0.38f, center.x + r * 0.12f, center.y + r * 0.25f)
        }
        drawPath(mouthPath, color = Color(0xFF424242), style = Stroke(width = 2.5f, cap = StrokeCap.Round))
    }

    private fun DrawScope.drawUglyPredator(pred: Predator, center: Offset, isPacified: Boolean) {
        val r = pred.size

        // If pacified by player's light, show calming golden glow
        if (isPacified) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD54F).copy(alpha = 0.45f), Color.Transparent),
                    center = center,
                    radius = r * 2.2f
                ),
                radius = r * 2.2f,
                center = center
            )
        } else {
            // Dark ominous aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(pred.type.secondaryColor.copy(alpha = 0.35f), Color.Transparent),
                    center = center,
                    radius = r * 1.8f
                ),
                radius = r * 1.8f,
                center = center
            )
        }

        when (pred.type) {
            PredatorType.UGLY_ABYSS_BEAST -> {
                // Spiky dark round monster
                val bodySpikes = 10
                val bodyPath = Path()
                for (s in 0 until bodySpikes) {
                    val angle = s * (PI.toFloat() * 2f / bodySpikes) + pred.phase * 0.8f
                    val outerR = r * (1.15f + sin(pred.phase * 4f + s) * 0.15f)
                    val innerR = r * 0.85f
                    val midAngle = angle + (PI.toFloat() / bodySpikes)
                    val p1X = center.x + cos(angle) * outerR
                    val p1Y = center.y + sin(angle) * outerR
                    val p2X = center.x + cos(midAngle) * innerR
                    val p2Y = center.y + sin(midAngle) * innerR
                    if (s == 0) bodyPath.moveTo(p1X, p1Y) else bodyPath.lineTo(p1X, p1Y)
                    bodyPath.lineTo(p2X, p2Y)
                }
                bodyPath.close()
                drawPath(bodyPath, color = pred.type.primaryColor)

                // Giant cyclops red eye
                drawCircle(color = Color.White, radius = r * 0.42f, center = center)
                drawCircle(color = pred.type.secondaryColor, radius = r * 0.28f, center = center)
                // Slit pupil
                drawOval(
                    color = Color.Black,
                    topLeft = Offset(center.x - r * 0.08f, center.y - r * 0.24f),
                    size = Size(r * 0.16f, r * 0.48f)
                )

                // Sharp fangs
                for (f in -2..2) {
                    val fangPath = Path().apply {
                        val fX = center.x + f * (r * 0.22f)
                        moveTo(fX - r * 0.08f, center.y + r * 0.45f)
                        lineTo(fX, center.y + r * 0.72f)
                        lineTo(fX + r * 0.08f, center.y + r * 0.45f)
                        close()
                    }
                    drawPath(fangPath, color = Color(0xFFFFF9C4))
                }
            }

            PredatorType.UGLY_MAGMA_WORM -> {
                // Segmented lava caterpillar/worm
                val segmentCount = 5
                for (seg in segmentCount downTo 0) {
                    val sOffset = seg * (r * 0.35f)
                    val waveY = sin(pred.phase * 4f + seg) * (r * 0.2f)
                    val sCenter = Offset(center.x - sOffset, center.y + waveY)
                    val segRadius = r * (1f - seg * 0.12f)

                    // Basalt rock shell
                    drawCircle(color = pred.type.primaryColor, radius = segRadius, center = sCenter)
                    // Molten lava cracks
                    drawCircle(
                        color = pred.type.secondaryColor.copy(alpha = 0.85f),
                        radius = segRadius * 0.65f,
                        center = sCenter,
                        style = Stroke(width = 3f)
                    )
                }

                // Glowing fiery red eyes on front segment
                drawCircle(color = Color(0xFFFFD54F), radius = r * 0.16f, center = Offset(center.x + r * 0.2f, center.y - r * 0.2f))
                drawCircle(color = Color(0xFFFFD54F), radius = r * 0.16f, center = Offset(center.x + r * 0.2f, center.y + r * 0.2f))
            }

            PredatorType.UGLY_VOID_CRAWLER -> {
                // Spidery venomous crawler
                // Mandible pincers
                val leftPincer = Path().apply {
                    moveTo(center.x - r * 0.3f, center.y - r * 0.5f)
                    quadraticBezierTo(center.x - r * 1.2f, center.y - r * 0.9f, center.x - r * 0.4f, center.y - r * 1.2f)
                }
                val rightPincer = Path().apply {
                    moveTo(center.x + r * 0.3f, center.y - r * 0.5f)
                    quadraticBezierTo(center.x + r * 1.2f, center.y - r * 0.9f, center.x + r * 0.4f, center.y - r * 1.2f)
                }
                drawPath(leftPincer, color = pred.type.secondaryColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
                drawPath(rightPincer, color = pred.type.secondaryColor, style = Stroke(width = 5f, cap = StrokeCap.Round))

                // Obsidian body
                drawOval(
                    color = pred.type.primaryColor,
                    topLeft = Offset(center.x - r * 0.7f, center.y - r * 0.9f),
                    size = Size(r * 1.4f, r * 1.8f)
                )

                // 4 Toxic glowing eyes
                val eyePositions = listOf(
                    Offset(center.x - r * 0.3f, center.y - r * 0.4f),
                    Offset(center.x + r * 0.3f, center.y - r * 0.4f),
                    Offset(center.x - r * 0.15f, center.y - r * 0.65f),
                    Offset(center.x + r * 0.15f, center.y - r * 0.65f)
                )
                eyePositions.forEach { pos ->
                    drawCircle(color = pred.type.secondaryColor, radius = r * 0.12f, center = pos)
                    drawCircle(color = Color.White, radius = r * 0.05f, center = pos)
                }
            }

            else -> {}
        }
    }

    fun DrawScope.drawMatrixCodeStreams(streams: List<MatrixCodeStream>, state: GameUiState) {
        if (state.currentRealm != Realm.MATRIX) return

        val textPaint = Paint().apply {
            color = android.graphics.Color.argb(230, 0, 230, 118)
            textSize = 28f
            isAntiAlias = true
            typeface = Typeface.MONOSPACE
        }
        val headPaint = Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 30f
            isAntiAlias = true
            typeface = Typeface.MONOSPACE
            setShadowLayer(8f, 0f, 0f, android.graphics.Color.GREEN)
        }

        streams.forEach { stream ->
            var curY = stream.y
            val charArray = stream.chars.toCharArray()
            for (idx in charArray.indices) {
                val ch = charArray[idx].toString()
                if (idx == charArray.lastIndex) {
                    // Glowing white head character
                    drawContext.canvas.nativeCanvas.drawText(ch, stream.x, curY, headPaint)
                } else {
                    // Phosphor green fading tail
                    val alphaRatio = (idx.toFloat() / charArray.size).coerceIn(0.2f, 1f)
                    textPaint.alpha = (alphaRatio * stream.alpha * 255).toInt().coerceIn(30, 255)
                    drawContext.canvas.nativeCanvas.drawText(ch, stream.x, curY, textPaint)
                }
                curY += 26f
            }
        }
    }

    fun DrawScope.drawHyperspaceWarp(progress: Float) {
        val center = Offset(size.width * 0.5f, size.height * 0.5f)
        val maxR = hypot(size.width, size.height)

        // 1. Hyperspace tunnel flash
        drawRect(
            color = Color(0xFF00E5FF).copy(alpha = 0.15f * progress),
            size = size
        )

        // 2. Light speed radiating beams bursting from center
        val beamCount = 36
        for (i in 0 until beamCount) {
            val angle = i * (PI.toFloat() * 2f / beamCount)
            val innerR = 40f + (1f - progress) * 120f
            val outerR = maxR * (0.4f + (1f - progress) * 0.6f)
            val beamColor = if (i % 2 == 0) Color.White else Color(0xFF00E5FF)
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        beamColor.copy(alpha = 0.85f * progress),
                        Color.White.copy(alpha = 0.95f * progress)
                    ),
                    start = Offset(center.x + cos(angle) * innerR, center.y + sin(angle) * innerR),
                    end = Offset(center.x + cos(angle) * outerR, center.y + sin(angle) * outerR)
                ),
                start = Offset(center.x + cos(angle) * innerR, center.y + sin(angle) * innerR),
                end = Offset(center.x + cos(angle) * outerR, center.y + sin(angle) * outerR),
                strokeWidth = 3.5f + (i % 3) * 2f,
                cap = StrokeCap.Round
            )
        }

        // 3. Central warp vortex ring
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.75f * progress),
                    Color(0xFF00E5FF).copy(alpha = 0.35f * progress),
                    Color.Transparent
                ),
                center = center,
                radius = 160f
            ),
            radius = 160f,
            center = center
        )
    }
}
