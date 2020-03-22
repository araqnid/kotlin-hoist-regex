package testInput

class Example {
    val pattern = Regex("""propertyPattern""")

    fun someMethod(input: String): Boolean {
        return input.matches(Regex("""variablePattern"""))
    }

    fun someMethodUsingSingleOption(input: String): Boolean {
        return input.matches(Regex("""variablePatternWithOption""", RegexOption.IGNORE_CASE))
    }

    fun someMethodUsingMultipleOptions(input: String): Boolean {
        return input.matches(
            Regex(
                """variablePatternWithMultipleOptions""",
                setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)
            )
        )
    }

    fun someMethodUsingLotsaOptions(input: String): Boolean {
        return input.matches(
            Regex(
                """variablePatternWithMultipleOptions""",
                setOf(
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE,
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE,
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE,
                    RegexOption.IGNORE_CASE,
                    RegexOption.MULTILINE
                )
            )
        )
    }

    fun someMethodUsingSamePatternTwice(input: String): Boolean {
        val p1 = Regex("variablePattern")
        val p2 = Regex("variablePattern")
        return input.matches(p1) || input.matches(p2)
    }

    companion object {
        // don't intercept this
        val staticPattern = Regex("""staticPattern""")
    }
}

object Example2 {
    // don't intercept this
    val objectPattern = Regex("""objectPattern""")

}

// don't intercept this
val toplevelPattern = Regex("""topLevelPattern""")
