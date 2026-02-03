package com.dungeonmaster.app.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dungeonmaster.app.data.models.Character
import com.dungeonmaster.app.data.models.ChatMessage
import com.dungeonmaster.app.data.models.GameSession

@Database(
    entities = [
        Character::class,
        GameSession::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun characterDao(): CharacterDao
    abstract fun gameSessionDao(): GameSessionDao
    abstract fun chatMessageDao(): ChatMessageDao
}
