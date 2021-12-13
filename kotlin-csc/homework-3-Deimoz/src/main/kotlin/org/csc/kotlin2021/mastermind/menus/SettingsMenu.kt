package org.csc.kotlin2021.mastermind.menus

import org.csc.kotlin2021.mastermind.GameSettings
import java.lang.NumberFormatException
import java.util.*

class SettingsMenu(
    override val settings: GameSettings,
    override val scanner: Scanner
) : Menu {
    override fun startMenu() {
        while (true) {
            printUi(
                """
                    Settings menu
                    Write:
                    "1" to change length of word in game
                    "2" to enable/disable repeating characters
                    "3" to change maximum of available guesses in game
                    "exit" to close the settings
                """.trimIndent()
            )
            currentSettings()
            when (scanner.next()) {
                "1" -> {
                    settingsBrunch(
                        """
                            Length option
                            Write:
                            "<number>" to change length of word in game (from 1 to 10)
                            "exit" to close length option
                        """,
                        { validateNumber(it) },
                        { changeSettingsLen(it.toInt()) }
                    )
                }
                "2" -> {
                    settingsBrunch(
                        """
                            Repeating characters option
                            Write:
                            "<yes/no>" to enable/disable repeating characters
                            "exit" to close repeating characters option
                        """,
                        { validateYesOrNo(it) },
                        { changeSettingsRepeat(it.lowercase() == "yes") }
                    )
                }
                "3" -> {
                    settingsBrunch(
                        """
                            Maximum guesses option
                            Write: "<number>" to change amount of maximum possible guesses.
                            Write -1 to disable this function
                            "exit" to close maximum guesses option
                        """,
                        { validateTries(it) },
                        { changeSettingsTries(it.toInt()) }
                    )
                }
                "exit" -> {
                    println("Going back to menu")
                    break
                }
                else -> {
                    wrongInput()
                }
            }
        }
    }

    private fun settingsBrunch(
        message: String,
        validate: (String) -> Boolean,
        changeSettings: (String) -> Unit
    ) {
        printUi(
            message.trimIndent()
        )
        var input: String
        do {
            input = scanner.next()
        } while (!validate(input) && input != "exit")
        if (input != "exit") {
            changeSettings(input)
        }
    }

    private fun validateNumber(input: String): Boolean {
        return try {
            input.toInt() in 1..10
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validateTries(input: String): Boolean {
        return try {
            input.toInt() > 0
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun validateYesOrNo(input: String): Boolean {
        return input.lowercase() == "yes" || input.lowercase() == "no"
    }

    private fun changeSettingsLen(length: Int) {
        var res = length
        if (!settings.canRepeat && length > settings.amountOfLetters) {
            println("Repeat is not available so maximum possible length is ${settings.amountOfLetters}")
            res = settings.amountOfLetters
        }
        settings.length = res
        println("Changes saved")
    }

    private fun changeSettingsRepeat(canRepeat: Boolean) {
        settings.canRepeat = canRepeat
        if (!canRepeat && settings.length > settings.amountOfLetters) {
            settings.length = settings.amountOfLetters
            println("Number of letters was invalid for this mode. Changed to ${settings.length}")
        }
        println("Changes saved")
    }

    private fun changeSettingsTries(tries: Int) {
        settings.tries = tries
        println("Changes saved")
    }

    private fun currentSettings() {
        println(
            """
                Current settings
                Length: ${settings.length}
                Repeatable: ${settings.canRepeat}
                Maximum guesses: ${settings.tries}
            """.trimIndent()
        )
    }
}
