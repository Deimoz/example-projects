package org.csc.kotlin2021.mastermind

import java.util.*

interface Player {
    fun guess(length: Int, possibleLetters: CharRange): String
    fun receiveEvaluation(complete: Boolean, positions: Int, letters: Int)
    fun incorrectInput(guess: String, length: Int) {}
}

class RealPlayer : Player {
    private val scanner = Scanner(System.`in`)

    override fun guess(length: Int, possibleLetters: CharRange): String {
        print("Your guess: ")
        while (true) {
            val input = scanner.next()
            if (validateGuess(input, length, possibleLetters)) {
                return input
            }
            incorrectInput(input, length)
        }
    }

    override fun receiveEvaluation(complete: Boolean, positions: Int, letters: Int) {
        if (complete) {
            println("You are correct!")
        } else {
            println("Positions: $positions; letters: $letters.")
        }
    }

    override fun incorrectInput(guess: String, length: Int) {
        println("Incorrect input: $guess. It should consist of $length letters (A, B, C, D, E, F, G, H).")
    }

    private fun validateGuess(guess: String, length: Int, possibleLetters: CharRange): Boolean {
        if (guess == "exit") {
            return true
        }
        if (guess.length != length) {
            return false
        }
        for (char in guess) {
            if (char !in possibleLetters) {
                return false
            }
        }
        return true
    }
}
