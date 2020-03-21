package testInput

class Example {
    val pattern = Regex("""\S+""")

    fun someMethod(input: String): Boolean {
        return input.matches(Regex("""\S+"""))
    }

    companion object {
        // don't intercept this
        val staticPattern = Regex("""\S+""")
    }
}

object Example2 {
    // don't intercept this
    val objectPattern = Regex("""\S+""")

}

// don't intercept this
val toplevelPattern = Regex("""\S+""")
