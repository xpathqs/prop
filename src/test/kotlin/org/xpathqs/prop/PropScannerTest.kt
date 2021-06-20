package org.xpathqs.prop

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.prop.data.SomeData1
import org.xpathqs.prop.data.data1.SomeData2

internal class PropScannerTest {

    @Test
    fun test() {
        PropScanner("org.xpathqs.prop", "data")
            .scan()

        assertAll {
            assertThat(SomeData1.s)
                .isEqualTo("sss1")

            assertThat(SomeData1.user.name1)
                .isEqualTo("name1")

            assertThat(SomeData1.user.name2)
                .isEqualTo("name2")

            assertThat(SomeData2.s2)
                .isEqualTo("sss2")
        }
    }
}