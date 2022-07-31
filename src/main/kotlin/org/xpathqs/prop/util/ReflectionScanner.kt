package org.xpathqs.prop.util

import org.apache.commons.lang3.ClassUtils
import org.apache.commons.lang3.reflect.FieldUtils
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class ReflectionScanner(
    private val obj: Any
) {
    val fields: Collection<Field>
        by lazy {
            var res = ArrayList<Field>()

            var cls = obj::class.java
            res.addAll(cls.declaredFields.filter {
                it.name != "Companion" && it.name != "INSTANCE"
                //        && !Modifier.isStatic(it.modifiers)
            })

            while (cls.superclass != null
                && cls.packageName == cls.superclass.packageName
            ) {
                cls = cls.superclass
                if(cls.simpleName != "Companion") {
                    res.addAll(cls.declaredFields)
                }
            }

            var r = removeUnnecessary(res)
            r = r.distinct()
            r.forEach {
                it.isAccessible = true
                FieldUtils.removeFinalModifier(it)
                res.add(it)
            }
            r
        }

    val innerObjects: Collection<Any> by lazy {
        val res = ArrayList<Any>()

        obj.javaClass.declaredClasses.forEach {
            try{ res.add(it.getObject()) } catch (e: Exception) { }
        }

        res
    }

    /**
     * Filter Unnecessary fields
     */
    private fun removeUnnecessary(fields: Collection<Field>) = fields
        .filter {
            it.name != "INSTANCE" //remove object-class instances
             //       && !it.name.contains("\$")  //remove fields which was added dynamically
        }
        .distinctBy { it.name }

}

internal fun Class<*>.getObject(): Any {
    val instanceField = this.declaredFields
        .find { it.name == "INSTANCE" } ?: throw IllegalArgumentException(
        "Provided class ${this.name} doesn't used as an object-class"
    )

    return instanceField.get(null) ?: throw IllegalArgumentException(
        "Provided class $name is not inherited from the Block class"
    )
}

internal fun Any.isObject(): Boolean {
    if (this is Class<*>) {
        return this.declaredFields
            .find {
                it.name == "INSTANCE"
            } != null
    }
    return this.javaClass.declaredFields
        .find {
            it.name == "INSTANCE"
        } != null
}

internal val Field.isPrimitive: Boolean
    get() {
        return this.type.simpleName == "String" || ClassUtils.isPrimitiveOrWrapper(this.type)
    }

internal val Class<*>.checkIsPrimitive: Boolean
    get() {
        return this.simpleName == "String" || ClassUtils.isPrimitiveOrWrapper(this)
    }