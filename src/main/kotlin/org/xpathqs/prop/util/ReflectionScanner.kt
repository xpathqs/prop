package org.xpathqs.prop.util

import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field

class ReflectionScanner(
    private val obj: Any
) {
    val fields: Collection<Field> by lazy {
        val res = ArrayList<Field>()

        obj.javaClass.declaredFields.forEach {
            it.isAccessible = true
            FieldUtils.removeFinalModifier(it)
            res.add(it)
        }

        removeUnnecessary(res)
    }

    val innerObjects: Collection<Any> by lazy {
        val res = ArrayList<Any>()

        obj.javaClass.declaredClasses.forEach {
            res.add(it.getObject())
        }

        res
    }

    /**
     * Filter Unnecessary fields
     */
    private fun removeUnnecessary(fields: Collection<Field>) = fields
        .filter {
            it.name != "INSTANCE"
                    && it.name != "\$jacocoData"
        }
        .distinctBy { it.name }


    internal fun Class<*>.getObject(): Any {
        val instanceField = this.declaredFields
            .find { it.name == "INSTANCE" } ?: throw IllegalArgumentException(
            "Provided class ${this.name} doesn't used as an object-class"
        )

        return instanceField.get(null) ?: throw IllegalArgumentException(
            "Provided class $name is not inherited from the Block class"
        )
    }
}