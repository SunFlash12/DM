package com.dungeonmaster.app.di

import android.content.Context
import androidx.room.Room
import com.dungeonmaster.app.ai.DungeonMasterAI
import com.dungeonmaster.app.data.database.AppDatabase
import com.dungeonmaster.app.data.database.CharacterDao
import com.dungeonmaster.app.data.database.ChatMessageDao
import com.dungeonmaster.app.data.database.GameSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dungeon_master_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: AppDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideGameSessionDao(database: AppDatabase): GameSessionDao {
        return database.gameSessionDao()
    }

    @Provides
    @Singleton
    fun provideChatMessageDao(database: AppDatabase): ChatMessageDao {
        return database.chatMessageDao()
    }

    @Provides
    @Singleton
    fun provideDungeonMasterAI(): DungeonMasterAI {
        return DungeonMasterAI()
    }
}
