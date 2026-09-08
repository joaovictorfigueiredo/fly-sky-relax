package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import com.example.model.AmbientParticle
import com.example.model.Collectible
import com.example.model.CollectibleType
import com.example.model.DimensionalPortal
import com.example.model.FlyerState
import com.example.model.MagmaEmber
import com.example.model.RainDrop
import com.example.model.Realm
import com.example.model.ShockwaveRing
import com.example.model.Skin
import com.example.model.SparkleParticle
import com.example.model.WingStyle
import com.example.ui.renderers.CosmicEntitiesRenderer.drawBlackHoles
import com.example.ui.renderers.CosmicEntitiesRenderer.drawHyperspaceWarp
import com.example.ui.renderers.CosmicEntitiesRenderer.drawMatrixCodeStreams
import com.example.ui.renderers.CosmicEntitiesRenderer.drawPredators
import com.example.ui.renderers.ExtendedRealmsRenderer.drawAndromedaRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawCloudsSeaRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawCyberpunkRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawMarsRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawMatrixRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawMedievalRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawMountainsRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawMushroomsRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawNightFantasyRealm
import com.example.ui.renderers.ExtendedRealmsRenderer.drawTempestRealm
import com.example.viewmodel.GameUiState
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun GameCanvas(
    uiState: GameUiState,
    collectibles: List<Collectible>,
    sparkles: List<SparkleParticle>,
    shockwaves: List<ShockwaveRing>,
    ambientParticles: List<AmbientParticle> = emptyList(),
    onSizeChanged: (Float, Float) -> Unit,
    onTouchDown: (Offset) -> Unit,
    onTouchMove: (Offset) -> Unit,
    onTouchUp: () -> Unit,
    onSoarStart: () -> Unit,
    onSoarEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .onSizeChanged { size ->
                onSizeChanged(size.width.toFloat(), size.height.toFloat())
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        onTouchDown(offset)
                        onSoarStart()
                        tryAwaitRelease()
                        onTouchUp()
                        onSoarEnd()
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        onTouchDown(offset)
                        onSoarStart()
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        onTouchMove(change.position)
                    },
                    onDragEnd = {
                        onTouchUp()
                        onSoarEnd()
                    },
                    onDragCancel = {
                        onTouchUp()
                        onSoarEnd()
                    }
                )
            }
    ) {
        // 1. Draw dynamic interpolated sky gradient with moving celestial light
        drawSkyBackground(uiState)

        // 2. Draw Realm-specific scenery with multi-depth parallax following the character
        drawRealmScenery(uiState)

        // 3. Draw Ambient atmospheric particles swirling in flight
        drawAmbientParticles(ambientParticles, uiState)

        // 4. Draw Subterranean Magma Embers
        drawMagmaEmbers(uiState.magmaEmbers, uiState)

        // 5. Draw Matrix Digital Code Streams (Active in Matrix dimension)
        drawMatrixCodeStreams(uiState.matrixStreams, uiState)

        // 6. Draw Dimensional Portals
        drawPortals(uiState.activePortal)

        // 7. Draw Cosmic Black Holes
        drawBlackHoles(uiState.activeBlackHoles)

        // 8. Draw Collectibles (Lights, Bubbles & Flowers)
        drawCollectibles(collectibles)

        // 9. Draw Predators (Cute companions & Wild creatures)
        drawPredators(uiState.activePredators)

        // 10. Draw Visual Feedback (Shockwaves & Sparkles)
        drawShockwaves(shockwaves)
        drawSparkles(sparkles)

        // 11. Draw Flyer (Silk trail, luminous wings, radiant core styled by equipped skin)
        drawFlyer(uiState.flyer, uiState.equippedSkin)

        // 12. Draw Atmospheric Rain
        drawRain(uiState.rainDrops, uiState)

        // 13. Draw Light-Speed Hyperspace Animation Warp Overlay
        if (uiState.isHyperspaceActive) {
            drawHyperspaceWarp(uiState.hyperspaceProgress)
        }

        // 14. Draw Soft Touch Guidance Reticle (Soft starlight beam & pulse when touching/steering)
        uiState.targetOffset?.let { target ->
            drawTouchGuidance(
                center = Offset(uiState.flyer.x, uiState.flyer.y),
                target = target,
                color = uiState.equippedSkin.auraColor
            )
        }
    }
}

private fun DrawScope.drawSkyBackground(state: GameUiState) {
    val current = state.currentRealm
    val next = state.nextRealm
    val alpha = state.transitionAlpha.coerceIn(0f, 1f)

    // Blend top and bottom colors smoothly
    val topColor = lerpColor(current.skyTopColor, next.skyTopColor, alpha)
    val bottomColor = lerpColor(current.skyBottomColor, next.skyBottomColor, alpha)

    val w = size.width
    val h = size.height
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    // Base sky gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor)
        ),
        size = size
    )

    // Dynamic atmospheric celestial glow (sun / cosmic beacon) that moves with camera perspective
    val celestialX = w * 0.5f - playerRelX * 0.16f - ((state.cameraX * 0.04f) % (w * 0.4f))
    val celestialY = h * 0.28f - playerRelY * 0.12f + ((state.altitude * 0.03f) % (h * 0.25f))
    val glowColor = lerpColor(current.accentColor, next.accentColor, alpha)

    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                glowColor.copy(alpha = 0.28f),
                glowColor.copy(alpha = 0.1f),
                Color.Transparent
            ),
            center = Offset(celestialX, celestialY),
            radius = w * 0.72f
        ),
        radius = w * 0.72f,
        center = Offset(celestialX, celestialY)
    )
}

