package com.example.networking

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import kotlin.concurrent.thread

class FullPhotoActivity : AppCompatActivity() {
    var bitmap: Bitmap? = null
    var description: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_photo)
        var image: ImageView = findViewById(R.id.full_photo)
        var text: TextView = findViewById(R.id.full_photo_description)
        if (savedInstanceState == null) {
            bitmap = DownloadBitmap().execute(intent.getStringExtra(MainActivity.PHOTOURL)).get()
            description = intent.getStringExtra(MainActivity.PHOTODESCRIPTION)
        } else {
            bitmap = savedInstanceState.getParcelable("photo")
            description = savedInstanceState.getString("description")
        }
        image.setImageBitmap(bitmap)
        text.text = description
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("photo", bitmap)
        outState.putString("description", description)
    }
}