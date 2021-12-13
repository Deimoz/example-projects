package org.csc.kotlin2021.mastermind.menus

import org.csc.kotlin2021.mastermind.GameSettings
import org.csc.kotlin2021.mastermind.recordtable.TableHolder
import java.lang.Exception
import java.util.*

class RecordsMenu(
    override val settings: GameSettings,
    override val scanner: Scanner,
    private val records: TableHolder
) : Menu {
    override fun startMenu() {
        printUi(
            """
                Write "<length> <repeat enabled>" in format of <number> <yes/no> without <>
                if you want to find scores for other game mode
                "exit" to go back to main menu
            """.trimIndent()
        )
        records.standardTable()
        while (true) {
            val arg1 = scanner.next()
            if (arg1 == "exit") {
                println("Going back to menu")
                break
            }
            val arg2 = scanner.next()
            var num = -1
            try {
                num = arg1.toInt()
            } catch (ignored: Exception) {
            }
            if (num != -1 && (arg2 == "yes" || arg2 == "no")) {
                records.findTable(num, arg2 == "yes")
                continue
            }
            wrongInput()
        }
    }
}
