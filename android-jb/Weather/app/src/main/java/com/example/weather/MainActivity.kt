package com.example.weather

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val THEME = "THEME"
    }

    var isLightTheme = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isLightTheme) {
            setTheme(R.style.DarkAppTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_main)
        Log.i(TAG, "onCreate")
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(TAG, "onRestart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(TAG, "onResume")
    }

    override fun onStop() {
        super.onStop()
        Log.i(TAG, "onStart")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(THEME, isLightTheme)
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        isLightTheme = savedInstanceState.getBoolean(THEME)
        Log.i(TAG, "onRestoreInstanceState")
    }
}