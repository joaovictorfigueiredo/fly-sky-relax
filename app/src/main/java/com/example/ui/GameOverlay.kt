package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlightTakeoff
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FloatingFeedback
import com.example.model.Realm
import com.example.model.Skin
import com.example.viewmodel.GameUiState

@Composable
fun GameOverlay(
    uiState: GameUiState,
    floatingFeedbacks: List<FloatingFeedback>,
    onToggleWind: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleLofi: () -> Unit,
    onToggleBreathing: () -> Unit,
    onOpenInfo: () -> Unit,
    onDismissInfo: () -> Unit,
    onOpenSkins: () -> Unit,
    onDismissSkins: () -> Unit,
    onSelectSkin: (Skin) -> Unit,
    onOpenUniverses: () -> Unit,
    onDismissUniverses: () -> Unit,
    onTeleportToRealm: (Realm) -> Unit,
    onOpenFlowerShop: () -> Unit,
    onDismissFlowerShop: () -> Unit,
    onBuyMultiplier: () -> Unit,
    onOpenScores: () -> Unit,
    onDismissScores: () -> Unit,
    onSaveCurrentFlight: () -> Unit,
    onSoarStart: () -> Unit,
    onSoarEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // 1. Floating In-World Feedback Labels
        floatingFeedbacks.forEach { fb ->
            Box(
                modifier = Modifier
                    .offset { IntOffset(fb.x.toInt() - 60, (fb.y + fb.offsetY).toInt()) }
                    .alpha(fb.alpha)
                    .scale(fb.scale)
            ) {
                Text(
                    text = fb.text,
                    color = fb.color,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            color = Color.Black.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // 2. Top Header: Realm Badge & Collectibles Count
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Realm and Altitude Indicator (Clickable to open Universes Dialog!)
                Surface(
                    onClick = onOpenUniverses,
                    color = Color.Black.copy(alpha = 0.32f),
                    shape = RoundedCornerShape(20.dp),
                    border = border(1.dp, uiState.currentRealm.accentColor.copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("realm_altitude_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = getRealmIcon(uiState.currentRealm),
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = uiState.currentRealm.title,
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "▾",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "${uiState.altitude.toInt()} m",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Audio and Controls Menu
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Lo-Fi chill beats toggle
                    IconButton(
                        onClick = onToggleLofi,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (uiState.isLofiActive) Color(0xFFFF4081).copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.25f),
                                CircleShape
                            )
                            .border(
                                1.dp,
                                if (uiState.isLofiActive) Color(0xFFFF80AB) else Color.White.copy(alpha = 0.15f),
                                CircleShape
                            )
                            .testTag("lofi_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = "Alternar Lo-Fi Beats",
                            tint = if (uiState.isLofiActive) Color(0xFFFF80AB) else Color.White.copy(alpha = 0.45f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Universes exploration dialog button
                    IconButton(
                        onClick = onOpenUniverses,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color(0xFF7C4DFF).copy(alpha = 0.35f),
                                CircleShape
                            )
                            .border(1.dp, Color(0xFFB388FF).copy(alpha = 0.6f), CircleShape)
                            .testTag("universes_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Menu de Universos",
                            tint = Color(0xFFD1C4E9),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Flower Multiplier Shop button
                    IconButton(
                        onClick = onOpenFlowerShop,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color(0xFFFF4081).copy(alpha = 0.3f),
                                CircleShape
                            )
                            .border(1.dp, Color(0xFFFF80AB).copy(alpha = 0.6f), CircleShape)
                            .testTag("flower_shop_button")
                    ) {
                        Text(text = "🌸", fontSize = 16.sp)
                    }

                    // Ambient music toggle
                    IconButton(
                        onClick = onToggleMusic,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                            .testTag("music_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isMusicMuted) Icons.Default.MusicOff else Icons.Default.MusicNote,
                            contentDescription = "Alternar trilha sonora",
                            tint = if (uiState.isMusicMuted) Color.White.copy(alpha = 0.45f) else Color(0xFFFFD180),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Wind sound toggle
                    IconButton(
                        onClick = onToggleWind,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                            .testTag("wind_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (uiState.isWindMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.Default.Air,
                            contentDescription = "Alternar som do vento",
                            tint = if (uiState.isWindMuted) Color.White.copy(alpha = 0.45f) else Color(0xFFB2DFDB),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Guided breathing toggle
                    IconButton(
                        onClick = onToggleBreathing,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                if (uiState.isBreathingMode) Color(0xFF80CBC4).copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.25f),
                                CircleShape
                            )
                            .testTag("breathing_toggle_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "Modo Respiração",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Skins Sanctuary Button
                    IconButton(
                        onClick = onOpenSkins,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color(0xFFFFD54F).copy(alpha = 0.28f),
                                CircleShape
                            )
                            .border(1.dp, Color(0xFFFFE082).copy(alpha = 0.6f), CircleShape)
                            .testTag("skins_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Menu de Skins",
                            tint = Color(0xFFFFECB3),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Hall of Fame & Saved Scores Button
                    IconButton(
                        onClick = onOpenScores,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Color(0xFFFFB300).copy(alpha = 0.28f),
                                CircleShape
                            )
                            .border(1.dp, Color(0xFFFFD54F).copy(alpha = 0.7f), CircleShape)
                            .testTag("scores_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Recordes e Pontuações Salvas",
                            tint = Color(0xFFFFE082),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Info Dialog
                    IconButton(
                        onClick = onOpenInfo,
                        modifier = Modifier
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.25f), CircleShape)
                            .testTag("info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Informações do jogo",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Score Pills: Lights, Bubbles, Flowers & Multiplier
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Lights collected
                Surface(
                    color = Color.Black.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, Color(0xFFFFF59D).copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("lights_counter_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "✨", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${uiState.lightsCollected}",
                            color = Color(0xFFFFF9C4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Bubbles collected (Clickable to open Universes dialog!)
                Surface(
                    onClick = onOpenUniverses,
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, Color(0xFF80DEEA).copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("bubbles_counter_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🫧", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${uiState.bubblesCollected}",
                            color = Color(0xFFE0F7FA),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Flowers collected (Clickable to buy multiplier!)
                Surface(
                    onClick = onOpenFlowerShop,
                    color = Color.Black.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, Color(0xFFFF80AB).copy(alpha = 0.6f)),
                    modifier = Modifier.testTag("flowers_counter_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🌸", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${uiState.flowersCollected}",
                            color = Color(0xFFF8BBD0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Multiplier chip
                Surface(
                    onClick = onOpenFlowerShop,
                    color = if (uiState.pointMultiplier > 1) Color(0xFFFFD54F).copy(alpha = 0.28f) else Color.Black.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, if (uiState.pointMultiplier > 1) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f)),
                    modifier = Modifier.testTag("multiplier_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "⚡", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${uiState.pointMultiplier}x",
                            color = if (uiState.pointMultiplier > 1) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Serenity Score
                Surface(
                    color = Color.Black.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, Color(0xFFCE93D8).copy(alpha = 0.4f)),
                    modifier = Modifier.testTag("serenity_counter_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🕊️", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${uiState.serenityScore}",
                            color = Color(0xFFF3E5F5),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Atmospheric Rain tag if current realm has rain
            if (uiState.currentRealm.hasRain) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = Color(0xFF0288D1).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    border = border(0.8.dp, Color(0xFF80D8FF).copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "Chuva",
                            tint = Color(0xFF80D8FF),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Chuva suave caindo nesta dimensão",
                            color = Color(0xFFE1F5FE),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equipped Skin Quick Badge
                Surface(
                    onClick = onOpenSkins,
                    color = uiState.equippedSkin.auraColor.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(16.dp),
                    border = border(0.8.dp, uiState.equippedSkin.wingColor.copy(alpha = 0.7f)),
                    modifier = Modifier.testTag("equipped_skin_badge_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = uiState.equippedSkin.icon, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.equippedSkin.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Combo Badge
                if (uiState.comboCount > 1) {
                    Surface(
                        color = Color(0xFFFFD54F).copy(alpha = 0.35f),
                        shape = RoundedCornerShape(14.dp),
                        border = border(1.dp, Color(0xFFFFE082)),
                        modifier = Modifier.testTag("combo_chip")
                    ) {
                        Text(
                            text = "Harmonia x${uiState.comboCount}",
                            color = Color(0xFFFFF9C4),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }

        // 3. Center: Guided Breathing Circle (when active)
        AnimatedVisibility(
            visible = uiState.isBreathingMode,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            val scale by animateFloatAsState(
                targetValue = 0.8f + uiState.breathingProgress * 0.45f,
                animationSpec = tween(durationMillis = 300),
                label = "breathingScale"
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .scale(scale)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFE0F2F1).copy(alpha = 0.45f),
                                    Color(0xFF80CBC4).copy(alpha = 0.2f),
                                    Color.Transparent
                                )
                            ),
                            shape = CircleShape
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Spa,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.85f),
                        modifier = Modifier.size(46.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = uiState.breathingPhase,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(
                            Color.Black.copy(alpha = 0.35f),
                            RoundedCornerShape(16.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                )
            }
        }

        // 4. Bottom Controls: Altitude Progress Pill & Soar / Planar Button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Altitude Level Bar with Realm Milestones
            AltitudeStatusBar(currentAltitude = uiState.altitude)

            Spacer(modifier = Modifier.height(12.dp))

            // Guidance & Soar Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Toque e arraste para guiar o voo ✦",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )

                // Soar Boost Button
                Surface(
                    color = Color.White.copy(alpha = 0.25f),
                    shape = RoundedCornerShape(24.dp),
                    border = border(1.dp, Color.White.copy(alpha = 0.55f)),
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { /* Tap handled via pointer input below */ }
                        )
                        .testTag("soar_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlightTakeoff,
                            contentDescription = "Subir e planar",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Planar",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 5. Zen Info / About Dialog
        if (uiState.showInfoDialog) {
            AlertDialog(
                onDismissRequest = onDismissInfo,
                confirmButton = {
                    TextButton(onClick = onDismissInfo) {
                        Text("Continuar Voando", color = Color(0xFF5B427F))
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🕊️ Voo Relaxante", fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Um espaço de paz e descompressão criado para desacelerar a mente.",
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• Navegação Suave: Toque e deslize o dedo para conduzir o espírito alado com inércia serena.",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Mudança de Cenário: Suba ou desça para transitar entre a Floresta (0m), Espaço Sideral (1200m), Planetas & Galáxias (2400m) e o Reino Celestial (3600m+).",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Coleta e Sons: Aproxime-se das luzes douradas e bolhas iridescentes para ouvir acordes cristalinos e sentir as ondas de paz.",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Som do Vento & 432Hz: Paisagem sonora sintetizada em tempo real para alívio do estresse.",
                            fontSize = 13.sp
                        )
                        Text(
                            text = "• Skins & Espíritos: Toque no ícone de paleta 🎨 para ver e equipar até 20 skins liberadas por coletas (50, 100, 1000, 2000, etc.)!",
                            fontSize = 13.sp
                        )
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }

        // 6. Skins Menu Dialog
        if (uiState.showSkinsDialog) {
            SkinsMenuDialog(
                currentSkinId = uiState.equippedSkin.id,
                totalCollections = uiState.totalCollections,
                unlockedSkinIds = uiState.unlockedSkinIds,
                onSelectSkin = onSelectSkin,
                onDismiss = onDismissSkins
            )
        }

        // 7. Universe & Dimensions Multiverse Dialog
        if (uiState.showUniverseDialog) {
            UniverseSelectionDialog(
                currentRealm = uiState.currentRealm,
                bubblesCollected = uiState.bubblesCollected,
                unlockedRealms = uiState.unlockedRealms,
                onTeleportToRealm = onTeleportToRealm,
                onDismiss = onDismissUniverses
            )
        }

        // 8. Flower Multiplier Upgrade Shop Dialog
        if (uiState.showFlowerShopDialog) {
            FlowerShopDialog(
                flowersCollected = uiState.flowersCollected,
                pointMultiplier = uiState.pointMultiplier,
                multiplierCost = uiState.multiplierCost,
                onBuyMultiplier = onBuyMultiplier,
                onDismiss = onDismissFlowerShop
            )
        }

        // 9. Hall of Fame & Saved Scores Dialog
        if (uiState.showScoreHistoryDialog) {
            SavedScoresDialog(
                uiState = uiState,
                savedScores = uiState.savedScores,
                bestSavedScore = uiState.bestSavedScore,
                onSaveCurrentFlight = onSaveCurrentFlight,
                onDismiss = onDismissScores
            )
        }
    }
}

@Composable
private fun AltitudeStatusBar(currentAltitude: Float) {
    // Altitude spans from Subterranean Earth Core (-3500m) to Night Fantasy (21500m)
    val progress = ((currentAltitude + 3500f) / 25000f).coerceIn(0f, 1f)

    Surface(
        color = Color.Black.copy(alpha = 0.28f),
        shape = RoundedCornerShape(16.dp),
        border = border(0.8.dp, Color.White.copy(alpha = 0.25f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            // Milestone Labels across all depths & heights
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("🔥 Núcleo", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("🌳 Floresta", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("🌌 Espaço", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("🌆 Ciberpunk", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("💻 Matrix", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("🍄 Cogumelos", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
                Text("🌙 Fantasia", fontSize = 9.sp, color = Color.White.copy(alpha = 0.75f))
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF3D00),
                                    Color(0xFFFF9100),
                                    Color(0xFF00E676),
                                    Color(0xFF80CBC4),
                                    Color(0xFFB39DDB),
                                    Color(0xFFFFCC80),
                                    Color(0xFFFF80AB),
                                    Color(0xFF00E5FF),
                                    Color(0xFF00E676),
                                    Color(0xFFEA80FC)
                                )
                            ),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}

private fun getRealmIcon(realm: Realm): String {
    return when (realm) {
        Realm.EARTH_CORE -> "🔥"
        Realm.MAGMA -> "🌋"
        Realm.EARTH_MANTLE -> "🧱"
        Realm.EARTH_CRUST -> "💎"
        Realm.FOREST -> "🌳"
        Realm.SPACE -> "🌌"
        Realm.PLANETS_GALAXIES -> "🪐"
        Realm.CELESTIAL_CLOUDS -> "☁️"
        Realm.SUNSET_CITY -> "🌆"
        Realm.CYBERPUNK -> "🏙️"
        Realm.MATRIX -> "💻"
        Realm.MUSHROOMS -> "🍄"
        Realm.CLOUDS_SEA -> "⛅"
        Realm.MOUNTAINS -> "🏔️"
        Realm.MARS -> "🔴"
        Realm.TEMPEST -> "⚡"
        Realm.ANDROMEDA -> "🌀"
        Realm.MEDIEVAL -> "🏰"
        Realm.NIGHT_FANTASY -> "🌙"
    }
}

private fun border(width: androidx.compose.ui.unit.Dp, color: Color) =
    androidx.compose.foundation.BorderStroke(width, color)
