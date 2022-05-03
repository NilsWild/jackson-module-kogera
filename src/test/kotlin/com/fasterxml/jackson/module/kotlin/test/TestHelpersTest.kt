package com.fasterxml.jackson.module.kotlin.test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestHelpersTest {
    @Test
    fun expectFailure_ExpectedExceptionThrown() {
        expectFailure<AssertionError>("This will not be printed") {
            throw AssertionError("This is expected")
        }
    }

    @Test
    fun expectFailure_AnotherExceptionThrown() {
        val throwable = assertThrows<AssertionError> {
            expectFailure<AssertionError>("This will not be printed") {
                throw Exception("This is not expected")
            }
        }

        assertEquals("Expected class java.lang.AssertionError but got java.lang.Exception: This is not expected", throwable.message)
    }

    @Test
    fun expectFailure_NoExceptionThrown() {
        val fixMessage = "Test will fail with this message"
        val throwable = assertThrows<AssertionError> {
            expectFailure<AssertionError>(fixMessage) {
                // Do nothing
            }
        }

        assertEquals(fixMessage, throwable.message)
    }
}
