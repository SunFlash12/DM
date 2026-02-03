package com.dungeonmaster.app.data.repository

import com.dungeonmaster.app.data.database.CharacterDao
import com.dungeonmaster.app.data.database.ChatMessageDao
import com.dungeonmaster.app.data.database.GameSessionDao
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.models.ChatMessage
import com.dungeonmaster.app.data.models.GameSession
import com.dungeonmaster.app.data.models.MessageRole
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GameRepository @Inject constructor(
    private val characterDao: CharacterDao,
    private val gameSessionDao: GameSessionDao,
    private val chatMessageDao: ChatMessageDao
) {
    // Character operations
    fun getAllCharacters(): Flow<List<Character>> = characterDao.getAllCharacters()

    suspend fun getCharacterById(id: Long): Character? = characterDao.getCharacterById(id)

    fun getCharacterByIdFlow(id: Long): Flow<Character?> = characterDao.getCharacterByIdFlow(id)

    suspend fun saveCharacter(character: Character): Long = characterDao.insertCharacter(character)

    suspend fun updateCharacter(character: Character) = characterDao.updateCharacter(character)

    suspend fun deleteCharacter(character: Character) = characterDao.deleteCharacter(character)

    suspend fun updateCharacterHitPoints(characterId: Long, hp: Int) {
        characterDao.updateHitPoints(characterId, hp)
    }

    suspend fun updateCharacterGold(characterId: Long, gold: Int) {
        characterDao.updateGold(characterId, gold)
    }

    suspend fun updateCharacterExperience(characterId: Long, xp: Int, level: Int) {
        characterDao.updateExperience(characterId, xp, level)
    }

    // Game session operations
    fun getAllSessions(): Flow<List<GameSession>> = gameSessionDao.getAllSessions()

    fun getSessionsForCharacter(characterId: Long): Flow<List<GameSession>> =
        gameSessionDao.getSessionsForCharacter(characterId)

    suspend fun getSessionById(id: Long): GameSession? = gameSessionDao.getSessionById(id)

    fun getSessionByIdFlow(id: Long): Flow<GameSession?> = gameSessionDao.getSessionByIdFlow(id)

    suspend fun createSession(session: GameSession): Long = gameSessionDao.insertSession(session)

    suspend fun updateSession(session: GameSession) = gameSessionDao.updateSession(session)

    suspend fun deleteSession(session: GameSession) {
        chatMessageDao.deleteMessagesForSession(session.id)
        gameSessionDao.deleteSession(session)
    }

    suspend fun updateSessionLocation(sessionId: Long, location: String) {
        gameSessionDao.updateLocation(sessionId, location)
    }

    // Chat message operations
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> =
        chatMessageDao.getMessagesForSession(sessionId)

    suspend fun getRecentMessages(sessionId: Long, limit: Int = 20): List<ChatMessage> =
        chatMessageDao.getRecentMessages(sessionId, limit)

    suspend fun addMessage(message: ChatMessage): Long = chatMessageDao.insertMessage(message)

    suspend fun addUserMessage(sessionId: Long, content: String): Long {
        return addMessage(
            ChatMessage(
                sessionId = sessionId,
                role = MessageRole.USER,
                content = content
            )
        )
    }

    suspend fun addDMMessage(sessionId: Long, content: String): Long {
        return addMessage(
            ChatMessage(
                sessionId = sessionId,
                role = MessageRole.DM,
                content = content
            )
        )
    }

    suspend fun addSystemMessage(sessionId: Long, content: String): Long {
        return addMessage(
            ChatMessage(
                sessionId = sessionId,
                role = MessageRole.SYSTEM,
                content = content
            )
        )
    }

    suspend fun addNarratorMessage(sessionId: Long, content: String): Long {
        return addMessage(
            ChatMessage(
                sessionId = sessionId,
                role = MessageRole.NARRATOR,
                content = content
            )
        )
    }

    suspend fun deleteMessagesForSession(sessionId: Long) {
        chatMessageDao.deleteMessagesForSession(sessionId)
    }
}
