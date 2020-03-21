package testInput

class Example {
    fun someMethod(input: String): Boolean {
        return input.matches(Regex("""\S+"""))
    }
}