private fun DrawScope.drawRealmScenery(state: GameUiState) {
    val alt = state.altitude
    val w = size.width
    val h = size.height

    // 1. Earth Core (-3800m to -2400m)
    if (alt < -2400f) {
        val coreFade = if (alt < -2800f) 1f else ((-2400f - alt) / 400f).coerceIn(0f, 1f)
        if (coreFade > 0f) drawEarthCore(w, h, alt, coreFade, state)
    }

    // 2. Magma Layer (-3200m to -1600m)
    if (alt in -3200f..-1600f) {
        val magmaFade = when {
            alt < -2800f -> ((alt - (-3200f)) / 400f).coerceIn(0f, 1f)
            alt > -2000f -> ((-1600f - alt) / 400f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (magmaFade > 0f) drawMagmaRealm(w, h, alt, magmaFade, state)
    }

    // 3. Earth Mantle (-2200m to -600m)
    if (alt in -2200f..-600f) {
        val mantleFade = when {
            alt < -1800f -> ((alt - (-2200f)) / 400f).coerceIn(0f, 1f)
            alt > -1000f -> ((-600f - alt) / 400f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (mantleFade > 0f) drawEarthMantle(w, h, alt, mantleFade, state)
    }

    // 4. Earth Crust / Geode Caverns (-1200m to 300m)
    if (alt in -1200f..300f) {
        val crustFade = when {
            alt < -800f -> ((alt - (-1200f)) / 400f).coerceIn(0f, 1f)
            alt > 0f -> ((300f - alt) / 300f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (crustFade > 0f) drawEarthCrust(w, h, alt, crustFade, state)
    }

    // 5. Trees & Forest Canopy (-100m to 1400m)
    if (alt in -100f..1400f) {
        val forestFade = when {
            alt < 100f -> ((alt - (-100f)) / 200f).coerceIn(0f, 1f)
            alt > 800f -> (1f - (alt - 800f) / 600f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (forestFade > 0f) {
            drawForestCanopy(w, h, alt, forestFade, state)
        }
    }

    // 6. Outer Space (900m - 2700m)
    if (alt in 900f..2700f) {
        val spaceFade = when {
            alt < 1300f -> ((alt - 900f) / 400f).coerceIn(0f, 1f)
            alt > 2300f -> (1f - (alt - 2300f) / 400f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (spaceFade > 0f) {
            drawDeepSpace(w, h, alt, spaceFade, state)
        }
    }

    // 7. Planets & Galaxies (2100m - 3900m)
    if (alt in 2100f..3900f) {
        val planetFade = when {
            alt < 2500f -> ((alt - 2100f) / 400f).coerceIn(0f, 1f)
            alt > 3500f -> (1f - (alt - 3500f) / 400f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (planetFade > 0f) {
            drawPlanetsAndGalaxies(w, h, alt, planetFade, state)
        }
    }

    // 8. Celestial Clouds Realm (3200m - 5200m)
    if (alt in 3200f..5200f) {
        val cloudFade = when {
            alt < 3600f -> ((alt - 3200f) / 400f).coerceIn(0f, 1f)
            alt > 4700f -> ((5200f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (cloudFade > 0f) {
            drawCelestialCloudRealm(w, h, alt, cloudFade, state)
        }
    }

    // 9. Sunset City Dimension (4600m - 6800m)
    if (alt in 4600f..6800f || state.currentRealm == Realm.SUNSET_CITY) {
        val cityFade = when {
            state.currentRealm == Realm.SUNSET_CITY -> 1f
            alt < 5000f -> ((alt - 4600f) / 400f).coerceIn(0f, 1f)
            alt > 6300f -> ((6800f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (cityFade > 0f) drawSunsetCityRealm(w, h, alt, cityFade, state)
    }

    // 10. Cyberpunk Dimension (6300m - 8300m)
    if (alt in 6300f..8300f || state.currentRealm == Realm.CYBERPUNK) {
        val cyberFade = when {
            state.currentRealm == Realm.CYBERPUNK -> 1f
            alt < 6700f -> ((alt - 6300f) / 400f).coerceIn(0f, 1f)
            alt > 7800f -> ((8300f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (cyberFade > 0f) drawCyberpunkRealm(w, h, alt, cyberFade, state)
    }

    // 11. Matrix Dimension (7800m - 9800m)
    if (alt in 7800f..9800f || state.currentRealm == Realm.MATRIX) {
        val matFade = when {
            state.currentRealm == Realm.MATRIX -> 1f
            alt < 8200f -> ((alt - 7800f) / 400f).coerceIn(0f, 1f)
            alt > 9300f -> ((9800f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (matFade > 0f) drawMatrixRealm(w, h, alt, matFade, state)
    }

    // 12. Mushrooms Forest (9300m - 11300m)
    if (alt in 9300f..11300f || state.currentRealm == Realm.MUSHROOMS) {
        val mushFade = when {
            state.currentRealm == Realm.MUSHROOMS -> 1f
            alt < 9700f -> ((alt - 9300f) / 400f).coerceIn(0f, 1f)
            alt > 10800f -> ((11300f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (mushFade > 0f) drawMushroomsRealm(w, h, alt, mushFade, state)
    }

    // 13. Clouds Sea (10800m - 12800m)
    if (alt in 10800f..12800f || state.currentRealm == Realm.CLOUDS_SEA) {
        val seaFade = when {
            state.currentRealm == Realm.CLOUDS_SEA -> 1f
            alt < 11200f -> ((alt - 10800f) / 400f).coerceIn(0f, 1f)
            alt > 12300f -> ((12800f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (seaFade > 0f) drawCloudsSeaRealm(w, h, alt, seaFade, state)
    }

    // 14. Alpine Mountains (12300m - 14300m)
    if (alt in 12300f..14300f || state.currentRealm == Realm.MOUNTAINS) {
        val mtnFade = when {
            state.currentRealm == Realm.MOUNTAINS -> 1f
            alt < 12700f -> ((alt - 12300f) / 400f).coerceIn(0f, 1f)
            alt > 13800f -> ((14300f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (mtnFade > 0f) drawMountainsRealm(w, h, alt, mtnFade, state)
    }

    // 15. Mars Canyons & Dunes (13800m - 15800m)
    if (alt in 13800f..15800f || state.currentRealm == Realm.MARS) {
        val marsFade = when {
            state.currentRealm == Realm.MARS -> 1f
            alt < 14200f -> ((alt - 13800f) / 400f).coerceIn(0f, 1f)
            alt > 15300f -> ((15800f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (marsFade > 0f) drawMarsRealm(w, h, alt, marsFade, state)
    }

    // 16. Tempest Lightning Storm (15300m - 17300m)
    if (alt in 15300f..17300f || state.currentRealm == Realm.TEMPEST) {
        val stormFade = when {
            state.currentRealm == Realm.TEMPEST -> 1f
            alt < 15700f -> ((alt - 15300f) / 400f).coerceIn(0f, 1f)
            alt > 16800f -> ((17300f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (stormFade > 0f) drawTempestRealm(w, h, alt, stormFade, state)
    }

    // 17. Andromeda Spiral Galaxy (16800m - 18800m)
    if (alt in 16800f..18800f || state.currentRealm == Realm.ANDROMEDA) {
        val androFade = when {
            state.currentRealm == Realm.ANDROMEDA -> 1f
            alt < 17200f -> ((alt - 16800f) / 400f).coerceIn(0f, 1f)
            alt > 18300f -> ((18800f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (androFade > 0f) drawAndromedaRealm(w, h, alt, androFade, state)
    }

    // 18. Medieval Castle (18300m - 20300m)
    if (alt in 18300f..20300f || state.currentRealm == Realm.MEDIEVAL) {
        val medFade = when {
            state.currentRealm == Realm.MEDIEVAL -> 1f
            alt < 18700f -> ((alt - 18300f) / 400f).coerceIn(0f, 1f)
            alt > 19800f -> ((20300f - alt) / 500f).coerceIn(0f, 1f)
            else -> 1f
        }
        if (medFade > 0f) drawMedievalRealm(w, h, alt, medFade, state)
    }

    // 19. Night Fantasy Moon & Lanterns (alt > 19800m or when in NIGHT_FANTASY)
    if (alt > 19800f || state.currentRealm == Realm.NIGHT_FANTASY) {
        val fantFade = if (state.currentRealm == Realm.NIGHT_FANTASY) 1f else ((alt - 19800f) / 400f).coerceIn(0f, 1f)
        drawNightFantasyRealm(w, h, alt, fantFade, state)
    }
}

/**
 * Forest Canopy Layer: Silhouettes of gentle ancient trees, trunks, and pastel foliage
 * that follow the flyer horizontally and vertically in multi-layered parallax.
 */
private fun DrawScope.drawForestCanopy(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    // Parallax depth calculations
    val farX = -state.cameraX * 0.12f - playerRelX * 0.08f
    val farY = (alt * 0.35f - playerRelY * 0.08f)

    val midX = -state.cameraX * 0.35f - playerRelX * 0.22f
    val midY = (alt * 0.55f - playerRelY * 0.2f)

    val nearX = -state.cameraX * 0.7f - playerRelX * 0.45f
    val nearY = (alt * 0.8f - playerRelY * 0.35f)

    // 1. Distant rolling mountain / mist canopy horizon (far parallax)
    val farPeriod = w * 1.3f
    val farShift = (farX % farPeriod + farPeriod) % farPeriod - farPeriod
    val distantPath = Path().apply {
        moveTo(farShift, h)
        var cx = farShift
        val step = farPeriod / 4f
        lineTo(cx, h * 0.46f + farY)
        while (cx <= w + farPeriod * 2f) {
            val nx = cx + step
            val waveDip = if (((cx - farShift) / step).toInt() % 2 == 0) -h * 0.07f else h * 0.06f
            cubicTo(
                cx + step * 0.35f, h * 0.46f + farY + waveDip,
                cx + step * 0.65f, h * 0.46f + farY + waveDip,
                nx, h * 0.46f + farY
            )
            cx = nx
        }
        lineTo(w + farPeriod * 2f, h)
        close()
    }
    drawPath(
        path = distantPath,
        color = Color(0xFF6E9882).copy(alpha = 0.45f * alpha)
    )

    // 2. Midground giant ancient tree canopies (mid parallax)
    val midPeriod = w * 1.1f
    val midShift = (midX % midPeriod + midPeriod) % midPeriod - midPeriod
    val midTreePath = Path().apply {
        moveTo(midShift, h)
        var cx = midShift
        val step = midPeriod / 4f
        lineTo(cx, h * 0.64f + midY)
        while (cx <= w + midPeriod * 2f) {
            val nx = cx + step
            val waveDip = if (((cx - midShift) / step).toInt() % 2 == 0) -h * 0.09f else h * 0.08f
            cubicTo(
                cx + step * 0.3f, h * 0.64f + midY + waveDip,
                cx + step * 0.7f, h * 0.64f + midY + waveDip,
                nx, h * 0.64f + midY
            )
            cx = nx
        }
        lineTo(w + midPeriod * 2f, h)
        close()
    }
    drawPath(
        path = midTreePath,
        color = Color(0xFF4F7A65).copy(alpha = 0.55f * alpha)
    )

    // 3. Near ancient tree trunks and lush crowns (near parallax)
    val trunkPeriod = w * 0.9f
    val trunkShift = (nearX % trunkPeriod + trunkPeriod) % trunkPeriod - trunkPeriod * 0.5f
    for (i in -1..2) {
        val tx = trunkShift + i * trunkPeriod
        drawCircle(
            color = Color(0xFF5A8570).copy(alpha = 0.4f * alpha),
            radius = w * 0.32f,
            center = Offset(tx, h * 0.76f + nearY)
        )
        drawCircle(
            color = Color(0xFF436955).copy(alpha = 0.35f * alpha),
            radius = w * 0.22f,
            center = Offset(tx + w * 0.12f, h * 0.8f + nearY)
        )
    }
}

// Multi-depth cosmic stars data for space parallax
private data class CosmicStar(
    val relX: Float,
    val relY: Float,
    val size: Float,
    val depth: Float,
    val pulseSpeed: Float
)

private val cosmicStars = List(65) { index ->
    CosmicStar(
        relX = ((index * 137.5f) % 1000f) / 1000f,
        relY = ((index * 293.7f) % 1000f) / 1000f,
        size = 1.5f + (index % 4) * 0.9f,
        depth = when (index % 3) {
            0 -> 0.09f // Distant stars (slow drift)
            1 -> 0.25f // Midfield stars (medium drift)
            else -> 0.48f // Near stars (rapid drift)
        },
        pulseSpeed = 1.2f + (index % 5) * 0.7f
    )
}

/**
 * Deep Space Layer: Twinkling pastel stars and soft stardust nebulae
 * that drift smoothly in 3D parallax as the flyer moves across space.
 */
private fun DrawScope.drawDeepSpace(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    // Soft swirling stardust nebulae (depth 0.12 and 0.2)
    val neb1X = ((w * 0.35f - state.cameraX * 0.12f - playerRelX * 0.08f) % w + w) % w
    val neb1Y = ((h * 0.32f - state.cameraY * 0.1f + alt * 0.2f - playerRelY * 0.08f) % h + h) % h
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF8E7AB5).copy(alpha = 0.35f * alpha),
                Color(0xFF5C6BC0).copy(alpha = 0.18f * alpha),
                Color.Transparent
            ),
            center = Offset(neb1X, neb1Y),
            radius = w * 0.6f
        ),
        center = Offset(neb1X, neb1Y),
        radius = w * 0.6f
    )

    val neb2X = ((w * 0.75f - state.cameraX * 0.18f - playerRelX * 0.12f) % w + w) % w
    val neb2Y = ((h * 0.7f - state.cameraY * 0.15f + alt * 0.3f - playerRelY * 0.12f) % h + h) % h
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF4DD0E1).copy(alpha = 0.22f * alpha),
                Color(0xFFBA68C8).copy(alpha = 0.14f * alpha),
                Color.Transparent
            ),
            center = Offset(neb2X, neb2Y),
            radius = w * 0.48f
        ),
        center = Offset(neb2X, neb2Y),
        radius = w * 0.48f
    )

    // Multi-depth Starfields (Far, Mid, Near)
    val flyerSpeed = hypot(flyer.vx, flyer.vy)
    cosmicStars.forEach { star ->
        val depthFactor = star.depth
        val starX = ((star.relX * w - state.cameraX * depthFactor - playerRelX * (depthFactor * 0.7f)) % w + w) % w
        val starY = ((star.relY * h - state.cameraY * depthFactor + alt * (depthFactor * 1.2f) - playerRelY * (depthFactor * 0.7f)) % h + h) % h

        val twinkle = 0.7f + sin(alt * 0.02f * star.pulseSpeed + star.relX * 10f) * 0.3f
        val starAlpha = (twinkle * alpha).coerceIn(0f, 1f)

        // Near stars stretch into soft celestial trails if flying fast
        if (star.depth > 0.35f && flyerSpeed > 180f) {
            val tailLen = (flyerSpeed / 600f * 10f).coerceIn(2f, 14f)
            val angleRad = Math.toRadians(flyer.angleDeg.toDouble())
            val dx = cos(angleRad).toFloat() * tailLen
            val dy = sin(angleRad).toFloat() * tailLen
            drawLine(
                color = Color(0xFFE1F5FE).copy(alpha = starAlpha * 0.85f),
                start = Offset(starX - dx, starY - dy),
                end = Offset(starX, starY),
                strokeWidth = star.size * 0.8f,
                cap = StrokeCap.Round
            )
        } else {
            drawCircle(
                color = Color(0xFFE1F5FE).copy(alpha = starAlpha * 0.85f),
                radius = star.size,
                center = Offset(starX, starY)
            )
        }
    }
}

/**
 * Planets & Galaxies Layer: Ringed pastel planet, spinning spiral galaxy, and moonlet
 * that move in realistic cosmic perspective relative to player flight.
 */
private fun DrawScope.drawPlanetsAndGalaxies(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    // 1. Distant Spiral Galaxy (Depth 0.1)
    val wrapGW = w * 1.8f
    val wrapGH = h * 1.4f
    val gx = ((w * 0.25f - state.cameraX * 0.1f - playerRelX * 0.08f) % wrapGW + wrapGW) % wrapGW - w * 0.4f
    val gy = ((h * 0.65f - state.cameraY * 0.08f + alt * 0.15f - playerRelY * 0.08f) % wrapGH + wrapGH) % wrapGH - h * 0.2f
    val galaxyCenter = Offset(gx, gy)

    rotate(alt * 0.05f + flyer.angleDeg * 0.04f, pivot = galaxyCenter) {
        for (arm in 0..2) {
            val armOffset = arm * 120f
            rotate(armOffset, pivot = galaxyCenter) {
                drawOval(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF80DEEA).copy(alpha = 0.55f * alpha),
                            Color(0xFFE1BEE7).copy(alpha = 0.32f * alpha),
                            Color.Transparent
                        ),
                        center = galaxyCenter,
                        radius = w * 0.26f
                    ),
                    topLeft = Offset(galaxyCenter.x - w * 0.24f, galaxyCenter.y - w * 0.1f),
                    size = Size(w * 0.48f, w * 0.2f)
                )
            }
        }
    }

    // 2. Giant Ringed Pastel Planet (Peach & Lavender) (Depth 0.25)
    val wrapPW = w * 1.8f
    val wrapPH = h * 1.5f
    val px = ((w * 0.75f - state.cameraX * 0.25f - playerRelX * 0.18f) % wrapPW + wrapPW) % wrapPW - w * 0.4f
    val py = ((h * 0.28f - state.cameraY * 0.18f + alt * 0.22f - playerRelY * 0.18f) % wrapPH + wrapPH) % wrapPH - h * 0.25f
    val planetCenter = Offset(px, py)
    val planetRadius = w * 0.18f

    // Planet atmosphere glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFCCBC).copy(alpha = 0.52f * alpha),
                Color(0xFFCE93D8).copy(alpha = 0.35f * alpha),
                Color.Transparent
            ),
            center = planetCenter,
            radius = planetRadius * 1.6f
        ),
        radius = planetRadius * 1.6f,
        center = planetCenter
    )

    // Planet body
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFAB91).copy(alpha = 0.92f * alpha),
                Color(0xFFB39DDB).copy(alpha = 0.92f * alpha)
            ),
            start = Offset(planetCenter.x - planetRadius, planetCenter.y - planetRadius),
            end = Offset(planetCenter.x + planetRadius, planetCenter.y + planetRadius)
        ),
        radius = planetRadius,
        center = planetCenter
    )

    // Planet Ring (Tilted ellipse reacting to flyer banking)
    val ringAngle = 25f + (flyer.angleDeg * 0.05f)
    rotate(ringAngle, pivot = planetCenter) {
        drawOval(
            color = Color(0xFFFFE082).copy(alpha = 0.65f * alpha),
            topLeft = Offset(planetCenter.x - planetRadius * 1.8f, planetCenter.y - planetRadius * 0.35f),
            size = Size(planetRadius * 3.6f, planetRadius * 0.7f),
            style = Stroke(width = 8f)
        )
    }

    // Orbiting miniature pastel moonlet
    val moonAngle = (alt * 0.015f).toDouble()
    val moonX = planetCenter.x + cos(moonAngle).toFloat() * planetRadius * 2.2f
    val moonY = planetCenter.y + sin(moonAngle).toFloat() * planetRadius * 0.7f
    drawCircle(
        color = Color(0xFFE0F7FA).copy(alpha = 0.85f * alpha),
        radius = 7.5f,
        center = Offset(moonX, moonY)
    )
}

/**
 * Celestial Cloud Realm Layer: Dreamy golden-pink fluffy cloud masses and volumetric sunbeams
 * shifting and rolling in multi-depth parallax as the flyer soars.
 */
private fun DrawScope.drawCelestialCloudRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    // Volumetric Celestial Rays tilting with character flight
    val rayShiftX = (-state.cameraX * 0.08f - playerRelX * 0.06f)
    val rayTilt = (flyer.angleDeg * 0.04f).coerceIn(-10f, 10f)

    rotate(rayTilt, pivot = Offset(w * 0.5f, 0f)) {
        for (i in -1..5) {
            val baseBeamX = w * (0.15f + i * 0.22f)
            val beamX = ((baseBeamX + rayShiftX) % (w * 1.4f) + (w * 1.4f)) % (w * 1.4f) - w * 0.2f
            val beamPath = Path().apply {
                moveTo(beamX, 0f)
                lineTo(beamX + w * 0.22f, h)
                lineTo(beamX + w * 0.38f, h)
                lineTo(beamX + w * 0.06f, 0f)
                close()
            }
            drawPath(
                path = beamPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF9C4).copy(alpha = 0.24f * alpha),
                        Color(0xFFFFECB3).copy(alpha = 0.09f * alpha),
                        Color.Transparent
                    )
                )
            )
        }
    }

    // Layered Celestial Clouds that roll and wrap continuously
    val wrapCW = w * 1.5f
    val wrapCH = h * 1.3f

    // Cloud Layer 1 (Deep golden dawn, depth 0.2)
    val c1x = ((w * 0.2f - state.cameraX * 0.2f) % wrapCW + wrapCW) % wrapCW - w * 0.3f
    val c1y = ((h * 0.72f - state.cameraY * 0.2f + alt * 0.15f) % wrapCH + wrapCH) % wrapCH - h * 0.2f
    drawCloudBank(c1x, c1y, w * 0.62f, Color(0xFFFFF3E0), 0.7f * alpha)

    // Cloud Layer 2 (Mid peach cumulus, depth 0.45)
    val c2x = ((w * 0.72f - state.cameraX * 0.45f) % wrapCW + wrapCW) % wrapCW - w * 0.3f
    val c2y = ((h * 0.52f - state.cameraY * 0.45f + alt * 0.28f) % wrapCH + wrapCH) % wrapCH - h * 0.2f
    drawCloudBank(c2x, c2y, w * 0.56f, Color(0xFFF8BBD0), 0.65f * alpha)

    // Cloud Layer 3 (Foreground golden cloud bank, depth 0.75)
    val c3x = ((w * 0.12f - state.cameraX * 0.75f) % wrapCW + wrapCW) % wrapCW - w * 0.35f
    val c3y = ((h * 0.32f - state.cameraY * 0.75f + alt * 0.42f) % wrapCH + wrapCH) % wrapCH - h * 0.25f
    drawCloudBank(c3x, c3y, w * 0.68f, Color(0xFFFFE0B2), 0.78f * alpha)
}

private fun DrawScope.drawCloudBank(centerX: Float, centerY: Float, baseRadius: Float, color: Color, alpha: Float) {
    drawCircle(
        color = color.copy(alpha = alpha),
        radius = baseRadius * 0.45f,
        center = Offset(centerX, centerY)
    )
    drawCircle(
        color = color.copy(alpha = alpha * 0.85f),
        radius = baseRadius * 0.38f,
        center = Offset(centerX - baseRadius * 0.35f, centerY + baseRadius * 0.08f)
    )
    drawCircle(
        color = color.copy(alpha = alpha * 0.85f),
        radius = baseRadius * 0.42f,
        center = Offset(centerX + baseRadius * 0.35f, centerY + baseRadius * 0.05f)
    )
}

private fun DrawScope.drawAmbientParticles(
    ambientParticles: List<AmbientParticle>,
    state: GameUiState
) {
    if (ambientParticles.isEmpty()) return
    val realm = state.currentRealm
    val accent = realm.accentColor

    ambientParticles.forEach { p ->
        val pulse = 0.7f + sin(p.phase) * 0.3f
        val particleRadius = p.size * pulse
        val pAlpha = (p.alpha * pulse).coerceIn(0.1f, 0.95f)

        // Soft outer halo
        drawCircle(
            color = accent.copy(alpha = pAlpha * 0.35f),
            radius = particleRadius * 2.2f,
            center = Offset(p.x, p.y)
        )
        // Radiant center core
        drawCircle(
            color = Color.White.copy(alpha = pAlpha * 0.85f),
            radius = particleRadius,
            center = Offset(p.x, p.y)
        )
    }
}

private fun DrawScope.drawCollectibles(collectibles: List<Collectible>) {
    collectibles.forEach { item ->
        val pulse = 1f + sin(item.phase) * 0.12f
        val currentRadius = item.baseRadius * pulse

        when (item.type) {
            CollectibleType.LIGHT -> {
                // Outer soft luminous halo
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            item.color.copy(alpha = 0.65f),
                            item.color.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = Offset(item.x, item.y),
                        radius = currentRadius * 2.8f
                    ),
                    radius = currentRadius * 2.8f,
                    center = Offset(item.x, item.y)
                )
                // Radiant core
                drawCircle(
                    color = Color.White.copy(alpha = 0.95f),
                    radius = currentRadius * 0.75f,
                    center = Offset(item.x, item.y)
                )
                // 4-Pointed sparkle glint
                val glintLen = currentRadius * 1.6f
                rotate(item.phase * 20f, pivot = Offset(item.x, item.y)) {
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(item.x - glintLen, item.y),
                        end = Offset(item.x + glintLen, item.y),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.8f),
                        start = Offset(item.x, item.y - glintLen),
                        end = Offset(item.x, item.y + glintLen),
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }
            }
            CollectibleType.BUBBLE -> {
                // Iridescent bubble body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            item.color.copy(alpha = 0.25f),
                            item.color.copy(alpha = 0.45f),
                            Color.White.copy(alpha = 0.65f)
                        ),
                        center = Offset(item.x - currentRadius * 0.3f, item.y - currentRadius * 0.3f),
                        radius = currentRadius * 1.1f
                    ),
                    radius = currentRadius,
                    center = Offset(item.x, item.y)
                )
                // Bubble delicate rim
                drawCircle(
                    color = Color.White.copy(alpha = 0.75f),
                    radius = currentRadius,
                    center = Offset(item.x, item.y),
                    style = Stroke(width = 2.5f)
                )
                // Specular reflection highlight
                drawCircle(
                    color = Color.White.copy(alpha = 0.9f),
                    radius = currentRadius * 0.25f,
                    center = Offset(item.x - currentRadius * 0.38f, item.y - currentRadius * 0.38f)
                )
            }
            CollectibleType.FLOWER -> {
                // Outer soft floral aura
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            item.color.copy(alpha = 0.55f),
                            item.color.copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        center = Offset(item.x, item.y),
                        radius = currentRadius * 2.4f
                    ),
                    radius = currentRadius * 2.4f,
                    center = Offset(item.x, item.y)
                )
                // 5 Blossom Petals
                for (p in 0 until 5) {
                    val petalAngle = item.phase * 0.8f + p * (2 * PI / 5f).toFloat()
                    val px = item.x + cos(petalAngle) * currentRadius * 0.65f
                    val py = item.y + sin(petalAngle) * currentRadius * 0.65f
                    drawCircle(
                        color = item.color.copy(alpha = 0.92f),
                        radius = currentRadius * 0.5f,
                        center = Offset(px, py)
                    )
                }
                // Golden Pistil Core
                drawCircle(
                    color = Color(0xFFFFF59D),
                    radius = currentRadius * 0.38f,
                    center = Offset(item.x, item.y)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.95f),
                    radius = currentRadius * 0.18f,
                    center = Offset(item.x, item.y)
                )
            }
        }
    }
}

private fun DrawScope.drawShockwaves(shockwaves: List<ShockwaveRing>) {
    shockwaves.forEach { ring ->
        drawCircle(
            color = ring.color.copy(alpha = ring.alpha),
            radius = ring.currentRadius,
            center = Offset(ring.x, ring.y),
            style = Stroke(width = 4.5f * (1f - ring.currentRadius / ring.maxRadius).coerceAtLeast(0.2f))
        )
    }
}

private fun DrawScope.drawSparkles(sparkles: List<SparkleParticle>) {
    sparkles.forEach { p ->
        val lifeAlpha = (1f - (p.life / p.maxLife)).coerceIn(0f, 1f)
        val pSize = p.size * lifeAlpha

        // Diamond star sparkle
        val path = Path().apply {
            moveTo(p.x, p.y - pSize * 1.8f)
            lineTo(p.x + pSize, p.y)
            lineTo(p.x, p.y + pSize * 1.8f)
            lineTo(p.x - pSize, p.y)
            close()
        }
        drawPath(
            path = path,
            color = p.color.copy(alpha = lifeAlpha)
        )
    }
}

private fun DrawScope.drawFlyer(flyer: FlyerState, skin: Skin) {
    val center = Offset(flyer.x, flyer.y)

    // 1. Silk Ribbon Trail
    if (flyer.trail.size > 2) {
        val trailPath = Path()
        trailPath.moveTo(flyer.trail[0].x, flyer.trail[0].y)
        for (i in 1 until flyer.trail.size) {
            val p = flyer.trail[i]
            trailPath.lineTo(p.x, p.y)
        }
        drawPath(
            path = trailPath,
            color = skin.trailColor.copy(alpha = 0.4f * flyer.glowIntensity.coerceAtMost(1.6f)),
            style = Stroke(width = 7.5f, cap = StrokeCap.Round)
        )
    }

    // 2. Luminous Flyer Aura
    val auraRadius = 40f * flyer.glowIntensity
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                skin.primaryColor.copy(alpha = 0.7f),
                skin.auraColor.copy(alpha = 0.38f),
                Color.Transparent
            ),
            center = center,
            radius = auraRadius
        ),
        radius = auraRadius,
        center = center
    )

    // 3. Flyer Body & Wings with Flight Rotation
    rotate(flyer.angleDeg, pivot = center) {
        val wingSpread = cos(flyer.wingPhase) * 22f // Wing flap flapping

        when (skin.wingStyle) {
            WingStyle.BUTTERFLY -> {
                // Soft dual-lobed rounded butterfly wings
                val leftWing = Path().apply {
                    moveTo(center.x - 2f, center.y - 2f)
                    cubicTo(
                        center.x - 14f, center.y - 36f + wingSpread,
                        center.x - 38f, center.y - 26f + wingSpread,
                        center.x - 24f, center.y - 4f
                    )
                    cubicTo(
                        center.x - 34f, center.y - 2f,
                        center.x - 26f, center.y + 14f,
                        center.x - 4f, center.y + 4f
                    )
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x - 2f, center.y + 2f)
                    cubicTo(
                        center.x - 14f, center.y + 36f - wingSpread,
                        center.x - 38f, center.y + 26f - wingSpread,
                        center.x - 24f, center.y + 4f
                    )
                    cubicTo(
                        center.x - 34f, center.y + 2f,
                        center.x - 26f, center.y - 14f,
                        center.x - 4f, center.y - 4f
                    )
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.92f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.92f))
            }
            WingStyle.MANTA_GLIDE -> {
                // Sweeping diamond manta wings
                val mantaWings = Path().apply {
                    moveTo(center.x + 12f, center.y)
                    cubicTo(
                        center.x - 8f, center.y - 40f + wingSpread * 1.2f,
                        center.x - 32f, center.y - 30f + wingSpread * 0.8f,
                        center.x - 26f, center.y
                    )
                    cubicTo(
                        center.x - 32f, center.y + 30f - wingSpread * 0.8f,
                        center.x - 8f, center.y + 40f - wingSpread * 1.2f,
                        center.x + 12f, center.y
                    )
                    close()
                }
                drawPath(path = mantaWings, color = skin.wingColor.copy(alpha = 0.9f))
            }
            WingStyle.STAR_CREST -> {
                // Geometric crystalline radiant star wings
                val leftWing = Path().apply {
                    moveTo(center.x - 4f, center.y - 3f)
                    lineTo(center.x - 18f, center.y - 38f + wingSpread)
                    lineTo(center.x - 26f, center.y - 18f + wingSpread * 0.5f)
                    lineTo(center.x - 38f, center.y - 24f + wingSpread)
                    lineTo(center.x - 28f, center.y + 2f)
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x - 4f, center.y + 3f)
                    lineTo(center.x - 18f, center.y + 38f - wingSpread)
                    lineTo(center.x - 26f, center.y + 18f - wingSpread * 0.5f)
                    lineTo(center.x - 38f, center.y + 24f - wingSpread)
                    lineTo(center.x - 28f, center.y - 2f)
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.94f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.94f))
            }
            WingStyle.MAGMA_FLAME -> {
                // Flaring incandescent magma flame wings with flickering tongues
                val leftWing = Path().apply {
                    moveTo(center.x - 2f, center.y - 2f)
                    lineTo(center.x - 14f, center.y - 42f + wingSpread * 1.3f)
                    lineTo(center.x - 22f, center.y - 24f + wingSpread * 0.7f)
                    lineTo(center.x - 34f, center.y - 36f + wingSpread * 1.1f)
                    lineTo(center.x - 30f, center.y - 14f + wingSpread * 0.4f)
                    lineTo(center.x - 42f, center.y - 20f + wingSpread * 0.8f)
                    lineTo(center.x - 26f, center.y + 2f)
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x - 2f, center.y + 2f)
                    lineTo(center.x - 14f, center.y + 42f - wingSpread * 1.3f)
                    lineTo(center.x - 22f, center.y + 24f - wingSpread * 0.7f)
                    lineTo(center.x - 34f, center.y + 36f - wingSpread * 1.1f)
                    lineTo(center.x - 30f, center.y + 14f - wingSpread * 0.4f)
                    lineTo(center.x - 42f, center.y + 20f - wingSpread * 0.8f)
                    lineTo(center.x - 26f, center.y - 2f)
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.95f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.95f))
            }
            WingStyle.GEODE_CRYSTAL -> {
                // Prismatic faceted crystal wings with geometric gemstone cuts
                val leftWing = Path().apply {
                    moveTo(center.x, center.y - 3f)
                    lineTo(center.x - 12f, center.y - 36f + wingSpread)
                    lineTo(center.x - 26f, center.y - 44f + wingSpread * 1.2f)
                    lineTo(center.x - 36f, center.y - 22f + wingSpread * 0.6f)
                    lineTo(center.x - 22f, center.y + 2f)
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x, center.y + 3f)
                    lineTo(center.x - 12f, center.y + 36f - wingSpread)
                    lineTo(center.x - 26f, center.y + 44f - wingSpread * 1.2f)
                    lineTo(center.x - 36f, center.y + 22f - wingSpread * 0.6f)
                    lineTo(center.x - 22f, center.y - 2f)
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.94f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.94f))
            }
            WingStyle.CYBER_NEON -> {
                // Futuristic swept neon blade wings
                val leftWing = Path().apply {
                    moveTo(center.x + 4f, center.y - 2f)
                    lineTo(center.x - 16f, center.y - 44f + wingSpread * 1.2f)
                    lineTo(center.x - 26f, center.y - 40f + wingSpread * 1.1f)
                    lineTo(center.x - 34f, center.y - 12f)
                    lineTo(center.x - 18f, center.y + 2f)
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x + 4f, center.y + 2f)
                    lineTo(center.x - 16f, center.y + 44f - wingSpread * 1.2f)
                    lineTo(center.x - 26f, center.y + 40f - wingSpread * 1.1f)
                    lineTo(center.x - 34f, center.y + 12f)
                    lineTo(center.x - 18f, center.y - 2f)
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.96f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.96f))
            }
            WingStyle.RAIN_TEMPEST -> {
                // Flowing aquatic wings with undulating water drops
                val leftWing = Path().apply {
                    moveTo(center.x - 2f, center.y - 2f)
                    cubicTo(
                        center.x - 18f, center.y - 40f + wingSpread,
                        center.x - 42f, center.y - 22f + wingSpread,
                        center.x - 24f, center.y + 4f
                    )
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x - 2f, center.y + 2f)
                    cubicTo(
                        center.x - 18f, center.y + 40f - wingSpread,
                        center.x - 42f, center.y + 22f - wingSpread,
                        center.x - 24f, center.y - 4f
                    )
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.88f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.88f))
            }
            else -> {
                // Classical Swallow / Angelic feathered wings
                val leftWing = Path().apply {
                    moveTo(center.x - 4f, center.y - 2f)
                    cubicTo(
                        center.x - 16f, center.y - 34f + wingSpread,
                        center.x - 36f, center.y - 18f + wingSpread,
                        center.x - 30f, center.y + 4f
                    )
                    close()
                }
                val rightWing = Path().apply {
                    moveTo(center.x - 4f, center.y + 2f)
                    cubicTo(
                        center.x - 16f, center.y + 34f - wingSpread,
                        center.x - 36f, center.y + 18f - wingSpread,
                        center.x - 30f, center.y - 4f
                    )
                    close()
                }
                drawPath(path = leftWing, color = skin.wingColor.copy(alpha = 0.92f))
                drawPath(path = rightWing, color = skin.wingColor.copy(alpha = 0.92f))
            }
        }

        // Sleek Celestial Spirit Body
        val bodyPath = Path().apply {
            moveTo(center.x + 19f, center.y) // Beak/Head forward
            lineTo(center.x - 14f, center.y - 6.5f)
            lineTo(center.x - 28f, center.y) // Tail
            lineTo(center.x - 14f, center.y + 6.5f)
            close()
        }
        drawPath(
            path = bodyPath,
            color = skin.primaryColor
        )

        // Glowing Spirit Eye / Forehead Star
        drawCircle(
            color = skin.eyeColor,
            radius = 3.2f,
            center = Offset(center.x + 10f, center.y)
        )
    }
}

