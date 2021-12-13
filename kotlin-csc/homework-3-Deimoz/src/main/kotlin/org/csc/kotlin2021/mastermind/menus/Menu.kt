package org.csc.kotlin2021.mastermind.menus

import org.csc.kotlin2021.mastermind.GameSettings
import java.util.*

interface Menu {
    val settings: GameSettings
    val scanner: Scanner

    fun startMenu()

    // For separating different menus
    fun printUi(text: String) {
        print("\u001b[H\u001b[2J")
        println("\n------------------------------------\n$text\n")
    }

    fun wrongInput(text: String = "Wrong input") {
        println(text)
    }
}
