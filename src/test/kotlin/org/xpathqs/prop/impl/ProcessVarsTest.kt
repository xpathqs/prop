package org.xpathqs.prop.impl

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test

internal class ProcessVarsTest {

    @Test
    fun selfProcessWithoutVars() {
        assertThat(
            ProcessVars(
                NoValueProcessor()
            ).process("hi")
        ).isEqualTo(
            "hi"
        )
    }

    @Test
    fun selfProcessWithDate() {

        assertThat(
            ProcessDate(
                NoValueProcessor()
            ).process("\${d+20.m.y}")
        ).isEqualTo(
            "20.1.2022"
        )
    }

    @Test
    fun selfProcessWithVar() {
        System.setProperty("prop", "some_value")
        assertThat(
            ProcessVars(
                NoValueProcessor()
            ).process("\${prop}")
        ).isEqualTo(
            "some_value"
        )
    }

    @Test
    fun selfProcessWithUninitVar() {
        System.setProperty("prop", "some_value")
        assertThat(
            ProcessVars(
                NoValueProcessor()
            ).process("\${prop2}")
        ).isEqualTo(
            "\${prop2}"
        )
    }
}