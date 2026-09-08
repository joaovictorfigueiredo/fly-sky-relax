package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "flight_records")
data class ScoreRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val serenityScore: Int,
    val maxAltitude: Float,
    val lightsCollected: Int,
    val bubblesCollected: Int,
    val flowersCollected: Int,
    val realmName: String,
    val durationSeconds: Long
)

@Entity(tableName = "game_stats")
data class GameStatsEntity(
    @PrimaryKey
    val id: Int = 1,
    val totalBubbles: Int = 0,
    val totalFlowers: Int = 0,
    val totalLights: Int = 0,
    val bestSerenity: Int = 0,
    val bestAltitude: Float = 0f,
    val pointMultiplier: Int = 1,
    val unlockedRealmsString: String = "FOREST,SPACE,PLANETS_GALAXIES,CELESTIAL_CLOUDS"
)
