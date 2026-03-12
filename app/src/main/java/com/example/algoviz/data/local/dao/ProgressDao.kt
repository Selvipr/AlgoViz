package com.example.algoviz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoviz.data.local.entity.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("SELECT * FROM progress WHERE user_id = :userId")
    fun getProgressByUser(userId: String): Flow<List<ProgressEntity>>

    @Query("SELECT * FROM progress WHERE user_id = :userId AND topic_id = :topicId")
    suspend fun getProgress(userId: String, topicId: String): ProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: ProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(progress: List<ProgressEntity>)
}
