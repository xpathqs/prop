package org.xpathqs.prop

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.prop.impl.YmlModelExtractor
import org.xpathqs.prop.models.ObjWithInner

import org.xpathqs.prop.models.SimpleObj

internal class PropParserTest {

    @Test
    fun parseSimple() {
        PropParser(SimpleObj, YmlModelExtractor.fromFile("simple.yml"))
            .parse()

        assertAll {
            assertThat(SimpleObj.s1)
                .isEqualTo("value 1")

            assertThat(SimpleObj.s2)
                .isEqualTo("value 2")
        }
    }

    @Test
    fun parseInner() {
        PropParser(ObjWithInner, YmlModelExtractor.fromFile("inner.yml"))
            .parse()

        assertAll {
            assertThat(ObjWithInner.s1)
                .isEqualTo("value 1")

            assertThat(ObjWithInner.s2)
                .isEqualTo("value 2")

            assertThat(ObjWithInner.Inner.s3)
                .isEqualTo("value 3")

            assertThat(ObjWithInner.Inner.s4)
                .isEqualTo("value 4")
        }
    }
}