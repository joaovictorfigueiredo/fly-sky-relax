package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.Realm
import com.example.model.RealmCategory

@Composable
fun UniverseSelectionDialog(
    currentRealm: Realm,
    bubblesCollected: Int,
    unlockedRealms: Set<Realm>,
    onTeleportToRealm: (Realm) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(RealmCategory.ALL) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF14121E).copy(alpha = 0.96f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                Color.White.copy(alpha = 0.2f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("universe_selection_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌌", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Universos & Dimensões",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Explore as 19 realidades e suas atmosferas únicas",
                            color = Color(0xFFB0BEC5),
                            fontSize = 11.5.sp
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_universe_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Currency & Progress summary banner
                Surface(
                    color = Color(0xFF006064).copy(alpha = 0.35f),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80DEEA).copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🫧", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Bolhas Coletadas:",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = "$bubblesCollected bolhas",
                            color = Color(0xFF80DEEA),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Category Filter Bar (Organized realms categorization)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    RealmCategory.values().forEach { cat ->
                        val isSelected = cat == selectedCategory
                        val count = if (cat == RealmCategory.ALL) {
                            Realm.values().size
                        } else {
                            Realm.values().count { it.category == cat }
                        }

                        Surface(
                            color = if (isSelected) cat.tagColor.copy(alpha = 0.35f) else Color.White.copy(alpha = 0.07f),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) cat.tagColor else Color.White.copy(alpha = 0.15f)
                            ),
                            modifier = Modifier
                                .clickable { selectedCategory = cat }
                                .testTag("category_filter_${cat.name}")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = cat.emoji, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${cat.title} ($count)",
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.65f),
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Filtered Realms List
                val filteredRealms = remember(selectedCategory) {
                    if (selectedCategory == RealmCategory.ALL) {
                        Realm.values().toList()
                    } else {
                        Realm.values().filter { it.category == selectedCategory }
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredRealms) { realm ->
                        val isUnlocked = unlockedRealms.contains(realm)
                        val isCurrent = realm == currentRealm

                        RealmItemCard(
                            realm = realm,
                            isUnlocked = isUnlocked,
                            isCurrent = isCurrent,
                            bubblesCollected = bubblesCollected,
                            onSelect = {
                                if (isUnlocked && !isCurrent) {
                                    onTeleportToRealm(realm)
                                    onDismiss()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RealmItemCard(
    realm: Realm,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    bubblesCollected: Int,
    onSelect: () -> Unit
) {
    Surface(
        color = if (isCurrent) {
            realm.accentColor.copy(alpha = 0.22f)
        } else if (isUnlocked) {
            Color(0xFF222030).copy(alpha = 0.88f)
        } else {
            Color(0xFF181722).copy(alpha = 0.65f)
        },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isCurrent) realm.accentColor else if (isUnlocked) Color.White.copy(alpha = 0.22f) else Color.White.copy(alpha = 0.08f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isUnlocked && !isCurrent, onClick = onSelect)
            .testTag("realm_card_${realm.name}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Top Row: Emoji, Title, Category Pill, Weather, Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(realm.skyTopColor, realm.skyBottomColor)
                                ),
                                shape = CircleShape
                            )
                            .border(1.2.dp, realm.accentColor.copy(alpha = 0.7f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getRealmEmoji(realm),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = realm.title,
                                color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Category and Rain tags
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            // Category Chip
                            Surface(
                                color = realm.category.tagColor.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "${realm.category.emoji} ${realm.category.title}",
                                    color = realm.category.tagColor,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                )
                            }

                            // Altitude Chip
                            Surface(
                                color = Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                val altText = if (realm.minAltitude < 0f) {
                                    "${realm.minAltitude.toInt()}m ~ ${realm.maxAltitude.toInt()}m"
                                } else {
                                    "${realm.minAltitude.toInt()}m ~ ${realm.maxAltitude.toInt()}m"
                                }
                                Text(
                                    text = "↕ $altText",
                                    color = Color(0xFFCFD8DC),
                                    fontSize = 9.5.sp,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                )
                            }

                            if (realm.hasRain) {
                                Surface(
                                    color = Color(0xFF0288D1).copy(alpha = 0.35f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.WaterDrop,
                                            contentDescription = "Chuva",
                                            tint = Color(0xFF80D8FF),
                                            modifier = Modifier.size(10.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Chuva",
                                            color = Color(0xFF80D8FF),
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Action status / button
                when {
                    isCurrent -> {
                        Surface(
                            color = realm.accentColor.copy(alpha = 0.28f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, realm.accentColor)
                        ) {
                            Text(
                                text = "Aqui agora",
                                color = realm.accentColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    isUnlocked -> {
                        Button(
                            onClick = onSelect,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = realm.accentColor.copy(alpha = 0.9f),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(text = "Entrar 🚀", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Bloqueado",
                                tint = Color(0xFFFFB74D),
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${realm.bubblesToUnlock} 🫧",
                                color = Color(0xFFFFB74D),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // Subtitle
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = realm.subtitle,
                color = Color(0xFFECEFF1).copy(alpha = if (isUnlocked) 0.9f else 0.5f),
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium
            )

            // Rich Atmospheric Lore Description
            if (realm.loreDescription.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = Color.Black.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, realm.accentColor.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "❝ ${realm.loreDescription} ❞",
                        color = if (isUnlocked) Color(0xFFE0E0E0) else Color(0xFF9E9E9E),
                        fontSize = 10.5.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 14.5.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }

            // Key Feature Tags
            if (realm.features.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    realm.features.forEach { feat ->
                        Surface(
                            color = realm.accentColor.copy(alpha = 0.14f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, realm.accentColor.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "✦ $feat",
                                color = realm.accentColor.copy(alpha = 0.95f),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            // Progress bar if locked
            if (!isUnlocked && realm.bubblesToUnlock > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                val progress = (bubblesCollected.toFloat() / realm.bubblesToUnlock).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color(0xFF80DEEA),
                    trackColor = Color.White.copy(alpha = 0.1f),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Faltam ${maxOf(0, realm.bubblesToUnlock - bubblesCollected)} bolhas para desbloquear",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

private fun getRealmEmoji(realm: Realm): String {
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