private fun lerpColor(c1: Color, c2: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = c1.red + (c2.red - c1.red) * f,
        green = c1.green + (c2.green - c1.green) * f,
        blue = c1.blue + (c2.blue - c1.blue) * f,
        alpha = c1.alpha + (c2.alpha - c1.alpha) * f
    )
}

/**
 * Renders a soothing, ethereal touch reticle and luminous direction flow
 * indicating the direction the player is steering from the center of the screen.
 */
private fun DrawScope.drawTouchGuidance(center: Offset, target: Offset, color: Color) {
    val dx = target.x - center.x
    val dy = target.y - center.y
    val dist = hypot(dx, dy)
    if (dist < 16f) return

    // Subtle luminous guidance beam
    val beamLength = minOf(dist, 110f)
    val beamEnd = Offset(
        center.x + (dx / dist) * beamLength,
        center.y + (dy / dist) * beamLength
    )
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = 0.55f),
                Color.Transparent
            ),
            start = center,
            end = beamEnd
        ),
        start = center,
        end = beamEnd,
        strokeWidth = 3f,
        cap = StrokeCap.Round
    )

    // Soft focus pulse ring at touch location
    drawCircle(
        color = color.copy(alpha = 0.16f),
        radius = 26f,
        center = target
    )
    drawCircle(
        color = color.copy(alpha = 0.45f),
        radius = 15f,
        center = target,
        style = Stroke(width = 2f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.82f),
        radius = 4f,
        center = target
    )
}

