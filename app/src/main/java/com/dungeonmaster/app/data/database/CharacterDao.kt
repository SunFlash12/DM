package com.dungeonmaster.app.data.database

import androidx.room.*
import com.dungeonmaster.app.data.models.Character
import kotlinx.coroutines.flow.Flow

@Dao
interface CharacterDao {
    @Query("SELECT * FROM characters ORDER BY lastPlayedAt DESC")
    fun getAllCharacters(): Flow<List<Character>>

    @Query("SELECT * FROM characters WHERE id = :id")
    suspend fun getCharacterById(id: Long): Character?

    @Query("SELECT * FROM characters WHERE id = :id")
    fun getCharacterByIdFlow(id: Long): Flow<Character?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character): Long

    @Update
    suspend fun updateCharacter(character: Character)

    @Delete
    suspend fun deleteCharacter(character: Character)

    @Query("DELETE FROM characters WHERE id = :id")
    suspend fun deleteCharacterById(id: Long)

    @Query("UPDATE characters SET currentHitPoints = :hp WHERE id = :id")
    suspend fun updateHitPoints(id: Long, hp: Int)

    @Query("UPDATE characters SET gold = :gold WHERE id = :id")
    suspend fun updateGold(id: Long, gold: Int)

    @Query("UPDATE characters SET experiencePoints = :xp, level = :level WHERE id = :id")
    suspend fun updateExperience(id: Long, xp: Int, level: Int)

    @Query("UPDATE characters SET lastPlayedAt = :timestamp WHERE id = :id")
    suspend fun updateLastPlayed(id: Long, timestamp: Long = System.currentTimeMillis())
}
