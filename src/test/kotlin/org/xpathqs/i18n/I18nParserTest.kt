package org.xpathqs.i18n

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.i18n.models.ObjWithInner

import org.xpathqs.i18n.models.SimpleObj

internal class I18nParserTest {

    @Test
    fun parseSimple() {
        I18nParser(SimpleObj, "simple.yml")
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
        I18nParser(ObjWithInner, "inner.yml")
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