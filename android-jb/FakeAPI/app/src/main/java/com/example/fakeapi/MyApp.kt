package com.example.fakeapi

import android.app.Application
import androidx.room.Room
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.internal.Internal.instance
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class MyApp : Application() {
    companion object {
        lateinit var retrofit: FakeApiService
    }
    fun getRetrofit() : FakeApiService {
        return retrofit
    }
    override fun onCreate() {
        super.onCreate()
        retrofit = Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(FakeApiService::class.java)
    }
}