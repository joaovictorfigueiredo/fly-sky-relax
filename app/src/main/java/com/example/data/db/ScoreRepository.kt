package com.example.data.db

import kotlinx.coroutines.flow.Flow

class ScoreRepository(private val scoreDao: ScoreDao) {
    val recentScores: Flow<List<ScoreRecord>> = scoreDao.getRecentScores()
    val topScores: Flow<List<ScoreRecord>> = scoreDao.getTopScores()
    val gameStats: Flow<GameStatsEntity?> = scoreDao.observeStats()

    suspend fun saveFlightRecord(record: ScoreRecord): Long {
        return scoreDao.insertRecord(record)
    }

    suspend fun loadStats(): GameStatsEntity? {
        return scoreDao.getStats()
    }

    suspend fun saveStats(stats: GameStatsEntity) {
        scoreDao.saveStats(stats)
    }
}
