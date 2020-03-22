package org.araqnid.kotlin.hoistregex.kotlin

import org.araqnid.kotlin.hoistregex.kotlin.TestInputsAsASM.originalSomeMethod
import org.araqnid.kotlin.hoistregex.kotlin.TestInputsAsASM.originalSomeMethodUsingLotsaOptions
import org.araqnid.kotlin.hoistregex.kotlin.TestInputsAsASM.originalSomeMethodUsingMultipleOptions
import org.araqnid.kotlin.hoistregex.kotlin.TestInputsAsASM.originalSomeMethodUsingSingleOption
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.util.Textifier
import org.jetbrains.org.objectweb.asm.util.TraceMethodVisitor
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class HoistingMethodAdapterTest {
    @Test
    fun `extracts regex creations and assigns symbols`() {
        val (allText, patternAllocator) = produceAndDisassemble(::originalSomeMethod)
        assertFalse(
            allText.contains(
                """
                |    NEW kotlin/text/Regex
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertTrue(
            allText.contains(
                """
                |    GETSTATIC testInputs.Example.${'$'}pattern${'$'}0 : Lkotlin/text/Regex;
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertEquals(
            patternAllocator.all,
            listOf(PatternAllocator.Allocated("testInputs.Example", "\$pattern\$0", PatternAllocator.Pattern("variablePattern", emptySet())))
        )
    }

    @Test
    fun `handles regex created with single option`() {
        val (allText, patternAllocator) = produceAndDisassemble(::originalSomeMethodUsingSingleOption)
        assertFalse(
            allText.contains(
                """
                |    NEW kotlin/text/Regex
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertTrue(
            allText.contains(
                """
                |    GETSTATIC testInputs.Example.${'$'}pattern${'$'}0 : Lkotlin/text/Regex;
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertEquals(
            patternAllocator.all,
            listOf(PatternAllocator.Allocated("testInputs.Example", "\$pattern\$0", PatternAllocator.Pattern("variablePatternWithOption", setOf(RegexOption.IGNORE_CASE))))
        )
    }

    @Test
    fun `handles regex created with multiple options`() {
        val (allText, patternAllocator) = produceAndDisassemble(::originalSomeMethodUsingMultipleOptions)
        assertFalse(
            allText.contains(
                """
                |    NEW kotlin/text/Regex
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertTrue(
            allText.contains(
                """
                |    GETSTATIC testInputs.Example.${'$'}pattern${'$'}0 : Lkotlin/text/Regex;
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertEquals(
            patternAllocator.all,
            listOf(PatternAllocator.Allocated("testInputs.Example", "\$pattern\$0", PatternAllocator.Pattern("variablePatternWithMultipleOptions", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))))
        )
    }

    @Test
    fun `handles regex created with lots of options`() {
        val (allText, patternAllocator) = produceAndDisassemble(::originalSomeMethodUsingLotsaOptions)
        assertFalse(
            allText.contains(
                """
                |    NEW kotlin/text/Regex
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertTrue(
            allText.contains(
                """
                |    GETSTATIC testInputs.Example.${'$'}pattern${'$'}0 : Lkotlin/text/Regex;
                """.trimMargin()
            ), "Method produced:\n\n$allText"
        )
        assertEquals(
            patternAllocator.all,
            listOf(PatternAllocator.Allocated("testInputs.Example", "\$pattern\$0", PatternAllocator.Pattern("variablePatternWithMultipleOptions", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE))))
        )
    }

    private fun produceAndDisassemble(source: (MethodVisitor) -> Unit): Pair<String, PatternAllocator> {
        val textifier = Textifier()
        val patternAllocator = PatternAllocator()
        val hoistingAdapter = HoistingMethodAdapter(
            "testInputs.Example",
            patternAllocator,
            TraceMethodVisitor(textifier)
        )
        source(hoistingAdapter)
        return Pair(textifier.getText().joinToString(""), patternAllocator)
    }
}
