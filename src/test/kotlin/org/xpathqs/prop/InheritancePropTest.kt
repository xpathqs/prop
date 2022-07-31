package org.xpathqs.prop

import assertk.assertAll
import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.jupiter.api.Test
import org.xpathqs.prop.data.Base2
import org.xpathqs.prop.data.WithInheritance
import org.xpathqs.prop.impl.YmlModelExtractor
import org.xpathqs.prop.models.ObjWithInner

internal class InheritancePropTest {

    @Test
    fun withBase() {
        PropParser(
            WithInheritance,
            YmlModelExtractor.fromFile("WithInheritance.yml")
        ).parse()

        assertThat(WithInheritance.m.p1)
            .isEqualTo("asd")
    }

    @Test
    fun withBase2() {
        PropParser(
            WithInheritance,
            YmlModelExtractor.fromFile("WithInheritance2.yml")
        ).parse()


        assertAll {
            assertThat(WithInheritance.m.p1)
                .isEqualTo("asd")
            assertThat((WithInheritance.m as Base2).p2)
                .isEqualTo("asd2")
        }
    }
}