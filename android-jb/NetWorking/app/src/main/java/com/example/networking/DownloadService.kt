package com.example.networking

import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Binder
import android.os.IBinder
import com.example.networking.photosJsonClass.PhotosJson
import com.google.gson.Gson
import java.net.URL

class DownloadService : Service() {
    private var jsonItems: PhotosJson? = null
    var photos: MutableList<Photo> = mutableListOf<Photo>()

    private val mBinder: IBinder = LocalService()

    inner class LocalService : Binder() {
        fun getService() : DownloadService {
            return this@DownloadService
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (photos.size == 0) {
            photos = mutableListOf<Photo>()
            Gson().fromJson<PhotosJson>(
                DownloadManyItems().execute("https://api.unsplash.com/photos/?client_id=iXQD_uZJYZDXWV2l3AGdR2dT1C8btcaJkYl457LWWng")
                    .get(),
                PhotosJson::class.java
            ).forEach() {
                photos.add(
                    Photo(
                        DownloadBitmap().execute(it.urls.thumb).get(),
                        it.urls.regular,
                        it.description
                    )
                )
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    fun takeJson() : PhotosJson? {
        return jsonItems
    }

    fun takePhotos() : List<Photo> {
        return photos
    }
}

private class DownloadManyItems : AsyncTask<String, Unit, String>() {
    override fun doInBackground(vararg params: String?): String {
        val jsonItems:String = URL(params[0]).openConnection().run() {
            connect()
            getInputStream().bufferedReader().readLines().joinToString("")
        }
        return jsonItems
    }
}

class DownloadBitmap : AsyncTask<String, Unit, Bitmap>() {
    override fun doInBackground(vararg params: String?): Bitmap {
        return BitmapFactory.decodeStream(URL(params[0]).openConnection().getInputStream())
    }
}