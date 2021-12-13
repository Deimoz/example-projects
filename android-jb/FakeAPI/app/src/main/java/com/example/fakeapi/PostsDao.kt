package com.example.fakeapi

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PostsDao {
    @Query("SELECT * FROM PostClass")
    fun getAll() : LiveData<List<PostClass>>

    @Query("SELECT * FROM PostClass WHERE id = :id")
    fun getById(id : Int) : LiveData<PostClass>

    @Insert
    fun insert(post : PostClass)

    @Delete
    fun delete(post : PostClass)

    @Query("DELETE FROM PostClass")
    fun deleteAll()
}
