package com.example.algoviz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.algoviz.data.local.dao.TopicDao
import com.example.algoviz.data.local.dao.ProgressDao
import com.example.algoviz.data.local.entity.TopicEntity
import com.example.algoviz.data.local.entity.ProgressEntity

@Database(
    entities = [
        TopicEntity::class,
        ProgressEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AlgoVizDatabase : RoomDatabase() {
    abstract fun topicDao(): TopicDao
    abstract fun progressDao(): ProgressDao
}
