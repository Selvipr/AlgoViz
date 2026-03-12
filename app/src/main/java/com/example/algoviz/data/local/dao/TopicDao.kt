package com.example.algoviz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.algoviz.data.local.entity.TopicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY order_index ASC")
    fun getAllTopics(): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE category = :category ORDER BY order_index ASC")
    fun getTopicsByCategory(category: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE track = :track ORDER BY order_index ASC")
    fun getTopicsByTrack(track: String): Flow<List<TopicEntity>>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: String): TopicEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicEntity>)

    @Query("DELETE FROM topics")
    suspend fun deleteAll()
}
