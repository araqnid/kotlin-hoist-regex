Regex-hoisting Kotlin compiler plugin
=====================================

The intent is to process source code that looks like this:

```kotlin
fun someFunction(inputs: List<String>) {
    for (str in inputs) {
        if (inputString.matches(Regex("""some pattern"""))) {
            // ... do something
        }
    }
}
```

In languages like Perl or Javascript, when you write a regular expression
inline, the compilation of the expression is "hoisted" up to the top level
(presumably per-file) so that the pattern itself is only compiled once,
and the singleton compiled pattern is used for matches throughout the entire
file.

In Java, this typically produces this pattern:

```java
class SomeClass {
    static final Pattern PATTERN = Pattern.compile("some pattern");

    public void someFunction(List<String> inputs) {
        for (String str : inputs) {
            Matcher matcher = PATTERN.matcher(str);
            if (matcher.matches()) {
                // ... do something
            }
        }
    }
}
```

Now of course this pattern can be replicated in Kotlin as well, by declaring
the Regex in a companion object or at the top level, but that moves the
pattern away from the point at which it was used, decreasing readability.

By constrast, in Perl you can write:

```perl
sub someFunction {
    for my $str (@_) {
        if ($str =~ /some pattern/) {
            # ...do something
        }
    }
}
```

Here you get both benefits: the pattern is right there at the point that it
is used, *and* it only gets compiled once rather than inside the loop.

Both Perl and Javascript recognise regular expressions at the language level:
that's too much for Kotlin, where language changes are kept minimal. However,
it should be possible to simply see `Regex(...)` calls taking only a literal
string (and possible flag arguments) and simply hoist that declaration to
an upper level.