/**
 * Earth Core: Radiant superheated crystalline heart of the planet with
 * dynamic pulsating magnetic field lines and blinding golden solar flare arcs.
 */
private fun DrawScope.drawEarthCore(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    val coreX = w * 0.5f - playerRelX * 0.15f - ((state.cameraX * 0.05f) % (w * 0.3f))
    val coreY = h * 0.55f - playerRelY * 0.15f
    val coreRadius = w * 0.42f

    // 1. Massive radiant outer plasma aura
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD54F).copy(alpha = 0.55f * alpha),
                Color(0xFFFF6F00).copy(alpha = 0.28f * alpha),
                Color(0xFFB71C1C).copy(alpha = 0.12f * alpha),
                Color.Transparent
            ),
            center = Offset(coreX, coreY),
            radius = coreRadius * 2.2f
        ),
        radius = coreRadius * 2.2f,
        center = Offset(coreX, coreY)
    )

    // 2. Magnetic loops / Geodynamo flux field lines
    for (i in 1..4) {
        val loopWidth = coreRadius * (0.8f + i * 0.35f)
        val loopHeight = coreRadius * (0.45f + i * 0.25f)
        val loopAngle = (state.flyer.wingPhase * 15f + i * 45f) % 360f

        rotate(loopAngle, pivot = Offset(coreX, coreY)) {
            drawOval(
                color = Color(0xFFFFE082).copy(alpha = (0.35f - i * 0.06f).coerceAtLeast(0.08f) * alpha),
                topLeft = Offset(coreX - loopWidth * 0.5f, coreY - loopHeight * 0.5f),
                size = androidx.compose.ui.geometry.Size(loopWidth, loopHeight),
                style = Stroke(width = 2.5f)
            )
        }
    }

    // 3. Super-dense inner core sphere
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.95f * alpha),
                Color(0xFFFFF9C4).copy(alpha = 0.85f * alpha),
                Color(0xFFFFB300).copy(alpha = 0.7f * alpha)
            ),
            center = Offset(coreX, coreY),
            radius = coreRadius * 0.75f
        ),
        radius = coreRadius * 0.75f,
        center = Offset(coreX, coreY)
    )
}

