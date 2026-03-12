package com.example.algoviz.di

import android.content.Context
import androidx.room.Room
import com.example.algoviz.data.local.AlgoVizDatabase
import com.example.algoviz.data.local.dao.TopicDao
import com.example.algoviz.data.local.dao.ProgressDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AlgoVizDatabase {
        return Room.databaseBuilder(
            context,
            AlgoVizDatabase::class.java,
            "algoviz_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideTopicDao(database: AlgoVizDatabase): TopicDao {
        return database.topicDao()
    }

    @Provides
    fun provideProgressDao(database: AlgoVizDatabase): ProgressDao {
        return database.progressDao()
    }
}
