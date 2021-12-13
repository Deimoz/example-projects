package org.csc.kotlin2021.mastermind.generation

import org.csc.kotlin2021.mastermind.GameSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class GeneratorTest {

    @ParameterizedTest
    @MethodSource("sequencesBase")
    fun massiveBaseGeneratorTest(canRepeat: Boolean, length: Int) {
        settings.canRepeat = canRepeat
        settings.length = length
        val generated = settings.generateSecret()
        Assertions.assertTrue(
            validateGenerator(generated, false),
            "$generated is not valid secret sequence"
        )
    }

    @ParameterizedTest
    @MethodSource("sequencesSmart")
    fun massiveSmartGeneratorTest(canRepeat: Boolean, length: Int) {
        settings.canRepeat = canRepeat
        settings.length = length
        val generated = settings.generateSecret()
        Assertions.assertTrue(
            validateGenerator(generated, true),
            "$generated is not valid secret sequence"
        )
    }

    /**
     * Game always checks that length in settings is more than 0
     * If length somehow became <= 0 then result would be an empty string
     */
    @Test
    fun zeroLength() {
        settings.length = 0
        Assertions.assertEquals(settings.generateSecret(), "")
    }

    /**
     * If length of secret more than possible then it will be changed
     * to maximum possible length (problem of non repeating characters)
     */
    @Test
    fun tooBigLength() {
        settings.length = settings.amountOfLetters * 10
        settings.canRepeat = false
        Assertions.assertEquals(settings.amountOfLetters, settings.generateSecret().length)
    }

    private fun validateGenerator(generated: String, canRepeat: Boolean): Boolean {
        val used = settings.letters.map { it to false }.toMap().toMutableMap()
        for (char in generated) {
            if (!used.containsKey(char) || (!canRepeat && used[char] == true)) {
                return false
            }
            used[char] = true
        }
        return true
    }

    companion object {
        private val settings = GameSettings()

        private fun generateList(canRepeat: Boolean): MutableList<Arguments> {
            val list: MutableList<Arguments> = mutableListOf()
            for (i in 2..settings.amountOfLetters) {
                for (j in 1..10) {
                    list.add(Arguments.of(canRepeat, i))
                }
            }
            return list
        }

        @JvmStatic
        fun sequencesBase() = generateList(false)

        @JvmStatic
        fun sequencesSmart() = generateList(true)
    }
}