/**
 * Magma Layer: Churning molten incandescent lava sea, floating volcanic basalt
 * shelves, and rising fire vapor seams.
 */
private fun DrawScope.drawMagmaRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    val farX = -state.cameraX * 0.15f - playerRelX * 0.1f
    val farY = -playerRelY * 0.1f
    val time = flyer.wingPhase * 0.8f

    // 1. Churning Molten Lava Wave Sea at bottom
    val lavaWave = Path().apply {
        val seaBaseY = h * 0.65f + farY
        moveTo(0f, h)
        lineTo(0f, seaBaseY)
        val step = w / 6f
        for (i in 0..6) {
            val px = i * step
            val waveHeight = sin(time + i * 1.2f + farX * 0.005f) * 18f
            val py = seaBaseY + waveHeight
            lineTo(px, py)
        }
        lineTo(w, h)
        close()
    }

    drawPath(
        path = lavaWave,
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFAB00).copy(alpha = 0.85f * alpha),
                Color(0xFFFF3D00).copy(alpha = 0.9f * alpha),
                Color(0xFFDD2C00).copy(alpha = 0.95f * alpha)
            )
        )
    )

    // 2. Basalt Columns / Volcanic rock crags rising from lava
    val rockPath = Path().apply {
        val rockBaseY = h * 0.72f + farY
        val rx1 = ((w * 0.2f + farX) % (w + 200f)) - 100f
        moveTo(rx1 - 40f, h)
        lineTo(rx1 - 25f, rockBaseY - 90f)
        lineTo(rx1 + 25f, rockBaseY - 85f)
        lineTo(rx1 + 45f, h)
        close()

        val rx2 = ((w * 0.75f + farX * 0.8f) % (w + 200f)) - 100f
        moveTo(rx2 - 55f, h)
        lineTo(rx2 - 35f, rockBaseY - 140f)
        lineTo(rx2 + 30f, rockBaseY - 130f)
        lineTo(rx2 + 60f, h)
        close()
    }
    drawPath(
        path = rockPath,
        color = Color(0xFF260805).copy(alpha = 0.92f * alpha)
    )

    // Glowing fissures on rocks
    val rx2 = ((w * 0.75f + farX * 0.8f) % (w + 200f)) - 100f
    drawLine(
        color = Color(0xFFFF6D00).copy(alpha = 0.85f * alpha),
        start = Offset(rx2 - 10f, h * 0.72f + farY - 120f),
        end = Offset(rx2 + 5f, h * 0.72f + farY - 30f),
        strokeWidth = 3.5f,
        cap = StrokeCap.Round
    )
}

