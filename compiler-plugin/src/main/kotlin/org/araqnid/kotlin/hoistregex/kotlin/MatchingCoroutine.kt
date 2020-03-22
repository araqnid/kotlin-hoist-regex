package org.araqnid.kotlin.hoistregex.kotlin

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.resume
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

@RestrictsSuspension
interface MatcherScope<out I : Any> {
    suspend fun take(): I
}

sealed class SequenceMatchResult<out O : Any> {
    object Mismatch : SequenceMatchResult<Nothing>() {
        override fun toString(): String = "Mismatch"
    }

    data class Matched<out O : Any>(val value: O) : SequenceMatchResult<O>()
}

typealias SequenceMatcher<I, O> = (I) -> SequenceMatchResult<O>?

fun <I : Any, O : Any> matchSequence(body: suspend MatcherScope<I>.() -> O?): SequenceMatcher<I, O> {
    val matcher = object : MatcherScope<I>, Continuation<O?>, SequenceMatcher<I, O> {
        init {
            body.startCoroutine(this, this)
        }

        var isFinished = false
        var valueProduced: O? = null
        var failureException: Throwable? = null
        var continuation: Continuation<I>? = null

        override suspend fun take(): I {
            return suspendCoroutine { cont ->
                continuation = cont
            }
        }

        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<O?>) {
            isFinished = true
            valueProduced = result.getOrNull()
            failureException = result.exceptionOrNull()
        }

        override fun invoke(value: I): SequenceMatchResult<O>? {
            check(!isFinished) { "matcher has already terminated" }
            check(continuation != null) { "matcher should be waiting for input" }
            continuation!!.resume(value)
            return when {
                !isFinished -> null
                valueProduced != null -> SequenceMatchResult.Matched(valueProduced!!)
                failureException != null -> throw failureException!!
                else -> SequenceMatchResult.Mismatch
            }
        }
    }

    check(!matcher.isFinished) { "matcher terminated without taking any input" }

    return matcher
}

private fun <I : Any, O : Any> SequenceMatcher<I, O>.matchIterator(inputs: Iterator<I>): SequenceMatchResult<O> {
    while (inputs.hasNext()) {
        val result = invoke(inputs.next())
        if (result != null) return result
    }
    return SequenceMatchResult.Mismatch
}

fun <I : Any, O : Any> SequenceMatcher<I, O>.match(inputs: Sequence<I>) =
    matchIterator(inputs.iterator())

fun <I : Any, O : Any> SequenceMatcher<I, O>.match(inputs: Iterable<I>) =
    matchIterator(inputs.iterator())
