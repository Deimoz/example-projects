package com.example.networking

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.networking.photosJsonClass.PhotosJson
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val PHOTOURL = "PHOTOURL"
        const val PHOTODESCRIPTION = "PHOTODESCRIPTION"
    }

    var dService: DownloadService? = null
    private var isBound = false

    var serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val localService = service as DownloadService.LocalService
            dService = localService.getService()
            if (dService != null) {
                val viewManager = LinearLayoutManager(this@MainActivity)
                listImages.apply {
                    layoutManager = viewManager
                    adapter = dService?.takePhotos()?.let {
                        PhotoAdapter(it) {
                            val intent = Intent(this@MainActivity, FullPhotoActivity::class.java)
                            intent.putExtra(PHOTOURL, it.fullPhotoURL)
                            intent.putExtra(PHOTODESCRIPTION, it.description)
                            startActivity(intent)
                        }
                    }
                }
            }
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val serviceClass = DownloadService::class.java
        val intent = Intent(this, serviceClass)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    override fun onDestroy() {
        super.onDestroy()
    }
}