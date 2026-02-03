package com.dungeonmaster.app.data.database

import androidx.room.*
import com.dungeonmaster.app.data.models.GameSession
import kotlinx.coroutines.flow.Flow

@Dao
interface GameSessionDao {
    @Query("SELECT * FROM game_sessions ORDER BY lastPlayedAt DESC")
    fun getAllSessions(): Flow<List<GameSession>>

    @Query("SELECT * FROM game_sessions WHERE characterId = :characterId ORDER BY lastPlayedAt DESC")
    fun getSessionsForCharacter(characterId: Long): Flow<List<GameSession>>

    @Query("SELECT * FROM game_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): GameSession?

    @Query("SELECT * FROM game_sessions WHERE id = :id")
    fun getSessionByIdFlow(id: Long): Flow<GameSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: GameSession): Long

    @Update
    suspend fun updateSession(session: GameSession)

    @Delete
    suspend fun deleteSession(session: GameSession)

    @Query("DELETE FROM game_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)

    @Query("UPDATE game_sessions SET lastPlayedAt = :timestamp WHERE id = :id")
    suspend fun updateLastPlayed(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE game_sessions SET currentLocation = :location WHERE id = :id")
    suspend fun updateLocation(id: Long, location: String)

    @Query("UPDATE game_sessions SET campaignSummary = :summary WHERE id = :id")
    suspend fun updateCampaignSummary(id: Long, summary: String)
}