/**
 * Earth Mantle: Convective semi-molten magma currents threading through dark
 * tectonic stone strata.
 */
private fun DrawScope.drawEarthMantle(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    val mantleOffsetX = -state.cameraX * 0.12f - playerRelX * 0.08f
    val mantleOffsetY = -playerRelY * 0.08f

    // 1. Tectonic plate slabs
    val plate1 = Path().apply {
        val y1 = h * 0.25f + mantleOffsetY
        val px = ((w * 0.35f + mantleOffsetX) % (w + 300f)) - 150f
        moveTo(px - 140f, y1 - 40f)
        lineTo(px + 120f, y1 - 60f)
        lineTo(px + 160f, y1 + 50f)
        lineTo(px - 110f, y1 + 70f)
        close()
    }
    drawPath(
        path = plate1,
        color = Color(0xFF3E1C14).copy(alpha = 0.8f * alpha)
    )

    val plate2 = Path().apply {
        val y2 = h * 0.7f + mantleOffsetY
        val px = ((w * 0.7f + mantleOffsetX * 0.9f) % (w + 300f)) - 150f
        moveTo(px - 160f, y2 - 50f)
        lineTo(px + 140f, y2 - 30f)
        lineTo(px + 180f, y2 + 80f)
        lineTo(px - 130f, y2 + 60f)
        close()
    }
    drawPath(
        path = plate2,
        color = Color(0xFF2D140E).copy(alpha = 0.85f * alpha)
    )

    // 2. Glowing Geothermal Veins threading through
    val veinPath = Path().apply {
        val py = h * 0.48f + mantleOffsetY
        moveTo(0f, py)
        cubicTo(
            w * 0.25f, py - 40f,
            w * 0.6f, py + 50f,
            w, py - 20f
        )
    }
    drawPath(
        path = veinPath,
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFF7043).copy(alpha = 0.75f * alpha),
                Color(0xFFFFAB40).copy(alpha = 0.95f * alpha),
                Color(0xFFFF5722).copy(alpha = 0.75f * alpha)
            )
        ),
        style = Stroke(width = 6f, cap = StrokeCap.Round)
    )
}

