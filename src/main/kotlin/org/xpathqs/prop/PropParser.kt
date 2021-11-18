package org.xpathqs.prop

import org.xpathqs.prop.base.IModelExtractor
import org.xpathqs.prop.base.IValueProcessor
import org.xpathqs.prop.impl.ModelExtractor
import org.xpathqs.prop.impl.NoValueProcessor
import org.xpathqs.prop.util.ReflectionScanner
import org.xpathqs.prop.util.checkIsPrimitive
import org.xpathqs.prop.util.isObject
import org.xpathqs.prop.util.isPrimitive
import java.lang.reflect.Field
import kotlin.reflect.KMutableProperty

class PropParser(
    private val obj: Any,
    private val modelExtractor: IModelExtractor,
    private val rs: ReflectionScanner = ReflectionScanner(obj),
    private val valueProcessor: IValueProcessor = NoValueProcessor()
) {
    private val model by lazy { modelExtractor.getModel() }
    private val values by lazy {
        model[obj::class.simpleName] as? Map<String, Any> ?:
            throw IllegalArgumentException("Can't parse provided model")
    }

    fun parse() {
        parseFields()
        parseObjects()
    }

    private fun parseFields() {
        rs.fields.forEach { processField(it) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun processField(f: Field) {
        val name = f.name.substringBefore("$")
        if (values.containsKey(name)) {
            if(f.isPrimitive || values[name]?.javaClass?.checkIsPrimitive == true) {
                f.setFieldValue(
                    valueProcessor.process(values[name]!!)
                )
            } else {
                PropParser(f.get(obj), ModelExtractor(values[name] as Map<String, Any>))
                    .parse()
            }
        }
    }

    private fun Field.setFieldValue(value: Any) {
        val p = this.toProp(obj)
        if(p != null) {
            p.setter.call(obj, value)
        } else {
            if(obj.isObject()) {
                this.set(null, value)
            } else {
                this.set(obj, value)
            }
        }
    }

    private fun parseObjects() {
        val objects = rs.innerObjects
        objects.forEach {
            try {
                PropParser(it, ModelExtractor(values)).parse()
            } catch (e: IllegalArgumentException) { }
        }
    }

    private fun Field.toProp(obj: Any): KMutableProperty<*>? {
        return obj::class.members.find {
            it.name == this.name.substringBefore("$")
        } as? KMutableProperty<*>
    }
}