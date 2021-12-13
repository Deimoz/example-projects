package org.csc.kotlin2021.mastermind.matching

import org.csc.kotlin2021.mastermind.GameSettings
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

internal class SequenceMatchingTest {
    private val settings: GameSettings = GameSettings()

    @ParameterizedTest
    @MethodSource("sequences")
    fun testSequenceMatching(initial: String, actual: String, expectedFullMatch: Int, expectedPartMatch: Int) {
        val res = settings.match(secret = actual, guess = initial)
        val actualFullMatch = res.bulls
        val actualPartMatch = res.cows
        Assertions.assertEquals(
            expectedFullMatch, actualFullMatch, "Full matches don't equal! " +
                    "Actual full match count = $actualFullMatch, expected full match count = $expectedFullMatch"
        )
        Assertions.assertEquals(
            expectedPartMatch, actualPartMatch, "Part matches don't equal! " +
                    "Part full part count = $actualPartMatch, expected part match count = $expectedPartMatch"
        )
    }

    companion object {
        @JvmStatic
        fun sequences() = listOf(
            Arguments.of("ACEB", "BCDF", 1, 1),
            Arguments.of("AAAB", "AABB", 3, 0),
            Arguments.of("AADFE", "AAFDE", 3, 2),
            Arguments.of("ABCD", "DCBA", 0, 4),
            Arguments.of("ABDFED", "ABDFED", 6, 0),
            Arguments.of("AAAB", "AABA", 2, 2),
        )
    }
}
