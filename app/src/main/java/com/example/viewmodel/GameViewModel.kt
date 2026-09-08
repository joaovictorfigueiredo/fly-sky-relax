package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.RelaxingAudioEngine
import com.example.model.AmbientParticle
import com.example.model.Collectible
import com.example.model.CollectibleType
import com.example.model.DimensionalPortal
import com.example.model.FloatingFeedback
import com.example.model.FlyerState
import com.example.model.MagmaEmber
import com.example.model.RainDrop
import com.example.model.Realm
import com.example.model.ShockwaveRing
import com.example.model.Skin
import com.example.model.SkinRepository
import com.example.model.SparkleParticle
import com.example.data.db.AppDatabase
import com.example.data.db.GameStatsEntity
import com.example.data.db.ScoreRecord
import com.example.data.db.ScoreRepository
import com.example.model.BlackHole
import com.example.model.MatrixCodeStream
import com.example.model.Predator
import com.example.model.PredatorCategory
import com.example.model.PredatorType
import com.example.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

data class GameUiState(
    val flyer: FlyerState = FlyerState(),
    val altitude: Float = 150f, // Starting in forest canopy
    val currentRealm: Realm = Realm.FOREST,
    val transitionAlpha: Float = 0f,
    val nextRealm: Realm = Realm.SPACE,
    val lightsCollected: Int = 0,
    val bubblesCollected: Int = 0,
    val flowersCollected: Int = 0,
    val totalCollections: Int = 0,
    val pointMultiplier: Int = 1,
    val multiplierCost: Int = 5,
    val equippedSkin: Skin = SkinRepository.allSkins[0],
    val unlockedSkinIds: Set<String> = setOf(SkinRepository.allSkins[0].id),
    val unlockedRealms: Set<Realm> = setOf(
        Realm.FOREST,
        Realm.SPACE,
        Realm.PLANETS_GALAXIES,
        Realm.CELESTIAL_CLOUDS
    ),
    val showSkinsDialog: Boolean = false,
    val showUniverseDialog: Boolean = false,
    val showFlowerShopDialog: Boolean = false,
    val showScoreHistoryDialog: Boolean = false,
    val savedScores: List<ScoreRecord> = emptyList(),
    val bestSavedScore: Int = 0,
    val isHyperspaceActive: Boolean = false,
    val hyperspaceProgress: Float = 0f,
    val activePredators: List<Predator> = emptyList(),
    val activeBlackHoles: List<BlackHole> = emptyList(),
    val matrixStreams: List<MatrixCodeStream> = emptyList(),
    val serenityScore: Int = 0,
    val comboCount: Int = 0,
    val maxAltitude: Float = 150f,
    val cameraX: Float = 0f,
    val cameraY: Float = 0f,
    val targetOffset: Offset? = null,
    val isPaused: Boolean = false,
    val isBreathingMode: Boolean = false,
    val breathingPhase: String = "Inspire suavemente...",
    val breathingProgress: Float = 0f,
    val isWindMuted: Boolean = false,
    val isMusicMuted: Boolean = false,
    val isLofiActive: Boolean = true,
    val showInfoDialog: Boolean = false,
    val lastRealmAnnounced: Realm = Realm.FOREST,
    val activePortal: DimensionalPortal? = null,
    val rainDrops: List<RainDrop> = emptyList(),
    val magmaEmbers: List<MagmaEmber> = emptyList()
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    val audioEngine = RelaxingAudioEngine()

    private val db by lazy { AppDatabase.getInstance(getApplication()) }
    private val scoreRepository by lazy { ScoreRepository(db.scoreDao()) }

    // Game world elements
    val collectibles = mutableListOf<Collectible>()
    val sparkles = mutableListOf<SparkleParticle>()
    val shockwaves = mutableListOf<ShockwaveRing>()
    val floatingFeedbacks = mutableListOf<FloatingFeedback>()
    val ambientParticles = mutableListOf<AmbientParticle>()
    val rainDrops = mutableListOf<RainDrop>()
    val magmaEmbers = mutableListOf<MagmaEmber>()
    val predators = mutableListOf<Predator>()
    val blackHoles = mutableListOf<BlackHole>()
    val matrixStreams = mutableListOf<MatrixCodeStream>()
    var activePortal: DimensionalPortal? = null

    private var gameLoopJob: Job? = null
    private var breathingJob: Job? = null
    private var comboTimer = 0f
    private var nextCollectibleId = 1L
    private var nextPredatorId = 1L
    private var nextBlackHoleId = 1L
    private var portalTimer = 20f // Spawns rare portals periodically
    private var predatorSpawnTimer = 6f
    private var blackHoleSpawnTimer = 16f
    private var hyperspaceTimer = 0f
    private var isHyperspaceActive = false
    private var flightDurationSeconds = 0
    private var flightDurationTimer = 0f
    private var autoSaveTimer = 25f

    // Viewport dimensions (set by Canvas onSizeChanged)
    var screenWidth: Float = 1080f
    var screenHeight: Float = 2160f

    // Touch control state
    private var targetOffset: Offset? = null
    private var isFlapping: Boolean = false

    private val prefs: SharedPreferences by lazy {
        getApplication<Application>().getSharedPreferences("relaxing_flight_prefs", Context.MODE_PRIVATE)
    }

    init {
        loadSavedSkinState()
        initPersistenceFlows()
        audioEngine.start()
        audioEngine.isLofiEnabled = true
        initWeatherElements()
        initMatrixCodeStreams()
        startGameLoop()
        startBreathingCycle()
    }

    private fun initPersistenceFlows() {
        viewModelScope.launch {
            scoreRepository.recentScores.collect { list ->
                _uiState.update { it.copy(savedScores = list) }
            }
        }
        viewModelScope.launch {
            scoreRepository.gameStats.collect { stats ->
                if (stats != null) {
                    val unlockedFromStats = stats.unlockedRealmsString
                        .split(",")
                        .mapNotNull { name ->
                            try { Realm.valueOf(name.trim()) } catch (e: Exception) { null }
                        }
                        .toSet()

                    _uiState.update { current ->
                        current.copy(
                            bestSavedScore = stats.bestSerenity,
                            bubblesCollected = maxOf(current.bubblesCollected, stats.totalBubbles),
                            flowersCollected = maxOf(current.flowersCollected, stats.totalFlowers),
                            lightsCollected = maxOf(current.lightsCollected, stats.totalLights),
                            pointMultiplier = maxOf(current.pointMultiplier, stats.pointMultiplier),
                            unlockedRealms = current.unlockedRealms + unlockedFromStats
                        )
                    }
                }
            }
        }
    }

    private fun initMatrixCodeStreams() {
        matrixStreams.clear()
        val glyphSet = "01010110アイウエオカキクケコサシスセソタチツテトナニヌネノハヒフヘホマミムメモヤユヨラリルレロワヲンλπΩ∞✦"
        val columnCount = 24
        val colWidth = 1200f / columnCount
        for (i in 0 until columnCount) {
            val streamLength = 12 + Random.nextInt(16)
            val builder = StringBuilder()
            for (j in 0 until streamLength) {
                builder.append(glyphSet.random())
            }
            matrixStreams.add(
                MatrixCodeStream(
                    x = i * colWidth + Random.nextFloat() * 10f,
                    y = Random.nextFloat() * 2400f,
                    speed = 280f + Random.nextFloat() * 320f,
                    chars = builder.toString(),
                    alpha = 0.4f + Random.nextFloat() * 0.55f
                )
            )
        }
    }

    private fun initWeatherElements() {
        rainDrops.clear()
        for (i in 0 until 65) {
            rainDrops.add(
                RainDrop(
                    x = Random.nextFloat() * 1200f,
                    y = Random.nextFloat() * 2400f,
                    speed = 700f + Random.nextFloat() * 450f,
                    length = 18f + Random.nextFloat() * 26f,
                    alpha = 0.25f + Random.nextFloat() * 0.45f
                )
            )
        }
        magmaEmbers.clear()
        for (i in 0 until 35) {
            magmaEmbers.add(
                MagmaEmber(
                    x = Random.nextFloat() * 1200f,
                    y = Random.nextFloat() * 2400f,
                    vx = (Random.nextFloat() - 0.5f) * 45f,
                    vy = -(40f + Random.nextFloat() * 80f),
                    size = 3f + Random.nextFloat() * 5.5f,
                    color = if (Random.nextFloat() < 0.6f) Color(0xFFFFAB40) else Color(0xFFFF5722),
                    alpha = 0.4f + Random.nextFloat() * 0.5f,
                    phase = Random.nextFloat() * 6.28f
                )
            )
        }
    }

    private fun loadSavedSkinState() {
        val totalCols = prefs.getInt("total_collections", 0)
        val savedSkinId = prefs.getString("equipped_skin_id", SkinRepository.allSkins[0].id) ?: SkinRepository.allSkins[0].id
        val equipped = SkinRepository.getSkinById(savedSkinId)
        val unlockedSet = mutableSetOf(SkinRepository.allSkins[0].id)
        SkinRepository.allSkins.forEach { skin ->
            if (totalCols >= skin.requiredCollections) {
                unlockedSet.add(skin.id)
            }
        }
        _uiState.update { current ->
            current.copy(
                totalCollections = totalCols,
                equippedSkin = equipped,
                unlockedSkinIds = unlockedSet
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        saveCurrentFlightToDb(showNotice = false)
        audioEngine.stop()
        gameLoopJob?.cancel()
        breathingJob?.cancel()
    }

    fun setScoreHistoryDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showScoreHistoryDialog = visible) }
    }

    fun saveCurrentFlightToDb(showNotice: Boolean = true) {
        val state = _uiState.value
        val duration = flightDurationSeconds
        viewModelScope.launch {
            val record = ScoreRecord(
                serenityScore = state.serenityScore,
                maxAltitude = state.maxAltitude,
                lightsCollected = state.lightsCollected,
                bubblesCollected = state.bubblesCollected,
                flowersCollected = state.flowersCollected,
                realmName = state.currentRealm.title,
                durationSeconds = duration.toLong()
            )
            scoreRepository.saveFlightRecord(record)

            val currentBest = maxOf(state.serenityScore, state.bestSavedScore)
            val stats = GameStatsEntity(
                id = 1,
                totalBubbles = state.bubblesCollected,
                totalFlowers = state.flowersCollected,
                totalLights = state.lightsCollected,
                bestSerenity = currentBest,
                bestAltitude = maxOf(state.maxAltitude, state.altitude),
                pointMultiplier = state.pointMultiplier,
                unlockedRealmsString = state.unlockedRealms.joinToString(",") { it.name }
            )
            scoreRepository.saveStats(stats)

            if (showNotice) {
                floatingFeedbacks.add(
                    FloatingFeedback(
                        x = screenWidth * 0.5f,
                        y = screenHeight * 0.35f,
                        text = "💾 Voo Salvo no Hall da Fama!",
                        color = Color(0xFF80E27E),
                        scale = 1.3f
                    )
                )
                audioEngine.playLightChime(5)
                HapticHelper.performGentleTap(getApplication())
            }
        }
    }

    fun onScreenSizeChanged(width: Float, height: Float) {
        if (width <= 0 || height <= 0) return
        screenWidth = width
        screenHeight = height

        // Always center flyer at screen center
        _uiState.update { current ->
            current.copy(
                flyer = current.flyer.copy(
                    x = width * 0.5f,
                    y = height * 0.5f
                )
            )
        }

        // Initialize ambient particles if empty
        if (ambientParticles.isEmpty()) {
            initAmbientParticles(width, height)
            spawnInitialCollectibles()
        }
    }

    fun onTouchDown(position: Offset) {
        targetOffset = position
        _uiState.update { it.copy(targetOffset = position) }
    }

    fun onTouchMove(position: Offset) {
        targetOffset = position
        _uiState.update { it.copy(targetOffset = position) }
    }

    fun onTouchUp() {
        targetOffset = null
        _uiState.update { it.copy(targetOffset = null) }
    }

    fun onSoarStart() {
        isFlapping = true
    }

    fun onSoarEnd() {
        isFlapping = false
    }

    fun togglePause() {
        _uiState.update { it.copy(isPaused = !it.isPaused) }
    }

    fun toggleBreathingMode() {
        _uiState.update { it.copy(isBreathingMode = !it.isBreathingMode) }
    }

    fun toggleWind() {
        val newWind = !audioEngine.isWindEnabled
        audioEngine.isWindEnabled = newWind
        _uiState.update { it.copy(isWindMuted = !newWind) }
    }

    fun toggleMusic() {
        val newMusic = !audioEngine.isMusicEnabled
        audioEngine.isMusicEnabled = newMusic
        _uiState.update { it.copy(isMusicMuted = !newMusic) }
    }

    fun toggleLofi() {
        val next = !audioEngine.isLofiEnabled
        audioEngine.isLofiEnabled = next
        _uiState.update { it.copy(isLofiActive = next) }
        floatingFeedbacks.add(
            FloatingFeedback(
                x = screenWidth * 0.5f,
                y = screenHeight * 0.35f,
                text = if (next) "🎵 Lo-Fi Chill Ativado" else "🍃 Modo Zen Puro",
                color = if (next) Color(0xFFFF80AB) else Color(0xFF80DEEA),
                scale = 1.2f
            )
        )
    }

    fun buyPointMultiplier() {
        val state = _uiState.value
        if (state.flowersCollected >= state.multiplierCost) {
            val newMultiplier = state.pointMultiplier * 2
            val newFlowers = state.flowersCollected - state.multiplierCost
            val newCost = state.multiplierCost * 2

            _uiState.update {
                it.copy(
                    flowersCollected = newFlowers,
                    pointMultiplier = newMultiplier,
                    multiplierCost = newCost
                )
            }

            audioEngine.playFlowerChime()
            HapticHelper.performGentleTap(getApplication())

            floatingFeedbacks.add(
                FloatingFeedback(
                    x = screenWidth * 0.5f,
                    y = screenHeight * 0.38f,
                    text = "🌸 Multiplicador Dobrado! (${newMultiplier}x)",
                    color = Color(0xFFFF4081),
                    scale = 1.4f
                )
            )
        }
    }

    fun setUniverseDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showUniverseDialog = visible) }
    }

    fun setFlowerShopVisible(visible: Boolean) {
        _uiState.update { it.copy(showFlowerShopDialog = visible) }
    }

    fun teleportToRealm(realm: Realm) {
        val targetAlt = when (realm) {
            Realm.EARTH_CORE -> -3400f
            Realm.MAGMA -> -2400f
            Realm.EARTH_MANTLE -> -1400f
            Realm.EARTH_CRUST -> -400f
            Realm.FOREST -> 400f
            Realm.SPACE -> 1600f
            Realm.PLANETS_GALAXIES -> 2800f
            Realm.CELESTIAL_CLOUDS -> 4000f
            Realm.SUNSET_CITY -> 5400f
            Realm.CYBERPUNK -> 7200f
            Realm.MATRIX -> 8700f
            Realm.MUSHROOMS -> 10200f
            Realm.CLOUDS_SEA -> 11700f
            Realm.MOUNTAINS -> 13200f
            Realm.MARS -> 14700f
            Realm.TEMPEST -> 16200f
            Realm.ANDROMEDA -> 17700f
            Realm.MEDIEVAL -> 19200f
            Realm.NIGHT_FANTASY -> 21000f
        }
        audioEngine.playPortalWarpSound()
        activePortal = null
        portalTimer = 35f

        _uiState.update {
            it.copy(
                altitude = targetAlt,
                currentRealm = realm,
                nextRealm = realm,
                transitionAlpha = 0f,
                showUniverseDialog = false,
                lastRealmAnnounced = realm
            )
        }

        floatingFeedbacks.add(
            FloatingFeedback(
                x = screenWidth * 0.5f,
                y = screenHeight * 0.32f,
                text = "🌀 Portal para ${realm.title}!",
                color = realm.accentColor,
                scale = 1.4f
            )
        )
    }

    fun setInfoDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showInfoDialog = visible) }
    }

    fun setSkinsDialogVisible(visible: Boolean) {
        _uiState.update { it.copy(showSkinsDialog = visible) }
    }

    fun equipSkin(skin: Skin) {
        val isUnlocked = _uiState.value.unlockedSkinIds.contains(skin.id) ||
                _uiState.value.totalCollections >= skin.requiredCollections
        if (isUnlocked) {
            prefs.edit().putString("equipped_skin_id", skin.id).apply()
            _uiState.update { current ->
                current.copy(equippedSkin = skin)
            }
            floatingFeedbacks.add(
                FloatingFeedback(
                    x = screenWidth * 0.5f,
                    y = screenHeight * 0.4f,
                    text = "${skin.icon} ${skin.name} Equipado!",
                    color = skin.wingColor,
                    scale = 1.3f
                )
            )
            audioEngine.playRealmTransitionChime()
        }
    }

    private fun initAmbientParticles(width: Float, height: Float) {
        ambientParticles.clear()
        val count = 45
        for (i in 0 until count) {
            ambientParticles.add(
                AmbientParticle(
                    x = Random.nextFloat() * width,
                    y = Random.nextFloat() * height,
                    vx = (Random.nextFloat() - 0.5f) * 1.5f,
                    vy = (Random.nextFloat() - 0.5f) * 1.2f,
                    size = 2.5f + Random.nextFloat() * 4.5f,
                    alpha = 0.3f + Random.nextFloat() * 0.5f,
                    phase = Random.nextFloat() * 6.28f,
                    color = Color.White
                )
            )
        }
    }

    private fun spawnInitialCollectibles() {
        collectibles.clear()
        for (i in 0 until 18) {
            spawnSingleCollectible(randomY = true)
        }
    }

    private fun spawnSingleCollectible(randomY: Boolean = false) {
        val rand = Random.nextFloat()
        val type = when {
            rand < 0.20f -> CollectibleType.FLOWER // 20% flowers for upgrades
            rand < 0.60f -> CollectibleType.LIGHT  // 40% lights
            else -> CollectibleType.BUBBLE         // 40% bubbles
        }
        val currentRealm = _uiState.value.currentRealm

        val color = when (type) {
            CollectibleType.FLOWER -> Color(0xFFFF80AB) // Radiant soft flower petal pink
            CollectibleType.LIGHT -> when (currentRealm) {
                Realm.EARTH_CORE -> Color(0xFFFFF9C4)
                Realm.MAGMA -> Color(0xFFFFD54F)
                Realm.EARTH_MANTLE -> Color(0xFFFFCC80)
                Realm.EARTH_CRUST -> Color(0xFFA7FFEB)
                Realm.FOREST -> Color(0xFFFFF176)
                Realm.SPACE -> Color(0xFF80DEEA)
                Realm.PLANETS_GALAXIES -> Color(0xFFFFCC80)
                Realm.CELESTIAL_CLOUDS -> Color(0xFFFFF9C4)
                Realm.SUNSET_CITY -> Color(0xFFFFE082)
                else -> currentRealm.accentColor
            }
            CollectibleType.BUBBLE -> when (currentRealm) {
                Realm.EARTH_CORE -> Color(0xFFFFD54F)
                Realm.MAGMA -> Color(0xFFFF7043)
                Realm.EARTH_MANTLE -> Color(0xFFFFAB40)
                Realm.EARTH_CRUST -> Color(0xFF80CBC4)
                Realm.FOREST -> Color(0xFFA7FFEB)
                Realm.SPACE -> Color(0xFFCE93D8)
                Realm.PLANETS_GALAXIES -> Color(0xFFFFAB91)
                Realm.CELESTIAL_CLOUDS -> Color(0xFFF8BBD0)
                Realm.SUNSET_CITY -> Color(0xFF80DEEA)
                else -> currentRealm.skyBottomColor
            }
        }

        val spawnX: Float
        val spawnY: Float

        if (randomY || screenWidth <= 0f) {
            spawnX = Random.nextFloat() * (if (screenWidth > 0) screenWidth else 1000f)
            spawnY = Random.nextFloat() * (if (screenHeight > 0) screenHeight else 1800f)
        } else {
            val flyer = _uiState.value.flyer
            val angleRad = Math.toRadians(flyer.angleDeg.toDouble())
            val spawnDist = hypot(screenWidth, screenHeight) * 0.55f + Random.nextFloat() * 120f
            val spreadAngle = (Random.nextFloat() - 0.5f) * 1.3f
            val finalAngle = angleRad + spreadAngle

            spawnX = screenWidth * 0.5f + cos(finalAngle).toFloat() * spawnDist
            spawnY = screenHeight * 0.5f + sin(finalAngle).toFloat() * spawnDist
        }

        collectibles.add(
            Collectible(
                id = nextCollectibleId++,
                x = spawnX,
                y = spawnY,
                type = type,
                baseRadius = when (type) {
                    CollectibleType.LIGHT -> 14f
                    CollectibleType.BUBBLE -> 20f
                    CollectibleType.FLOWER -> 18f
                },
                color = color,
                phase = Random.nextFloat() * 6.28f
            )
        )
    }

    private fun startGameLoop() {
        gameLoopJob = viewModelScope.launch {
            var lastTimeNanos = System.nanoTime()

            while (isActive) {
                val nowNanos = System.nanoTime()
                val dt = ((nowNanos - lastTimeNanos) / 1_000_000_000f).coerceIn(0.008f, 0.04f)
                lastTimeNanos = nowNanos

                if (!_uiState.value.isPaused) {
                    updatePhysics(dt)
                }

                delay(16) // Target ~60 FPS update
            }
        }
    }

    private fun updatePhysics(dt: Float) {
        val state = _uiState.value
        val flyer = state.flyer

        val centerX = screenWidth * 0.5f
        val centerY = screenHeight * 0.5f

        // 1. Steering & Flight Mechanics
        var ax = 0f
        var ay = 0f

        targetOffset?.let { target ->
            // Vector from center of screen to touch position
            val dx = target.x - centerX
            val dy = target.y - centerY
            val dist = hypot(dx, dy)
            if (dist > 15f) {
                val steerFactor = (dist / (screenWidth * 0.35f)).coerceIn(0.3f, 1.2f)
                val steerStrength = 5.2f
                ax += (dx / dist) * steerStrength * 60f * steerFactor
                ay += (dy / dist) * steerStrength * 60f * steerFactor
            }
        }

        // Flapping lift / gentle soaring boost
        if (isFlapping) {
            ay -= 260f // Lift upwards
            flyer.glowIntensity = (flyer.glowIntensity + dt * 4f).coerceAtMost(2.0f)
        } else {
            flyer.glowIntensity = (flyer.glowIntensity - dt * 2f).coerceAtLeast(1.0f)
        }

        // Soothing glide resistance
        val damping = (1f - dt * 1.6f).coerceIn(0.88f, 0.98f)
        flyer.vx = (flyer.vx + ax * dt) * damping
        flyer.vy = (flyer.vy + ay * dt) * damping

        // Max speed limit
        val speed = hypot(flyer.vx, flyer.vy)
        val maxSpeed = if (isFlapping) 520f else 380f
        if (speed > maxSpeed) {
            flyer.vx = (flyer.vx / speed) * maxSpeed
            flyer.vy = (flyer.vy / speed) * maxSpeed
        }

        // Gentle relaxing cruising speed in current heading when not actively touching
        if (targetOffset == null && speed < 85f) {
            val rad = Math.toRadians(flyer.angleDeg.toDouble())
            flyer.vx += (cos(rad).toFloat() * 95f - flyer.vx) * (dt * 1.6f)
            flyer.vy += (sin(rad).toFloat() * 95f - flyer.vy) * (dt * 1.6f)
        }

        // Keep flyer firmly at the screen center with a subtle inertia lean
        flyer.x = centerX + (flyer.vx / 520f) * 15f
        flyer.y = centerY + (flyer.vy / 520f) * 15f

        // Rotation towards flight direction with smooth banking
        val updatedSpeed = hypot(flyer.vx, flyer.vy)
        if (updatedSpeed > 15f) {
            val targetAngle = Math.toDegrees(atan2(flyer.vy.toDouble(), flyer.vx.toDouble())).toFloat()
            var angleDiff = (targetAngle - flyer.angleDeg) % 360f
            if (angleDiff > 180f) angleDiff -= 360f
            if (angleDiff < -180f) angleDiff += 360f
            flyer.angleDeg += angleDiff * (dt * 7.5f)
        }

        // Wing flap phase (frequency depends on speed)
        val wingFreq = 3.5f + (updatedSpeed / maxSpeed) * 8.5f
        flyer.wingPhase += dt * wingFreq

        // The world movement vector: world moves opposite to flyer velocity
        val worldDx = -flyer.vx * dt
        val worldDy = -flyer.vy * dt

        // Silk ribbon trail: shift existing trail points with world movement
        for (i in 0 until flyer.trail.size) {
            val p = flyer.trail[i]
            flyer.trail[i] = Offset(p.x + worldDx, p.y + worldDy)
        }
        flyer.trail.add(0, Offset(flyer.x, flyer.y))
        if (flyer.trail.size > 28) {
            flyer.trail.removeAt(flyer.trail.lastIndex)
        }

        // 2. Altitude Calculation based on vertical flight
        val altitudeDelta = (-flyer.vy * dt * 0.45f)
        val newAltitude = (state.altitude + altitudeDelta).coerceIn(-3800f, 22500f)
        val maxAlt = maxOf(state.maxAltitude, newAltitude)

        // Realm detection & seamless blending
        val currentRealm = Realm.fromAltitude(newAltitude)
        val (nextRealm, transitionAlpha) = calculateRealmTransition(newAltitude)

        // Check if realm changed to trigger cosmic chime
        if (currentRealm != state.lastRealmAnnounced) {
            audioEngine.playRealmTransitionChime()
            floatingFeedbacks.add(
                FloatingFeedback(
                    x = centerX,
                    y = centerY - 120f,
                    text = "✧ ${currentRealm.title} ✧",
                    color = currentRealm.accentColor,
                    scale = 1.4f
                )
            )
        }

        // Audio wind and rain sync
        val normalizedSpeed = (updatedSpeed / maxSpeed).coerceIn(0.1f, 1f)
        audioEngine.flightSpeedFactor = normalizedSpeed
        audioEngine.altitudeProgress = ((newAltitude + 3800f) / 25000f).coerceIn(0f, 1f)
        audioEngine.isRainActive = currentRealm.hasRain

        // Update animated rain drops
        if (currentRealm.hasRain) {
            for (drop in rainDrops) {
                drop.y += drop.speed * dt
                drop.x -= drop.speed * 0.22f * dt
                if (drop.y > screenHeight + 40f) {
                    drop.y = -40f
                    drop.x = Random.nextFloat() * (screenWidth + 240f)
                }
                if (drop.x < -30f) {
                    drop.x = screenWidth + 30f
                }
            }
        }

        // Update magma embers in subterranean layers
        if (currentRealm.isSubterranean) {
            for (ember in magmaEmbers) {
                ember.phase += dt * 2.8f
                ember.y += ember.vy * dt
                ember.x += (ember.vx + sin(ember.phase) * 24f) * dt
                if (ember.y < -30f) {
                    ember.y = screenHeight + 30f
                    ember.x = Random.nextFloat() * screenWidth
                }
                if (ember.x < -30f) ember.x = screenWidth + 30f
                if (ember.x > screenWidth + 30f) ember.x = -30f
            }
        }

        // Rare Dimensional Portal Logic
        if (activePortal == null) {
            portalTimer -= dt
            if (portalTimer <= 0f) {
                val candidateRealms = listOf(
                    Realm.SUNSET_CITY,
                    Realm.CYBERPUNK,
                    Realm.MATRIX,
                    Realm.MUSHROOMS,
                    Realm.CLOUDS_SEA,
                    Realm.MOUNTAINS,
                    Realm.MARS,
                    Realm.TEMPEST,
                    Realm.ANDROMEDA,
                    Realm.MEDIEVAL,
                    Realm.NIGHT_FANTASY,
                    Realm.MAGMA,
                    Realm.EARTH_CORE
                ).filter { it != currentRealm }
                val chosen = candidateRealms.random()
                val angleRad = Math.toRadians(flyer.angleDeg.toDouble())
                val spawnDist = hypot(screenWidth, screenHeight) * 0.65f
                val pX = centerX + cos(angleRad).toFloat() * spawnDist
                val pY = centerY + sin(angleRad).toFloat() * spawnDist

                activePortal = DimensionalPortal(
                    x = pX,
                    y = pY,
                    targetRealm = chosen,
                    radius = 46f,
                    color = chosen.accentColor
                )
            }
        } else {
            val portal = activePortal!!
            portal.phase += dt * 3.5f
            portal.x += worldDx
            portal.y += worldDy

            val pDx = flyer.x - portal.x
            val pDy = flyer.y - portal.y
            val pDist = hypot(pDx, pDy)

            if (pDist < portal.radius + 32f) {
                teleportToRealm(portal.targetRealm)
            } else if (portal.x < -screenWidth || portal.x > screenWidth * 2 || portal.y < -screenHeight || portal.y > screenHeight * 2) {
                activePortal = null
                portalTimer = 35f + Random.nextFloat() * 25f
            }
        }

        // --- Hyperspace / Light-Speed Update ---
        var hyperspaceProgressVal = 0f
        if (isHyperspaceActive) {
            hyperspaceTimer -= dt
            hyperspaceProgressVal = (hyperspaceTimer / 3.2f).coerceIn(0f, 1f)
            // Tremendous forward propulsion and cosmic sparkles
            flyer.vx *= 1.04f
            flyer.vy *= 1.04f
            for (k in 0..2) {
                sparkles.add(
                    SparkleParticle(
                        x = flyer.x + (Random.nextFloat() - 0.5f) * 80f,
                        y = flyer.y + (Random.nextFloat() - 0.5f) * 80f,
                        vx = -flyer.vx * 0.7f + (Random.nextFloat() - 0.5f) * 150f,
                        vy = -flyer.vy * 0.7f + (Random.nextFloat() - 0.5f) * 150f,
                        color = if (Random.nextBoolean()) Color(0xFF00E5FF) else Color.White,
                        size = 4f + Random.nextFloat() * 8f,
                        maxLife = 0.4f
                    )
                )
            }
            if (hyperspaceTimer <= 0f) {
                isHyperspaceActive = false
                val randomCosmic = listOf(
                    Realm.ANDROMEDA,
                    Realm.NIGHT_FANTASY,
                    Realm.PLANETS_GALAXIES,
                    Realm.CYBERPUNK,
                    Realm.MATRIX
                ).random()
                teleportToRealm(randomCosmic)
                floatingFeedbacks.add(
                    FloatingFeedback(
                        x = centerX,
                        y = centerY - 140f,
                        text = "✨ Saída do Hiper-Espaço: ${randomCosmic.title}!",
                        color = randomCosmic.accentColor,
                        scale = 1.4f
                    )
                )
            }
        }

        // --- Predators (Cute & Ugly) Spawning & Interaction ---
        predatorSpawnTimer -= dt
        if (predatorSpawnTimer <= 0f && predators.size < 3) {
            predatorSpawnTimer = 8f + Random.nextFloat() * 7f
            val isCute = Random.nextBoolean()
            val chosenType = if (isCute) {
                listOf(PredatorType.CUTE_BAT, PredatorType.CUTE_JELLY, PredatorType.CUTE_BABY_DRAGON).random()
            } else {
                listOf(PredatorType.UGLY_ABYSS_BEAST, PredatorType.UGLY_MAGMA_WORM, PredatorType.UGLY_VOID_CRAWLER).random()
            }
            val angleRad = Math.toRadians((flyer.angleDeg + (Random.nextFloat() - 0.5f) * 70f).toDouble())
            val spawnDist = hypot(screenWidth, screenHeight) * 0.7f
            predators.add(
                Predator(
                    id = nextPredatorId++,
                    x = centerX + cos(angleRad).toFloat() * spawnDist,
                    y = centerY + sin(angleRad).toFloat() * spawnDist,
                    vx = (Random.nextFloat() - 0.5f) * 35f,
                    vy = (Random.nextFloat() - 0.5f) * 35f,
                    type = chosenType,
                    size = if (isCute) 28f else 36f
                )
            )
        }

        val predIter = predators.iterator()
        while (predIter.hasNext()) {
            val pred = predIter.next()
            pred.phase += dt * 3f
            pred.x += worldDx + pred.vx * dt
            pred.y += worldDy + pred.vy * dt

            val pDx = flyer.x - pred.x
            val pDy = flyer.y - pred.y
            val pDist = hypot(pDx, pDy)

            if (pred.isPacified) {
                pred.pacifiedTimer -= dt
                pred.vx -= (pDx / (pDist + 0.1f)) * 30f * dt
                pred.vy -= (pDy / (pDist + 0.1f)) * 30f * dt
                if (pred.pacifiedTimer <= 0f) {
                    predIter.remove()
                    continue
                }
            } else {
                if (pDist > 15f) {
                    val chaseSpeed = if (pred.type.category == PredatorCategory.CUTE) 70f else 90f
                    pred.vx += (pDx / pDist) * chaseSpeed * dt * 1.5f
                    pred.vy += (pDy / pDist) * chaseSpeed * dt * 1.5f
                    val predCurSpeed = hypot(pred.vx, pred.vy)
                    if (predCurSpeed > chaseSpeed) {
                        pred.vx = (pred.vx / predCurSpeed) * chaseSpeed
                        pred.vy = (pred.vy / predCurSpeed) * chaseSpeed
                    }
                }

                if (pDist < pred.size + 28f) {
                    if (pred.type.category == PredatorCategory.CUTE) {
                        // Cute predator gives cuddles and serenity!
                        pred.isPacified = true
                        pred.pacifiedTimer = 5f
                        audioEngine.playCutePredatorChime()
                        HapticHelper.performGentleTap(getApplication())
                        floatingFeedbacks.add(
                            FloatingFeedback(
                                x = flyer.x,
                                y = flyer.y - 65f,
                                text = "💖 ${pred.type.displayName} te abraçou! +40 Serenidade",
                                color = Color(0xFFFF4081),
                                scale = 1.3f
                            )
                        )
                        _uiState.update { it.copy(serenityScore = it.serenityScore + 40 * it.pointMultiplier) }
                    } else {
                        // Ugly predator: if soaring or flying fast, light pacifies it!
                        if (updatedSpeed > 300f || isFlapping) {
                            pred.isPacified = true
                            pred.pacifiedTimer = 6f
                            audioEngine.playLightChime(4)
                            HapticHelper.performGentleTap(getApplication())
                            floatingFeedbacks.add(
                                FloatingFeedback(
                                    x = flyer.x,
                                    y = flyer.y - 65f,
                                    text = "✨ Luz Zen! ${pred.type.displayName} Acalmado (+80)",
                                    color = Color(0xFFFFD54F),
                                    scale = 1.3f
                                )
                            )
                            shockwaves.add(
                                ShockwaveRing(
                                    x = pred.x,
                                    y = pred.y,
                                    color = Color(0xFFFFD54F),
                                    maxRadius = 130f
                                )
                            )
                            _uiState.update { it.copy(serenityScore = it.serenityScore + 80 * it.pointMultiplier) }
                        } else {
                            // Ugly predator roars and pushes gently back
                            pred.isPacified = true
                            pred.pacifiedTimer = 4f
                            audioEngine.playUglyPredatorRoar()
                            HapticHelper.performGentleTap(getApplication())
                            flyer.vx -= (pDx / (pDist + 0.1f)) * 170f
                            flyer.vy -= (pDy / (pDist + 0.1f)) * 170f
                            floatingFeedbacks.add(
                                FloatingFeedback(
                                    x = flyer.x,
                                    y = flyer.y - 65f,
                                    text = "👾 ${pred.type.displayName} rugiu! Voe rápido para iluminá-lo!",
                                    color = Color(0xFFFF5252),
                                    scale = 1.2f
                                )
                            )
                        }
                    }
                }
            }

            if (pDist > hypot(screenWidth, screenHeight) * 1.6f) {
                predIter.remove()
            }
        }

        // --- Black Holes Spawning & Gravitational Warp ---
        val isCosmicRealm = currentRealm in listOf(
            Realm.SPACE,
            Realm.PLANETS_GALAXIES,
            Realm.ANDROMEDA,
            Realm.TEMPEST,
            Realm.MATRIX,
            Realm.CYBERPUNK
        )
        if (isCosmicRealm) {
            blackHoleSpawnTimer -= dt
            if (blackHoleSpawnTimer <= 0f && blackHoles.isEmpty()) {
                blackHoleSpawnTimer = 22f + Random.nextFloat() * 14f
                val angleRad = Math.toRadians((flyer.angleDeg + (Random.nextFloat() - 0.5f) * 45f).toDouble())
                val spawnDist = hypot(screenWidth, screenHeight) * 0.65f
                blackHoles.add(
                    BlackHole(
                        id = nextBlackHoleId++,
                        x = centerX + cos(angleRad).toFloat() * spawnDist,
                        y = centerY + sin(angleRad).toFloat() * spawnDist,
                        radius = 62f,
                        pullRadius = 360f
                    )
                )
            }
        }

        val bhIter = blackHoles.iterator()
        while (bhIter.hasNext()) {
            val bh = bhIter.next()
            bh.phase += dt * 4f
            bh.rotation += dt * 65f
            bh.x += worldDx
            bh.y += worldDy

            val bhDx = bh.x - flyer.x
            val bhDy = bh.y - flyer.y
            val bhDist = hypot(bhDx, bhDy)

            // Gravitational pull toward black hole singularity
            if (bhDist < bh.pullRadius && bhDist > 10f) {
                val pullFactor = (1f - bhDist / bh.pullRadius) * 220f * dt
                flyer.vx += (bhDx / bhDist) * pullFactor
                flyer.vy += (bhDy / bhDist) * pullFactor
            }

            // Entering black hole singularity activates LIGHT SPEED!
            if (bhDist < bh.radius + 32f && !isHyperspaceActive) {
                isHyperspaceActive = true
                hyperspaceTimer = 3.2f
                audioEngine.playHyperspaceChime()
                HapticHelper.performGentleTap(getApplication())
                shockwaves.add(
                    ShockwaveRing(
                        x = flyer.x,
                        y = flyer.y,
                        color = Color(0xFF00E5FF),
                        maxRadius = 260f
                    )
                )
                floatingFeedbacks.add(
                    FloatingFeedback(
                        x = centerX,
                        y = centerY - 130f,
                        text = "⚡ VELOCIDADE DA LUZ ATIVADA! (+300)",
                        color = Color(0xFF00E5FF),
                        scale = 1.5f
                    )
                )
                _uiState.update { it.copy(serenityScore = it.serenityScore + 300 * it.pointMultiplier) }
                bhIter.remove()
                continue
            }

            if (bhDist > hypot(screenWidth, screenHeight) * 1.6f) {
                bhIter.remove()
            }
        }

        // --- Matrix Code Streams (active in Dimensão Matrix) ---
        if (currentRealm == Realm.MATRIX) {
            for (stream in matrixStreams) {
                stream.y += stream.speed * dt
                if (stream.y > screenHeight + 350f) {
                    stream.y = -350f
                    stream.x = Random.nextFloat() * screenWidth
                }
            }
        }

        // --- Flight Duration and Periodic DB Persistence ---
        flightDurationTimer += dt
        if (flightDurationTimer >= 1f) {
            flightDurationTimer -= 1f
            flightDurationSeconds++
        }
        autoSaveTimer -= dt
        if (autoSaveTimer <= 0f) {
            autoSaveTimer = 30f
            saveCurrentFlightToDb(showNotice = false)
        }

        // 3. Move Collectibles with the world & Check Collection
        val collectibleIter = collectibles.iterator()
        val margin = 120f

        while (collectibleIter.hasNext()) {
            val item = collectibleIter.next()

            // Move collectible with world
            item.x += worldDx
            item.y += worldDy

            // Subtle floating oscillation
            item.phase += dt * 2.2f
            item.y += sin(item.phase) * 12f * dt

            // Screen wrap so world is continuous
            if (item.x < -margin) {
                item.x = screenWidth + margin
                item.y = Random.nextFloat() * screenHeight
            } else if (item.x > screenWidth + margin) {
                item.x = -margin
                item.y = Random.nextFloat() * screenHeight
            }
            if (item.y < -margin) {
                item.y = screenHeight + margin
                item.x = Random.nextFloat() * screenWidth
            } else if (item.y > screenHeight + margin) {
                item.y = -margin
                item.x = Random.nextFloat() * screenWidth
            }

            // Proximity attraction to flyer (centered)
            val dx = flyer.x - item.x
            val dy = flyer.y - item.y
            val dist = hypot(dx, dy)

            if (dist < 180f) {
                val pull = (180f - dist) / 180f * 240f * dt
                item.x += (dx / dist) * pull
                item.y += (dy / dist) * pull
            }

            // Collection detection
            val collectRadius = item.baseRadius + 28f
            if (dist < collectRadius && !item.isCollected) {
                item.isCollected = true
                collectibleIter.remove()
                onItemCollected(item, flyer)
            }
        }

        // Keep collectible pool filled
        while (collectibles.size < 18) {
            spawnSingleCollectible()
        }

        // 4. Update Visual Effects (Sparkles, Rings, Floating texts) shifting with world
        updateVisualFeedback(dt, worldDx, worldDy)

        // 5. Update Ambient Particles (Breeze, stardust) streaming past
        updateAmbientParticles(dt, flyer, currentRealm)

        // 6. Combo timer countdown
        if (comboTimer > 0f) {
            comboTimer -= dt
            if (comboTimer <= 0f) {
                _uiState.update { it.copy(comboCount = 0) }
            }
        }

        // Cumulative camera displacement tracking the flight
        val newCameraX = state.cameraX + flyer.vx * dt
        val newCameraY = state.cameraY + flyer.vy * dt

        // Emit updated state
        _uiState.update { current ->
            current.copy(
                flyer = flyer,
                altitude = newAltitude,
                cameraX = newCameraX,
                cameraY = newCameraY,
                currentRealm = currentRealm,
                transitionAlpha = transitionAlpha,
                nextRealm = nextRealm,
                maxAltitude = maxAlt,
                lastRealmAnnounced = currentRealm,
                activePortal = activePortal,
                rainDrops = rainDrops.toList(),
                magmaEmbers = magmaEmbers.toList(),
                activePredators = predators.toList(),
                activeBlackHoles = blackHoles.toList(),
                matrixStreams = matrixStreams.toList(),
                isHyperspaceActive = isHyperspaceActive,
                hyperspaceProgress = hyperspaceProgressVal
            )
        }
    }

    private fun onItemCollected(item: Collectible, flyer: FlyerState) {
        val currentCombo = _uiState.value.comboCount + 1
        comboTimer = 3.5f // Reset combo window

        val scoreIncrement = 10 * currentCombo * _uiState.value.pointMultiplier

        // Audio Feedback
        when (item.type) {
            CollectibleType.LIGHT -> audioEngine.playLightChime(currentCombo)
            CollectibleType.BUBBLE -> audioEngine.playBubblePop()
            CollectibleType.FLOWER -> audioEngine.playFlowerChime()
        }

        // Haptic Feedback
        HapticHelper.performGentleTap(getApplication())

        // Visual Feedback 1: Explosive sparkle burst
        val particleCount = if (item.type == CollectibleType.LIGHT) 18 else 14
        for (i in 0 until particleCount) {
            val angle = Random.nextFloat() * 6.28f
            val pSpeed = 60f + Random.nextFloat() * 160f
            sparkles.add(
                SparkleParticle(
                    x = item.x,
                    y = item.y,
                    vx = cos(angle) * pSpeed + flyer.vx * 0.2f,
                    vy = sin(angle) * pSpeed + flyer.vy * 0.2f,
                    color = item.color,
                    size = 3.5f + Random.nextFloat() * 4.5f,
                    life = 0f,
                    maxLife = 0.65f + Random.nextFloat() * 0.4f
                )
            )
        }

        // Visual Feedback 2: Expanding luminous shockwave ripple
        shockwaves.add(
            ShockwaveRing(
                x = item.x,
                y = item.y,
                currentRadius = item.baseRadius,
                maxRadius = if (item.type == CollectibleType.LIGHT) 95f else 115f,
                color = item.color,
                alpha = 0.85f
            )
        )

        // Visual Feedback 3: Floating indicator text
        val feedbackText = when {
            item.type == CollectibleType.FLOWER -> "🌸 +1 Flor"
            currentCombo >= 4 -> "Harmonia x$currentCombo!"
            item.type == CollectibleType.LIGHT -> "+Luz (${scoreIncrement})"
            else -> "+Bolha (${scoreIncrement})"
        }

        floatingFeedbacks.add(
            FloatingFeedback(
                x = item.x,
                y = item.y - 15f,
                text = feedbackText,
                color = item.color
            )
        )

        // Visual Feedback 4: Flyer temporarily blooms with light
        flyer.glowIntensity = 2.4f

        // Update collections
        val newTotalCollections = _uiState.value.totalCollections + 1
        val newBubbles = if (item.type == CollectibleType.BUBBLE) _uiState.value.bubblesCollected + 1 else _uiState.value.bubblesCollected
        val newFlowers = if (item.type == CollectibleType.FLOWER) _uiState.value.flowersCollected + 1 else _uiState.value.flowersCollected
        val newLights = if (item.type == CollectibleType.LIGHT) _uiState.value.lightsCollected + 1 else _uiState.value.lightsCollected

        prefs.edit().putInt("total_collections", newTotalCollections).apply()

        // Check if a new skin was just unlocked!
        val newlyUnlockedSkins = SkinRepository.allSkins.filter { skin ->
            skin.requiredCollections > 0 &&
                    newTotalCollections == skin.requiredCollections &&
                    !_uiState.value.unlockedSkinIds.contains(skin.id)
        }

        val updatedUnlockedSet = _uiState.value.unlockedSkinIds.toMutableSet()
        newlyUnlockedSkins.forEach { newSkin ->
            updatedUnlockedSet.add(newSkin.id)
            floatingFeedbacks.add(
                FloatingFeedback(
                    x = screenWidth * 0.5f,
                    y = screenHeight * 0.28f,
                    text = "🎉 Nova Skin: ${newSkin.name}! (${newSkin.icon})",
                    color = newSkin.wingColor,
                    scale = 1.4f
                )
            )
            audioEngine.playRealmTransitionChime()
        }

        // Check if any universe realm is unlocked by bubbles!
        val updatedRealms = _uiState.value.unlockedRealms.toMutableSet()
        Realm.values().forEach { r ->
            if (r.bubblesToUnlock > 0 && newBubbles >= r.bubblesToUnlock && !updatedRealms.contains(r)) {
                updatedRealms.add(r)
                floatingFeedbacks.add(
                    FloatingFeedback(
                        x = screenWidth * 0.5f,
                        y = screenHeight * 0.24f,
                        text = "🌌 Universo Desbloqueado: ${r.title}!",
                        color = r.accentColor,
                        scale = 1.4f
                    )
                )
                audioEngine.playRealmTransitionChime()
            }
        }

        _uiState.update { current ->
            current.copy(
                lightsCollected = newLights,
                bubblesCollected = newBubbles,
                flowersCollected = newFlowers,
                totalCollections = newTotalCollections,
                unlockedSkinIds = updatedUnlockedSet,
                unlockedRealms = updatedRealms,
                serenityScore = current.serenityScore + scoreIncrement,
                comboCount = currentCombo
            )
        }
    }

    private fun updateVisualFeedback(dt: Float, worldDx: Float, worldDy: Float) {
        // Sparkles
        val sparkleIter = sparkles.iterator()
        while (sparkleIter.hasNext()) {
            val p = sparkleIter.next()
            p.life += dt
            if (p.life >= p.maxLife) {
                sparkleIter.remove()
                continue
            }
            p.x += p.vx * dt + worldDx
            p.y += p.vy * dt + worldDy
            p.vx *= 0.95f
            p.vy *= 0.95f
        }

        // Shockwaves
        val shockwaveIter = shockwaves.iterator()
        while (shockwaveIter.hasNext()) {
            val ring = shockwaveIter.next()
            ring.x += worldDx
            ring.y += worldDy
            ring.currentRadius += (ring.maxRadius - ring.currentRadius) * (dt * 9f)
            ring.alpha = (1f - (ring.currentRadius / ring.maxRadius)).coerceIn(0f, 1f)
            if (ring.alpha <= 0.05f || ring.currentRadius >= ring.maxRadius - 2f) {
                shockwaveIter.remove()
            }
        }

        // Floating Text
        val feedbackIter = floatingFeedbacks.iterator()
        while (feedbackIter.hasNext()) {
            val fb = feedbackIter.next()
            fb.life -= dt * 1.5f
            fb.offsetY -= dt * 45f
            fb.x += worldDx
            fb.y += worldDy
            fb.alpha = fb.life.coerceIn(0f, 1f)
            if (fb.life <= 0f) {
                feedbackIter.remove()
            }
        }
    }

    private fun updateAmbientParticles(dt: Float, flyer: FlyerState, realm: Realm) {
        val windX = -flyer.vx * 0.75f
        val windY = -flyer.vy * 0.75f

        for (p in ambientParticles) {
            p.phase += dt * 1.8f
            p.x += (p.vx * 35f + sin(p.phase) * 12f + windX) * dt
            p.y += (p.vy * 35f + cos(p.phase) * 10f + windY) * dt

            // Wrap around screen with margin
            if (p.x < -30f) p.x = screenWidth + 30f
            if (p.x > screenWidth + 30f) p.x = -30f
            if (p.y < -30f) p.y = screenHeight + 30f
            if (p.y > screenHeight + 30f) p.y = -30f
        }
    }

    private fun calculateRealmTransition(altitude: Float): Pair<Realm, Float> {
        return when {
            altitude < -3000f -> Pair(Realm.EARTH_CORE, 0f)
            altitude < -2800f -> Pair(Realm.MAGMA, (altitude - (-3000f)) / 200f)
            altitude < -2000f -> Pair(Realm.MAGMA, 0f)
            altitude < -1800f -> Pair(Realm.EARTH_MANTLE, (altitude - (-2000f)) / 200f)
            altitude < -1000f -> Pair(Realm.EARTH_MANTLE, 0f)
            altitude < -800f -> Pair(Realm.EARTH_CRUST, (altitude - (-1000f)) / 200f)
            altitude < 0f -> Pair(Realm.EARTH_CRUST, 0f)
            altitude < 200f -> Pair(Realm.FOREST, (altitude - 0f) / 200f)
            altitude < 1000f -> Pair(Realm.FOREST, 0f)
            altitude < 1200f -> Pair(Realm.SPACE, (altitude - 1000f) / 200f)
            altitude < 2200f -> Pair(Realm.SPACE, 0f)
            altitude < 2400f -> Pair(Realm.PLANETS_GALAXIES, (altitude - 2200f) / 200f)
            altitude < 3400f -> Pair(Realm.PLANETS_GALAXIES, 0f)
            altitude < 3600f -> Pair(Realm.CELESTIAL_CLOUDS, (altitude - 3400f) / 200f)
            altitude < 4800f -> Pair(Realm.CELESTIAL_CLOUDS, 0f)
            altitude < 5000f -> Pair(Realm.SUNSET_CITY, (altitude - 4800f) / 200f)
            altitude < 6300f -> Pair(Realm.SUNSET_CITY, 0f)
            altitude < 6500f -> Pair(Realm.CYBERPUNK, (altitude - 6300f) / 200f)
            altitude < 7800f -> Pair(Realm.CYBERPUNK, 0f)
            altitude < 8000f -> Pair(Realm.MATRIX, (altitude - 7800f) / 200f)
            altitude < 9300f -> Pair(Realm.MATRIX, 0f)
            altitude < 9500f -> Pair(Realm.MUSHROOMS, (altitude - 9300f) / 200f)
            altitude < 10800f -> Pair(Realm.MUSHROOMS, 0f)
            altitude < 11000f -> Pair(Realm.CLOUDS_SEA, (altitude - 10800f) / 200f)
            altitude < 12300f -> Pair(Realm.CLOUDS_SEA, 0f)
            altitude < 12500f -> Pair(Realm.MOUNTAINS, (altitude - 12300f) / 200f)
            altitude < 13800f -> Pair(Realm.MOUNTAINS, 0f)
            altitude < 14000f -> Pair(Realm.MARS, (altitude - 13800f) / 200f)
            altitude < 15300f -> Pair(Realm.MARS, 0f)
            altitude < 15500f -> Pair(Realm.TEMPEST, (altitude - 15300f) / 200f)
            altitude < 16800f -> Pair(Realm.TEMPEST, 0f)
            altitude < 17000f -> Pair(Realm.ANDROMEDA, (altitude - 16800f) / 200f)
            altitude < 18300f -> Pair(Realm.ANDROMEDA, 0f)
            altitude < 18500f -> Pair(Realm.MEDIEVAL, (altitude - 18300f) / 200f)
            altitude < 19800f -> Pair(Realm.MEDIEVAL, 0f)
            altitude < 20000f -> Pair(Realm.NIGHT_FANTASY, (altitude - 19800f) / 200f)
            else -> Pair(Realm.NIGHT_FANTASY, 0f)
        }
    }

    private fun startBreathingCycle() {
        breathingJob = viewModelScope.launch {
            while (isActive) {
                // Inhale 4 seconds
                val steps = 80
                for (i in 0..steps) {
                    val progress = i.toFloat() / steps
                    _uiState.update {
                        it.copy(
                            breathingPhase = "Inspire suavemente...",
                            breathingProgress = progress
                        )
                    }
                    delay(50)
                }

                // Hold 1 second
                _uiState.update { it.copy(breathingPhase = "Sustente a calma...") }
                delay(1000)

                // Exhale 4 seconds
                for (i in steps downTo 0) {
                    val progress = i.toFloat() / steps
                    _uiState.update {
                        it.copy(
                            breathingPhase = "Expire soltando as tensões...",
                            breathingProgress = progress
                        )
                    }
                    delay(50)
                }

                // Rest 1 second
                _uiState.update { it.copy(breathingPhase = "Relaxe...") }
                delay(1000)
            }
        }
    }
}
