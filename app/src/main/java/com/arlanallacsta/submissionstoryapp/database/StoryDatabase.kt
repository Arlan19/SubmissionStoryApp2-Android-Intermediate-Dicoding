package com.arlanallacsta.submissionstoryapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =[Story::class, RemoteKeys::class],
    version = 1,
    exportSchema = false
)
abstract class StoryDatabase : RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}