/**
 * Earth Crust: Deep subterranean cavern with hanging stalactites, jagged
 * crystal formations (emerald & amethyst geodes), and mystical crystal glints.
 */
private fun DrawScope.drawEarthCrust(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    val crustX = -state.cameraX * 0.18f - playerRelX * 0.12f
    val crustY = -playerRelY * 0.12f

    // 1. Cavern Ceiling Stalactites
    val stalactitePath = Path().apply {
        moveTo(0f, 0f)
        val step = w / 7f
        for (i in 0..7) {
            val sx = i * step
            val length = (45f + sin(i * 2.3f) * 35f).coerceAtLeast(15f)
            lineTo(sx - step * 0.5f, 0f)
            lineTo(sx, length + crustY * 0.5f)
        }
        lineTo(w, 0f)
        close()
    }
    drawPath(
        path = stalactitePath,
        color = Color(0xFF1E2822).copy(alpha = 0.9f * alpha)
    )

    // 2. Luminous Emerald & Amethyst Geode Crystals on floor ledges
    val crystalColor = Color(0xFF69F0AE)
    val geodeX = ((w * 0.4f + crustX) % (w + 200f)) - 100f
    val geodeY = h * 0.82f + crustY

    // Draw crystal spire cluster
    val crystalSpire1 = Path().apply {
        moveTo(geodeX - 18f, geodeY + 30f)
        lineTo(geodeX - 6f, geodeY - 55f)
        lineTo(geodeX + 8f, geodeY + 30f)
        close()
    }
    val crystalSpire2 = Path().apply {
        moveTo(geodeX + 4f, geodeY + 30f)
        lineTo(geodeX + 18f, geodeY - 75f)
        lineTo(geodeX + 32f, geodeY + 30f)
        close()
    }
    drawPath(path = crystalSpire1, color = crystalColor.copy(alpha = 0.85f * alpha))
    drawPath(path = crystalSpire2, color = Color(0xFFB388FF).copy(alpha = 0.88f * alpha))

    // Crystal glow
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                crystalColor.copy(alpha = 0.4f * alpha),
                Color.Transparent
            ),
            center = Offset(geodeX + 6f, geodeY - 40f),
            radius = 65f
        ),
        radius = 65f,
        center = Offset(geodeX + 6f, geodeY - 40f)
    )
}

/**
 * Sunset City Dimension: Vibrant golden-magenta sunset, silhouetted skyscrapers
 * with glowing warm windows, rooftop radio antennas, and futuristic skyway trails.
 */
