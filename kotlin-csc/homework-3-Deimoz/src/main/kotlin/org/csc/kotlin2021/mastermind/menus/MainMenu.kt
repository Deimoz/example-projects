package org.csc.kotlin2021.mastermind.menus

import org.csc.kotlin2021.mastermind.GameSettings
import org.csc.kotlin2021.mastermind.playMastermind
import org.csc.kotlin2021.mastermind.recordtable.TableHolder
import java.util.*

class MainMenu(
    override val settings: GameSettings = GameSettings(),
    override val scanner: Scanner = Scanner(System.`in`),
    private val records: TableHolder = TableHolder()
) : Menu {
    private val settingsMenu = SettingsMenu(
        settings,
        scanner
    )
    private val recordsMenu = RecordsMenu(
        settings,
        scanner,
        records
    )

    override fun startMenu() {
        while (true) {
            printUi(
                """
                    Main menu
                    Write:
                    "play" to start the game
                    "settings" to open settings
                    "records" to see personal records
                    "exit" to close the game
                """.trimIndent()
            )
            when (scanner.next()) {
                "play" -> {
                    playMastermind(settings, records)
                }
                "settings" -> {
                    settingsMenu.startMenu()
                }
                "records" -> {
                    recordsMenu.startMenu()
                }
                "exit" -> {
                    println("See you next time")
                    break
                }
                else -> {
                    wrongInput()
                }
            }
        }
    }
}
