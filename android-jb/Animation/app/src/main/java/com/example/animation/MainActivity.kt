package com.example.animation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        text_anim.startAnimation(
            AlphaAnimation(0.3f, 1f).apply {
                repeatCount = AlphaAnimation.INFINITE
                duration = 2000
                repeatMode = AlphaAnimation.REVERSE
            }
        )
    }

    override fun onStop() {
        super.onStop()
        text_anim.animation.cancel()
    }
}