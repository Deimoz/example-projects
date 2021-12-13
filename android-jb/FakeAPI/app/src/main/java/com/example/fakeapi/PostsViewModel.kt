package com.example.fakeapi

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel


class PostsViewModel(application: Application) : AndroidViewModel(application) {
    private var mPostsRepository: PostsRepository? = null
    private var mListLiveData: LiveData<List<PostClass>>? = null

    init {
        mPostsRepository = PostsRepository(application)
        mListLiveData = mPostsRepository!!.getAll()
    }

    fun getAll(): LiveData<List<PostClass>>? {
        return mListLiveData
    }

    fun deleteAll() {
        mPostsRepository?.deleteAll()
    }

    fun insert(post: PostClass?) {
        mPostsRepository?.insert(post)
    }

    fun delete(post: PostClass?) {
        mPostsRepository?.delete(post)
    }
}