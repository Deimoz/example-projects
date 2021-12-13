package com.example.fakeapi

import android.app.Application
import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.room.Room


class PostsRepository(application: Application) {
    private var mPostsDao: PostsDao? = null
    private var mAllData: LiveData<List<PostClass>>? = null

    init {
        mPostsDao = Room.databaseBuilder(application, PostsDatabase::class.java, "database").build().postsDao()
        mAllData = mPostsDao!!.getAll()
    }

    fun getAll(): LiveData<List<PostClass>>? {
        return mAllData
    }

    fun deleteAll() {
        deleteAllAsyncTask(mPostsDao).execute()
    }

    private class deleteAllAsyncTask internal constructor(dao: PostsDao?) :
        AsyncTask<Void?, Void?, Void?>() {
        private val mAsyncTaskDao: PostsDao?
        override fun doInBackground(vararg params: Void?): Void? {
            if (mAsyncTaskDao != null) {
                mAsyncTaskDao.deleteAll()
            }
            return null
        }

        init {
            mAsyncTaskDao = dao
        }
    }

    fun insert(dataItem: PostClass?) {
        insertAsyncTask(mPostsDao).execute(dataItem)
    }

    private class insertAsyncTask internal constructor(dao: PostsDao?) :
        AsyncTask<PostClass?, Void?, Void?>() {
        private val mAsyncTaskDao: PostsDao?
        override fun doInBackground(vararg params: PostClass?): Void? {
            if (mAsyncTaskDao != null) {
                params[0]?.let { mAsyncTaskDao.insert(it) }
            }
            return null
        }

        init {
            mAsyncTaskDao = dao
        }
    }

    fun delete(post: PostClass?) {
        deleteAsyncTask(mPostsDao).execute(post)
    }

    private class deleteAsyncTask internal constructor(dao: PostsDao?) :
        AsyncTask<PostClass?, Void?, Void?>() {
        private val mAsyncTaskDao: PostsDao?
        override fun doInBackground(vararg params: PostClass?): Void? {
            params[0]?.let {
                if (mAsyncTaskDao != null) {
                    mAsyncTaskDao.delete(it)
                }
            }
            return null
        }

        init {
            mAsyncTaskDao = dao
        }
    }
}