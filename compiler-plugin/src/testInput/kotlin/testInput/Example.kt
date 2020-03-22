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
        return input.matches(Regex("""variablePatternWithMultipleOptions""", setOf(RegexOption.IGNORE_CASE, RegexOption.MULTILINE)))
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
