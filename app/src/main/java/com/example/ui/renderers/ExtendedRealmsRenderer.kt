package com.example.ui.renderers

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.viewmodel.GameUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Scenery renderers for the 10 extended environments requested by the user:
 * 1. Cyberpunk
 * 2. Matrix
 * 3. Cogumelos (Mushroom Forest)
 * 4. Nuvens (Clouds Sea)
 * 5. Montanhas (Alpine Mountains)
 * 6. Marte (Mars Canyons & Dunes)
 * 7. Tempestade (Tempest Storm & Lightning)
 * 8. Andrômeda (Spiral Galaxy)
 * 9. Medieval (Ancient Castle Towers)
 * 10. Fantasia Noturna (Night Fantasy & Floating Lanterns)
 */
object ExtendedRealmsRenderer {

    // 1. CYBERPUNK REALM
    fun DrawScope.drawCyberpunkRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Distant horizon neon grid
        val horizonY = h * 0.78f + (alt * 0.08f - pY * 0.08f)
        for (i in 0..12) {
            val gridX = (i * (w / 12f) - (state.cameraX * 0.15f) % (w / 12f))
            drawLine(
                color = Color(0xFF00E5FF).copy(alpha = 0.22f * alpha),
                start = Offset(gridX, horizonY),
                end = Offset((gridX - w * 0.5f) * 2.2f + w * 0.5f, h),
                strokeWidth = 1.5f
            )
        }
        drawLine(
            color = Color(0xFFFF007F).copy(alpha = 0.45f * alpha),
            start = Offset(0f, horizonY),
            end = Offset(w, horizonY),
            strokeWidth = 2.5f
        )

        // Mega-skyscrapers in parallax
        val buildingLayers = listOf(
            Triple(0.2f, Color(0xFF1A0A2A), 14),
            Triple(0.5f, Color(0xFF0D0221), 9)
        )

        buildingLayers.forEachIndexed { layerIdx, (depth, baseCol, count) ->
            val colW = w / (count - 2)
            val layerShiftX = (-state.cameraX * depth - pX * depth * 0.5f) % colW
            val neonColor = if (layerIdx == 0) Color(0xFF00E5FF) else Color(0xFFFF007F)

            for (i in -1..count) {
                val bX = i * colW + layerShiftX
                val hash = kotlin.math.abs((i * 997 + layerIdx * 331).hashCode())
                val bHeight = 160f + (hash % 260)
                val bTop = horizonY - bHeight + (pY * depth * 0.3f)

                // Building body
                drawRect(
                    color = baseCol.copy(alpha = 0.85f * alpha),
                    topLeft = Offset(bX, bTop),
                    size = Size(colW * 0.82f, h - bTop)
                )

                // Glowing neon roof edge & vertical cyber-strip
                drawLine(
                    color = neonColor.copy(alpha = 0.65f * alpha),
                    start = Offset(bX, bTop),
                    end = Offset(bX + colW * 0.82f, bTop),
                    strokeWidth = 2.5f
                )
                drawLine(
                    color = neonColor.copy(alpha = 0.4f * alpha),
                    start = Offset(bX + colW * 0.4f, bTop),
                    end = Offset(bX + colW * 0.4f, bTop + bHeight * 0.85f),
                    strokeWidth = 1.5f
                )

                // Little glowing windows
                val winCols = 3
                val winRows = 6
                for (r in 0 until winRows) {
                    for (c in 0 until winCols) {
                        if ((hash + r * 7 + c * 3) % 3 != 0) {
                            val wX = bX + 6f + c * (colW * 0.22f)
                            val wY = bTop + 14f + r * 22f
                            drawRect(
                                color = if ((hash + r) % 2 == 0) Color(0xFF00E5FF).copy(alpha = 0.7f * alpha) else Color(0xFFFFD54F).copy(alpha = 0.7f * alpha),
                                topLeft = Offset(wX, wY),
                                size = Size(6f, 10f)
                            )
                        }
                    }
                }
            }
        }

