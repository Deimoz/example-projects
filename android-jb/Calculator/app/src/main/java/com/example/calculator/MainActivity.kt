package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val EXPRESSION = "EXPRESSION"
        const val HASERROR = "HASERROR"
    }

    var hasError: Boolean = false
    lateinit var equation: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        equation = findViewById(R.id.equation)
        Log.i(TAG, "onCreate")
    }

    fun buttonClick(view: View) {
        if (hasError) {
            equation.text = ""
            hasError = false
        }
        val button: Button = findViewById(view.getId()) as Button
        val txt: String = button.text.toString()
        when (view.getId()) {
            R.id.buttonRes -> {
                val parser = ExpressionParser(equation.text.toString())
                equation.text = parser.parse()
                hasError = parser.currentState != CurrentState.NORMALEXPRESSION
            }
            R.id.buttonClear -> {
                deleteSymbols(1)
            }
            R.id.buttonAllClear -> {
                deleteSymbols(equation.text.length)
            } else -> {
                addSymbol(txt)
            }
        }
        Log.i(TAG, "${txt} pushed")
    }

    private fun deleteSymbols(amount: Int) {
        val str: String = equation.text.toString()
        if (str.length > 0) {
            equation.text = str.substring(0, str.length - amount)
        }
        Log.i(TAG, "${amount} symbols deleted")
    }

    private fun addSymbol(element: String) {
        equation.text = equation.text.toString().plus(element)
        Log.i(TAG, "${element} added")
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
        outState.putString(EXPRESSION, equation.text.toString())
        outState.putBoolean(HASERROR, hasError)
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        equation.text = savedInstanceState.getString(EXPRESSION)
        hasError = savedInstanceState.getBoolean(HASERROR)
        Log.i(TAG, "onRestoreInstanceState")
    }
}