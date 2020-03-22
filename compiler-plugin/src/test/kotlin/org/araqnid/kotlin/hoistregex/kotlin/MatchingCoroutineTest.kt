package org.araqnid.kotlin.hoistregex.kotlin

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MatchingCoroutineTest {
    private fun createMatcher(): SequenceMatcher<String, String> {
        return matchSequence {
            if (take() != "red") return@matchSequence null
            when (take()) {
                "green" -> "Kermit"
                "blue" -> "Elza"
                else -> null
            }
        }
    }

    @Test
    fun `coroutine accepts elements in order 1`() {
        val matcher = createMatcher()
        assertNull(matcher("red"))
        assertEquals(SequenceMatchResult.Matched("Kermit"), matcher("green"))
    }

    @Test
    fun `coroutine accepts elements in order 2`() {
        val matcher = createMatcher()
        assertNull(matcher("red"))
        assertEquals(SequenceMatchResult.Matched("Elza"), matcher("blue"))
    }

    @Test
    fun `coroutine rejects elements at first branch`() {
        val matcher = createMatcher()
        assertEquals(SequenceMatchResult.Mismatch, matcher("blue"))
    }

    @Test
    fun `coroutine rejects elements at second branch`() {
        val matcher = createMatcher()
        assertNull(matcher("red"))
        assertEquals(SequenceMatchResult.Mismatch, matcher("red"))
    }

    @Test
    fun `coroutine matches sequences`() {
        assertEquals(createMatcher().match(sequenceOf("red", "green")), SequenceMatchResult.Matched("Kermit"))
        assertEquals(createMatcher().match(sequenceOf("red")), SequenceMatchResult.Mismatch)
        assertEquals(createMatcher().match(sequenceOf("red", "orange")), SequenceMatchResult.Mismatch)
    }

    @Test
    fun `coroutine matches iterables`() {
        assertEquals(createMatcher().match(listOf("red", "green")), SequenceMatchResult.Matched("Kermit"))
        assertEquals(createMatcher().match(listOf("red")), SequenceMatchResult.Mismatch)
        assertEquals(createMatcher().match(listOf("red", "orange")), SequenceMatchResult.Mismatch)
    }
}
