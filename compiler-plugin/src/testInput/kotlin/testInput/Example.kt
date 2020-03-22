package testInput

class Example {
    val pattern = Regex("""propertyPattern""")

    fun someMethod(input: String): Boolean {
        return input.matches(Regex("""variablePattern"""))
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
