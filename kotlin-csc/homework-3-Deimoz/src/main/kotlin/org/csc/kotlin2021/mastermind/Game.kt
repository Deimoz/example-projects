package org.csc.kotlin2021.mastermind

import org.csc.kotlin2021.mastermind.recordtable.TableHolder

fun playMastermind(
    settings: GameSettings,
    records: TableHolder,
    player: Player = RealPlayer(),
    secret: String = settings.generateSecret()
) {
    val maxTries = settings.tries
    var tries = 0
    var complete = false
    while (!complete && (maxTries == -1 || tries < maxTries)) {
        val guess = player.guess(secret.length, settings.letters)
        if (guess == "exit") {
            println("Going back to menu")
            return
        }
        val result = settings.match(secret, guess)
        complete = result.bulls == secret.length
        player.receiveEvaluation(complete, result.bulls, result.cows)
        tries++
    }
    if (complete) {
        records.addResult(settings.length, settings.canRepeat, tries)
    } else {
        println("Failed :(")
    }
}
