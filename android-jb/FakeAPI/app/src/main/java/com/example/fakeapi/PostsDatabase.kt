package com.example.fakeapi

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [PostClass::class], version = 1)
abstract class PostsDatabase : RoomDatabase() {
    abstract fun postsDao() : PostsDao
}