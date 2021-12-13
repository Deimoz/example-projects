package org.csc.kotlin2021.mastermind

import com.google.gson.Gson
import java.io.File
import java.io.FileNotFoundException
import java.lang.StringBuilder
import kotlin.random.Random

const val SETTINGS_FILE = "settings.json"

class GameSettings {
    private var options: Options

    val letters = 'A'..'H'
    val amountOfLetters = letters.last - letters.first + 1

    var length: Int
        get() = options.length
        set(value) {
            options.length = value
            updateFile()
        }

    var canRepeat: Boolean
        get() = options.canRepeat
        set(value) {
            options.canRepeat = value
            updateFile()
        }

    var tries: Int
        get() = options.numberOfTries
        set(value) {
            options.numberOfTries = value
            updateFile()
        }

    init {
        val gson = Gson()
        try {
            options = gson.fromJson(File(SETTINGS_FILE).readText(), Options::class.java)
        } catch (e: FileNotFoundException) {
            options = Options()
            updateFile()
        }
    }

    fun generateSecret(): String {
        if (!options.canRepeat) {
            return letters.shuffled().take(options.length).joinToString("")
        }

        val res = StringBuilder()
        repeat(options.length) {
            res.append(letters.first + Random.nextInt(0, amountOfLetters))
        }
        return res.toString()
    }

    fun match(secret: String, guess: String): BullsAndCows {
        var cows = 0
        var bulls = 0
        val letterCounts = Array(amountOfLetters) { 0 }
        for (i in 1..secret.length) {
            if (guess[i - 1] == secret[i - 1]) {
                bulls++
            } else {
                letterCounts[secret[i - 1] - 'A']++
            }
        }
        for (i in 1..secret.length) {
            if (guess[i - 1] == secret[i - 1]) {
                continue
            }
            val index = guess[i - 1] - 'A'
            if (letterCounts[index] > 0) {
                letterCounts[index]--
                cows++
            }
        }
        return BullsAndCows(bulls, cows)
    }

    private fun updateFile() {
        File(SETTINGS_FILE).writeText(Gson().toJson(options).toString())
    }

    data class BullsAndCows(val bulls: Int, val cows: Int)

    private data class Options(
        var length: Int = 4,
        var canRepeat: Boolean = false,
        var numberOfTries: Int = -1
    )
}
