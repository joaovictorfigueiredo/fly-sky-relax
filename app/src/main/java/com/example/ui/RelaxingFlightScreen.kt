package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viewmodel.GameViewModel

@Composable
fun RelaxingFlightScreen(
    viewModel: GameViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        // Main Interactive 60fps Canvas
        GameCanvas(
            uiState = uiState,
            collectibles = viewModel.collectibles,
            sparkles = viewModel.sparkles,
            shockwaves = viewModel.shockwaves,
            ambientParticles = viewModel.ambientParticles,
            onSizeChanged = viewModel::onScreenSizeChanged,
            onTouchDown = viewModel::onTouchDown,
            onTouchMove = viewModel::onTouchMove,
            onTouchUp = viewModel::onTouchUp,
            onSoarStart = viewModel::onSoarStart,
            onSoarEnd = viewModel::onSoarEnd,
            modifier = Modifier.fillMaxSize()
        )

        // Peaceful Minimal UI Overlay
        GameOverlay(
            uiState = uiState,
            floatingFeedbacks = viewModel.floatingFeedbacks,
            onToggleWind = viewModel::toggleWind,
            onToggleMusic = viewModel::toggleMusic,
            onToggleLofi = viewModel::toggleLofi,
            onToggleBreathing = viewModel::toggleBreathingMode,
            onOpenInfo = { viewModel.setInfoDialogVisible(true) },
            onDismissInfo = { viewModel.setInfoDialogVisible(false) },
            onOpenSkins = { viewModel.setSkinsDialogVisible(true) },
            onDismissSkins = { viewModel.setSkinsDialogVisible(false) },
            onSelectSkin = viewModel::equipSkin,
            onOpenUniverses = { viewModel.setUniverseDialogVisible(true) },
            onDismissUniverses = { viewModel.setUniverseDialogVisible(false) },
            onTeleportToRealm = viewModel::teleportToRealm,
            onOpenFlowerShop = { viewModel.setFlowerShopVisible(true) },
            onDismissFlowerShop = { viewModel.setFlowerShopVisible(false) },
            onBuyMultiplier = viewModel::buyPointMultiplier,
            onOpenScores = { viewModel.setScoreHistoryDialogVisible(true) },
            onDismissScores = { viewModel.setScoreHistoryDialogVisible(false) },
            onSaveCurrentFlight = { viewModel.saveCurrentFlightToDb(showNotice = true) },
            onSoarStart = viewModel::onSoarStart,
            onSoarEnd = viewModel::onSoarEnd,
            modifier = Modifier.fillMaxSize()
        )
    }
}