        // Flying aerocar silhouettes moving horizontally with cyan/red light streaks
        val carY1 = h * 0.42f + (pY * 0.15f)
        val carX1 = (w * 0.2f + (state.cameraX * 0.8f)) % (w + 200f) - 100f
        drawLine(
            color = Color(0xFF00E5FF).copy(alpha = 0.75f * alpha),
            start = Offset(carX1 - 40f, carY1),
            end = Offset(carX1, carY1),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round
        )
        drawCircle(
            color = Color.White,
            radius = 3f,
            center = Offset(carX1, carY1)
        )
    }

    // 2. MATRIX REALM
    fun DrawScope.drawMatrixRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Digital phosphorescent cyber-wire grid
        val gridSpacing = 48f
        val shiftX = (-state.cameraX * 0.25f - pX * 0.2f) % gridSpacing
        val shiftY = (alt * 0.25f - pY * 0.2f) % gridSpacing

        var curX = shiftX
        while (curX < w) {
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.12f * alpha),
                start = Offset(curX, 0f),
                end = Offset(curX, h),
                strokeWidth = 1f
            )
            curX += gridSpacing
        }
        var curY = shiftY
        while (curY < h) {
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.12f * alpha),
                start = Offset(0f, curY),
                end = Offset(w, curY),
                strokeWidth = 1f
            )
            curY += gridSpacing
        }

        // Concentric Digital Data Nodes (Glitches & cyberspace rings)
        val nodeX = w * 0.5f - pX * 0.15f
        val nodeY = h * 0.35f - pY * 0.15f
        drawCircle(
            color = Color(0xFF00E676).copy(alpha = 0.25f * alpha),
            radius = 120f,
            center = Offset(nodeX, nodeY),
            style = Stroke(width = 1.5f)
        )
        drawCircle(
            color = Color(0xFF00E676).copy(alpha = 0.18f * alpha),
            radius = 200f,
            center = Offset(nodeX, nodeY),
            style = Stroke(width = 1.5f)
        )
    }

    // 3. MUSHROOMS REALM (Floresta Encantada de Cogumelos Gigantes)
    fun DrawScope.drawMushroomsRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        val groundY = h * 0.82f + (alt * 0.15f - pY * 0.15f)

        // Soft enchanted floor hill
        val hillPath = Path().apply {
            moveTo(0f, groundY)
            quadraticBezierTo(w * 0.35f, groundY - 45f, w * 0.7f, groundY + 15f)
            quadraticBezierTo(w * 0.88f, groundY - 25f, w, groundY)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }
        drawPath(
            hillPath,
            brush = Brush.verticalGradient(
                colors = listOf(Color(0xFF1B0033).copy(alpha = 0.9f * alpha), Color(0xFF0D001A).copy(alpha = 0.95f * alpha)),
                startY = groundY - 50f,
                endY = h
            )
        )

        // Giant Bioluminescent Mushrooms
        val mushroomConfigs = listOf(
            Triple(0.18f * w, groundY - 20f, 65f),
            Triple(0.55f * w, groundY + 10f, 90f),
            Triple(0.82f * w, groundY - 15f, 55f)
        )

        mushroomConfigs.forEachIndexed { idx, (mX, mY, capR) ->
            val shiftMX = mX - (state.cameraX * 0.35f + pX * 0.25f) % (w * 1.2f)
            val wrappedX = if (shiftMX < -capR * 2f) shiftMX + w * 1.4f else if (shiftMX > w + capR * 2f) shiftMX - w * 1.4f else shiftMX

            val capColor = if (idx % 2 == 0) Color(0xFFE040FB) else Color(0xFF00E5FF)
            val stemColor = Color(0xFFEDE7F6).copy(alpha = 0.7f * alpha)

            // Curved mushroom stem
            val stemPath = Path().apply {
                moveTo(wrappedX - capR * 0.15f, mY + 90f)
                quadraticBezierTo(wrappedX - capR * 0.25f, mY + 40f, wrappedX - capR * 0.12f, mY)
                lineTo(wrappedX + capR * 0.12f, mY)
                quadraticBezierTo(wrappedX + capR * 0.2f, mY + 40f, wrappedX + capR * 0.15f, mY + 90f)
                close()
            }
            drawPath(stemPath, color = stemColor)

            // Mushroom cap (Dome with glowing spots)
            val capPath = Path().apply {
                moveTo(wrappedX - capR, mY)
                quadraticBezierTo(wrappedX - capR * 0.8f, mY - capR * 0.9f, wrappedX, mY - capR)
                quadraticBezierTo(wrappedX + capR * 0.8f, mY - capR * 0.9f, wrappedX + capR, mY)
                quadraticBezierTo(wrappedX, mY + capR * 0.25f, wrappedX - capR, mY)
                close()
            }
            drawPath(
                capPath,
                brush = Brush.radialGradient(
                    colors = listOf(capColor.copy(alpha = 0.95f * alpha), capColor.copy(alpha = 0.65f * alpha)),
                    center = Offset(wrappedX, mY - capR * 0.5f),
                    radius = capR
                )
            )

            // Glowing spores under cap
            drawCircle(color = Color.White.copy(alpha = 0.85f * alpha), radius = capR * 0.14f, center = Offset(wrappedX - capR * 0.4f, mY - capR * 0.45f))
            drawCircle(color = Color.White.copy(alpha = 0.85f * alpha), radius = capR * 0.16f, center = Offset(wrappedX + capR * 0.35f, mY - capR * 0.5f))
            drawCircle(color = Color.White.copy(alpha = 0.9f * alpha), radius = capR * 0.18f, center = Offset(wrappedX, mY - capR * 0.7f))

            // Ambient floating bioluminescent spores
            for (s in 0..4) {
                val sporePhase = (state.altitude * 0.05f + s * 2.1f)
                val spX = wrappedX + sin(sporePhase) * (capR * 1.2f)
                val spY = mY - capR * 0.5f - (s * 25f + (state.altitude * 0.2f) % 150f)
                drawCircle(
                    color = capColor.copy(alpha = 0.65f * alpha),
                    radius = 4f + sin(sporePhase) * 2f,
                    center = Offset(spX, spY)
                )
            }
        }
    }

    // 4. CLOUDS SEA REALM (Mar de Nuvens Alvas e Douradas)
    fun DrawScope.drawCloudsSeaRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        val layers = listOf(
            Triple(0.2f, Color(0xFFFFD180), 0.55f),
            Triple(0.45f, Color(0xFFFFAB91), 0.7f),
            Triple(0.75f, Color(0xFFFFF9C4), 0.85f)
        )

        layers.forEachIndexed { idx, (depth, col, baseAlpha) ->
            val cloudY = h * (0.6f + idx * 0.12f) + (alt * depth * 0.2f - pY * depth * 0.15f)
            val waveFreq = 0.008f
            val shiftX = -state.cameraX * depth * 0.5f

            val cloudPath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, cloudY)
                var x = 0f
                while (x <= w + 40f) {
                    val y = cloudY + sin((x + shiftX) * waveFreq + idx) * 35f
                    lineTo(x, y)
                    x += 35f
                }
                lineTo(w, h)
                close()
            }
            drawPath(
                cloudPath,
                brush = Brush.verticalGradient(
                    colors = listOf(col.copy(alpha = baseAlpha * alpha), Color.White.copy(alpha = 0.95f * alpha)),
                    startY = cloudY - 30f,
                    endY = h
                )
            )
        }
    }

    // 5. MOUNTAINS REALM (Montanhas Majestosas com Picos Nevados)
    fun DrawScope.drawMountainsRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Distant alpine range
        val farShiftX = (-state.cameraX * 0.18f - pX * 0.12f)
        val mtnBaseY = h * 0.72f + (alt * 0.18f - pY * 0.15f)

        val farPeaks = listOf(
            Triple(w * 0.1f, mtnBaseY - 260f, 180f),
            Triple(w * 0.45f, mtnBaseY - 340f, 240f),
            Triple(w * 0.85f, mtnBaseY - 280f, 210f)
        )

        farPeaks.forEach { (baseX, peakY, halfW) ->
            val realX = baseX + (farShiftX % (w * 1.2f))
            val mtnPath = Path().apply {
                moveTo(realX - halfW, mtnBaseY)
                lineTo(realX, peakY)
                lineTo(realX + halfW, mtnBaseY)
                close()
            }
            drawPath(mtnPath, color = Color(0xFF263238).copy(alpha = 0.85f * alpha))

            // Snow cap
            val snowCapPath = Path().apply {
                val capH = (mtnBaseY - peakY) * 0.35f
                moveTo(realX - halfW * 0.35f, peakY + capH)
                lineTo(realX, peakY)
                lineTo(realX + halfW * 0.35f, peakY + capH)
                quadraticBezierTo(realX, peakY + capH * 1.2f, realX - halfW * 0.35f, peakY + capH)
                close()
            }
            drawPath(snowCapPath, color = Color.White.copy(alpha = 0.95f * alpha))
        }

        // Near pine tree silhouettes
        val nearShiftX = (-state.cameraX * 0.45f - pX * 0.3f) % 60f
        var treeX = nearShiftX - 30f
        while (treeX < w + 60f) {
            val treeH = 50f + kotlin.math.abs((treeX * 13).hashCode() % 35)
            val treePath = Path().apply {
                moveTo(treeX - 14f, mtnBaseY + 60f)
                lineTo(treeX, mtnBaseY + 60f - treeH)
                lineTo(treeX + 14f, mtnBaseY + 60f)
                close()
            }
            drawPath(treePath, color = Color(0xFF1B2A1E).copy(alpha = 0.9f * alpha))
            treeX += 45f
        }
    }

    // 6. MARS REALM (Dunas Vermelhas e Cânions de Marte)
    fun DrawScope.drawMarsRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Distant Moon Phobos in Martian sky
        val moonX = w * 0.75f - pX * 0.05f
        val moonY = h * 0.22f - pY * 0.05f
        drawCircle(
            color = Color(0xFFD7CCC8).copy(alpha = 0.85f * alpha),
            radius = 28f,
            center = Offset(moonX, moonY)
        )
        drawCircle(
            color = Color(0xFF8D6E63).copy(alpha = 0.5f * alpha),
            radius = 6f,
            center = Offset(moonX - 8f, moonY - 6f)
        )

        // Mars Red Sand Dunes in multi-depth parallax
        val duneLayers = listOf(
            Triple(0.2f, Color(0xFFBF360C), 0.65f),
            Triple(0.45f, Color(0xFFD84315), 0.85f),
            Triple(0.7f, Color(0xFFE64A19), 0.95f)
        )

        duneLayers.forEachIndexed { idx, (depth, color, layerAlpha) ->
            val baseY = h * (0.65f + idx * 0.1f) + (alt * depth * 0.2f - pY * depth * 0.15f)
            val shiftX = -state.cameraX * depth * 0.6f

            val dunePath = Path().apply {
                moveTo(0f, h)
                lineTo(0f, baseY)
                var x = 0f
                while (x <= w + 60f) {
                    val y = baseY + sin((x + shiftX) * 0.006f + idx * 2f) * 45f
                    lineTo(x, y)
                    x += 40f
                }
                lineTo(w, h)
                close()
            }
            drawPath(
                dunePath,
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = layerAlpha * alpha), Color(0xFF3E2723).copy(alpha = 0.95f * alpha)),
                    startY = baseY - 30f,
                    endY = h
                )
            )
        }
    }

    // 7. TEMPEST REALM (Tempestade Noturna com Raios Elétricos)
    fun DrawScope.drawTempestRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Storm clouds billowing
        val cloudBillows = 5
        for (i in 0 until cloudBillows) {
            val bX = (i * (w / 3f) - (state.cameraX * 0.2f) % (w / 3f))
            val bY = h * 0.18f + (i % 2) * 40f - pY * 0.1f
            drawCircle(
                color = Color(0xFF1A153A).copy(alpha = 0.85f * alpha),
                radius = 160f,
                center = Offset(bX, bY)
            )
        }

        // Forked Lightning Flash (Periodic electric discharge)
        val flashPhase = (state.altitude * 0.1f).toInt() % 7
        if (flashPhase == 0) {
            // Flash ambient violet light
            drawRect(
                color = Color(0xFFB388FF).copy(alpha = 0.28f * alpha),
                size = Size(w, h)
            )

            // Forked lightning bolt
            val boltStartX = w * 0.45f - pX * 0.1f
            val boltStartY = 0f
            val boltPath = Path().apply {
                moveTo(boltStartX, boltStartY)
                lineTo(boltStartX + 30f, boltStartY + 90f)
                lineTo(boltStartX - 15f, boltStartY + 140f)
                lineTo(boltStartX + 45f, boltStartY + 240f)
                lineTo(boltStartX + 10f, boltStartY + 310f)
                lineTo(boltStartX + 50f, boltStartY + 420f)
            }
            // Lightning outer glow
            drawPath(
                boltPath,
                color = Color(0xFF00E5FF).copy(alpha = 0.85f * alpha),
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
            // Lightning bright inner core
            drawPath(
                boltPath,
                color = Color.White.copy(alpha = 0.95f * alpha),
                style = Stroke(width = 3f, cap = StrokeCap.Round)
            )
        }
    }

    // 8. ANDROMEDA REALM (Galáxia Espiral Gigante e Poeira Cósmica)
    fun DrawScope.drawAndromedaRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        val center = Offset(w * 0.5f - pX * 0.08f, h * 0.4f - pY * 0.08f)
        val spiralRotation = (state.altitude * 0.08f) % 360f

        // Luminous galactic core
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.95f * alpha),
                    Color(0xFFFF80AB).copy(alpha = 0.65f * alpha),
                    Color(0xFF7C4DFF).copy(alpha = 0.35f * alpha),
                    Color.Transparent
                ),
                center = center,
                radius = 180f
            ),
            radius = 180f,
            center = center
        )

        // 4 Grand spiral arms
        rotate(spiralRotation, pivot = center) {
            val armCount = 4
            for (arm in 0 until armCount) {
                val armAngleOffset = arm * (PI.toFloat() * 2f / armCount)
                val armPath = Path()
                var first = true

                for (step in 0..50) {
                    val t = step / 50f
                    val r = 20f + t * 320f
                    val theta = armAngleOffset + t * 4.5f
                    val x = center.x + cos(theta) * r
                    val y = center.y + sin(theta) * (r * 0.55f) // Elliptical perspective tilt
                    if (first) {
                        armPath.moveTo(x, y)
                        first = false
                    } else {
                        armPath.lineTo(x, y)
                    }
                }

                drawPath(
                    armPath,
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF00E5FF).copy(alpha = 0.75f * alpha), Color(0xFFE040FB).copy(alpha = 0.25f * alpha), Color.Transparent),
                        center = center,
                        radius = 340f
                    ),
                    style = Stroke(width = 16f, cap = StrokeCap.Round)
                )
            }
        }
    }

    // 9. MEDIEVAL REALM (Castelo de Pedra com Tochas e Torres)
    fun DrawScope.drawMedievalRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        val groundY = h * 0.78f + (alt * 0.15f - pY * 0.15f)
        val shiftX = (-state.cameraX * 0.35f - pX * 0.25f) % (w * 1.3f)
        val castleCenter = w * 0.5f + shiftX

        // Central keep & outer towers
        val towers = listOf(
            Triple(castleCenter - 140f, 180f, 60f),
            Triple(castleCenter + 140f, 180f, 60f),
            Triple(castleCenter - 60f, 240f, 70f),
            Triple(castleCenter + 60f, 240f, 70f),
            Triple(castleCenter, 290f, 90f) // Tallest center cathedral spire
        )

        towers.forEach { (tX, tH, tW) ->
            val topY = groundY - tH
            // Tower wall
            drawRect(
                color = Color(0xFF212121).copy(alpha = 0.95f * alpha),
                topLeft = Offset(tX - tW * 0.5f, topY),
                size = Size(tW, tH)
            )

            // Conical tower roof
            val roofPath = Path().apply {
                moveTo(tX - tW * 0.6f, topY)
                lineTo(tX, topY - 70f)
                lineTo(tX + tW * 0.6f, topY)
                close()
            }
            drawPath(roofPath, color = Color(0xFF37474F).copy(alpha = 0.95f * alpha))

            // Flickering warm torchlight on wall
            val torchGlow = 1f + sin(state.altitude * 0.2f + tX) * 0.25f
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD54F).copy(alpha = 0.85f * alpha), Color(0xFFFF6D00).copy(alpha = 0.35f * alpha), Color.Transparent),
                    center = Offset(tX, topY + tH * 0.45f),
                    radius = 24f * torchGlow
                ),
                radius = 24f * torchGlow,
                center = Offset(tX, topY + tH * 0.45f)
            )
        }

        // Fortress base curtain wall
        drawRect(
            color = Color(0xFF1E1E1E).copy(alpha = 0.98f * alpha),
            topLeft = Offset(castleCenter - 220f, groundY - 110f),
            size = Size(440f, 110f)
        )
    }

    // 10. NIGHT FANTASY REALM (Lua Gigante, Lanternas Flutuantes e Salgueiros)
    fun DrawScope.drawNightFantasyRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
        val flyer = state.flyer
        val pX = flyer.x - w * 0.5f
        val pY = flyer.y - h * 0.5f

        // Enormous Silver Glowing Moon
        val moonCenter = Offset(w * 0.5f - pX * 0.05f, h * 0.26f - pY * 0.05f)
        val moonRadius = 110f

        // Moon ambient halo
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFE1F5FE).copy(alpha = 0.45f * alpha),
                    Color(0xFF80D8FF).copy(alpha = 0.18f * alpha),
                    Color.Transparent
                ),
                center = moonCenter,
                radius = moonRadius * 2.2f
            ),
            radius = moonRadius * 2.2f,
            center = moonCenter
        )
        // Silver Moon Disk
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.98f * alpha), Color(0xFFE0F7FA).copy(alpha = 0.92f * alpha)),
                center = moonCenter,
                radius = moonRadius
            ),
            radius = moonRadius,
            center = moonCenter
        )

        // Floating Paper Lanterns gently ascending
        val lanternCount = 8
        for (i in 0 until lanternCount) {
            val hash = kotlin.math.abs((i * 433).hashCode())
            val lX = (i * (w / lanternCount) + (state.cameraX * 0.1f) % (w / lanternCount))
            val ascendY = (h * 0.8f - ((state.altitude * 0.4f + i * 90f) % (h * 0.75f)))
            val lanternCenter = Offset(lX, ascendY)

            // Warm golden glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFFD54F).copy(alpha = 0.75f * alpha), Color(0xFFFF6D00).copy(alpha = 0.25f * alpha), Color.Transparent),
                    center = lanternCenter,
                    radius = 28f
                ),
                radius = 28f,
                center = lanternCenter
            )

            // Paper lantern box
            drawRect(
                color = Color(0xFFFFAB40).copy(alpha = 0.9f * alpha),
                topLeft = Offset(lanternCenter.x - 9f, lanternCenter.y - 12f),
                size = Size(18f, 24f)
            )
            // Inner flame
            drawCircle(
                color = Color.White,
                radius = 3.5f,
                center = lanternCenter
            )
        }
    }
}
