package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {
    @Query("SELECT * FROM flight_records ORDER BY timestamp DESC LIMIT 30")
    fun getRecentScores(): Flow<List<ScoreRecord>>

    @Query("SELECT * FROM flight_records ORDER BY serenityScore DESC LIMIT 15")
    fun getTopScores(): Flow<List<ScoreRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: ScoreRecord): Long

    @Query("SELECT * FROM game_stats WHERE id = 1")
    suspend fun getStats(): GameStatsEntity?

    @Query("SELECT * FROM game_stats WHERE id = 1")
    fun observeStats(): Flow<GameStatsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveStats(stats: GameStatsEntity)
}