private fun DrawScope.drawSunsetCityRealm(w: Float, h: Float, alt: Float, alpha: Float, state: GameUiState) {
    val flyer = state.flyer
    val playerRelX = flyer.x - w * 0.5f
    val playerRelY = flyer.y - h * 0.5f

    val cityFarX = -state.cameraX * 0.08f - playerRelX * 0.05f
    val cityMidX = -state.cameraX * 0.18f - playerRelX * 0.1f
    val cityNearX = -state.cameraX * 0.28f - playerRelX * 0.16f
    val cityY = -playerRelY * 0.08f

    // 1. Giant Setting Sun on the Horizon
    val sunX = w * 0.5f + cityFarX * 0.4f
    val sunY = h * 0.42f + cityY
    val sunRadius = w * 0.26f

    drawCircle(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFFE082).copy(alpha = 0.95f * alpha),
                Color(0xFFFF8A65).copy(alpha = 0.9f * alpha),
                Color(0xFFE91E63).copy(alpha = 0.85f * alpha)
            ),
            startY = sunY - sunRadius,
            endY = sunY + sunRadius
        ),
        radius = sunRadius,
        center = Offset(sunX, sunY)
    )

    // Sun atmospheric halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFAB40).copy(alpha = 0.35f * alpha),
                Color(0xFFFF6D00).copy(alpha = 0.15f * alpha),
                Color.Transparent
            ),
            center = Offset(sunX, sunY),
            radius = sunRadius * 2.2f
        ),
        radius = sunRadius * 2.2f,
        center = Offset(sunX, sunY)
    )

    // 2. Far Skyline Silhouettes (Deep violet/purple)
    val farSkyline = Path().apply {
        val baseY = h * 0.58f + cityY
        moveTo(0f, h)
        lineTo(0f, baseY)
        val buildingWidths = listOf(55f, 75f, 40f, 90f, 60f, 80f, 50f, 100f, 65f)
        val buildingHeights = listOf(140f, 210f, 170f, 260f, 190f, 230f, 150f, 280f, 180f)

        var curX = ((cityFarX) % 600f) - 200f
        var idx = 0
        while (curX < w + 200f) {
            val bw = buildingWidths[idx % buildingWidths.size]
            val bh = buildingHeights[idx % buildingHeights.size]
            lineTo(curX, baseY)
            lineTo(curX, baseY - bh)
            lineTo(curX + bw, baseY - bh)
            curX += bw
            idx++
        }
        lineTo(w, baseY)
        lineTo(w, h)
        close()
    }
    drawPath(path = farSkyline, color = Color(0xFF3F1D38).copy(alpha = 0.75f * alpha))

    // 3. Midground City Skyline with Glowing Windows
    val midBaseY = h * 0.72f + cityY
    val midBuildingWidths = listOf(70f, 85f, 60f, 110f, 75f, 95f)
    val midBuildingHeights = listOf(160f, 240f, 190f, 310f, 220f, 270f)

    var curMidX = ((cityMidX) % 500f) - 150f
    var mIdx = 0
    while (curMidX < w + 150f) {
        val bw = midBuildingWidths[mIdx % midBuildingWidths.size]
        val bh = midBuildingHeights[mIdx % midBuildingHeights.size]
        val bTop = midBaseY - bh

        // Tower body
        drawRect(
            color = Color(0xFF1E1026).copy(alpha = 0.92f * alpha),
            topLeft = Offset(curMidX, bTop),
            size = androidx.compose.ui.geometry.Size(bw, bh + (h - midBaseY))
        )

        // Rooftop antenna with red beacon light
        val antennaX = curMidX + bw * 0.5f
        drawLine(
            color = Color(0xFF6A4C93).copy(alpha = 0.8f * alpha),
            start = Offset(antennaX, bTop),
            end = Offset(antennaX, bTop - 35f),
            strokeWidth = 2f
        )
        drawCircle(
            color = Color(0xFFFF1744).copy(alpha = (0.7f + sin(state.flyer.wingPhase * 4f) * 0.25f) * alpha),
            radius = 3.5f,
            center = Offset(antennaX, bTop - 35f)
        )

        // Windows grid
        val winCols = (bw / 16f).toInt()
        val winRows = (bh / 24f).toInt()
        for (r in 1 until winRows) {
            for (c in 1 until winCols) {
                val wx = curMidX + c * 16f
                val wy = bTop + r * 24f
                val isLit = (mIdx * 11 + r * 7 + c * 13) % 4 != 0
                if (isLit) {
                    val winColor = if ((r + c) % 3 == 0) Color(0xFF80D8FF) else Color(0xFFFFD54F)
                    drawRect(
                        color = winColor.copy(alpha = 0.78f * alpha),
                        topLeft = Offset(wx - 3f, wy - 4f),
                        size = androidx.compose.ui.geometry.Size(6f, 8f)
                    )
                }
            }
        }

        curMidX += bw + 8f
        mIdx++
    }

    // 4. Futuristic Skyway Traffic Light Trails
    val skywayY = h * 0.62f + cityY
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(
                Color.Transparent,
                Color(0xFFFFD54F).copy(alpha = 0.85f * alpha),
                Color(0xFFFF5252).copy(alpha = 0.9f * alpha),
                Color.Transparent
            )
        ),
        start = Offset(0f, skywayY),
        end = Offset(w, skywayY),
        strokeWidth = 2.5f
    )
}

/**
 * Dimensional Portal: Swirling cosmic vortex with rotating rings, event
 * horizon glow, and destination realm title!
 */
private fun DrawScope.drawPortals(portal: DimensionalPortal?) {
    if (portal == null) return

    val p = portal
    val phase = p.phase
    val center = Offset(p.x, p.y)
    val r = p.radius

    // 1. Swirling Outer Accretion Disk
    for (ring in 1..3) {
        val ringRadius = r * (0.8f + ring * 0.35f)
        val ringAngle = (phase * (35f / ring) + ring * 60f) % 360f

        rotate(ringAngle, pivot = center) {
            drawOval(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        p.color.copy(alpha = 0.9f),
                        Color(0xFFE040FB).copy(alpha = 0.6f),
                        Color.White.copy(alpha = 0.95f),
                        p.color.copy(alpha = 0.2f)
                    ),
                    center = center
                ),
                topLeft = Offset(center.x - ringRadius, center.y - ringRadius * 0.65f),
                size = androidx.compose.ui.geometry.Size(ringRadius * 2f, ringRadius * 1.3f),
                style = Stroke(width = 3.5f)
            )
        }
    }

    // 2. Cosmic Void Event Horizon Center
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF0D0221),
                p.color.copy(alpha = 0.75f),
                Color.Transparent
            ),
            center = center,
            radius = r * 1.3f
        ),
        radius = r * 1.3f,
        center = center
    )

    // 3. Glowing Center Core
    drawCircle(
        color = Color.White.copy(alpha = 0.95f),
        radius = r * 0.35f,
        center = center
    )

    // 4. Portal Beacon Pulsing Aura
    val auraRadius = (r * (1.8f + kotlin.math.sin(phase * 3f) * 0.25f)).toFloat()
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                p.color.copy(alpha = 0.38f),
                Color.Transparent
            ),
            center = center,
            radius = auraRadius
        ),
        radius = auraRadius,
        center = center
    )
}

/**
 * Atmospheric Rain: Renders translucent falling rain streaks with gentle motion
 * blur and soft splash ripple rings when raining in the current realm.
 */
private fun DrawScope.drawRain(rainDrops: List<RainDrop>, state: GameUiState) {
    if (!state.currentRealm.hasRain) return

    val rainColor = Color(0xFF80D8FF)

    rainDrops.forEach { drop ->
        val startX = drop.x
        val startY = drop.y
        val endX = drop.x - drop.length * 0.24f
        val endY = drop.y + drop.length

        drawLine(
            brush = Brush.linearGradient(
                colors = listOf(
                    rainColor.copy(alpha = drop.alpha * 0.35f),
                    rainColor.copy(alpha = drop.alpha * 0.95f)
                ),
                start = Offset(startX, startY),
                end = Offset(endX, endY)
            ),
            start = Offset(startX, startY),
            end = Offset(endX, endY),
            strokeWidth = 2.2f,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Subterranean Magma Embers: Floating incandescent fire specks rising softly
 * in mantle, magma, and core layers.
 */
private fun DrawScope.drawMagmaEmbers(magmaEmbers: List<MagmaEmber>, state: GameUiState) {
    if (!state.currentRealm.isSubterranean) return

    magmaEmbers.forEach { ember ->
        val pulse = 1f + sin(ember.phase) * 0.3f
        val r = ember.radius * pulse

        // Outer warm glow
        drawCircle(
            color = ember.color.copy(alpha = 0.35f),
            radius = r * 2.5f,
            center = Offset(ember.x, ember.y)
        )
        // Radiant ember spark
        drawCircle(
            color = Color.White.copy(alpha = 0.9f),
            radius = r * 0.6f,
            center = Offset(ember.x, ember.y)
        )
    }
}
