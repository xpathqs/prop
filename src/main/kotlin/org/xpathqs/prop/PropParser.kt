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
    private val valueProcessor: IValueProcessor = NoValueProcessor(),
    private val rs: ReflectionScanner = ReflectionScanner(obj)
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
        rs.fields.forEach {
            processField(it)
        }
    }

    fun <T>tryOrNull(block: () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            null
        }
    }

    private fun createObj(clsName: String): Any {
        val newCls: Class<*> = tryOrNull {
            this.javaClass.classLoader.loadClass(
            obj.javaClass.packageName + "." + clsName
            )
        } ?: tryOrNull {
            this.javaClass.classLoader.loadClass(
                obj.javaClass.name + "$" + clsName
            )
        } ?: throw Exception("Can't load class: $clsName")

        val newObj = newCls.declaredConstructors.firstOrNull {
            it.parameterCount == 0
        }?.newInstance() ?: newCls.declaredConstructors.first().newInstance(obj)
        return newObj
    }

    @Suppress("UNCHECKED_CAST")
    private fun processField(f: Field) {
        val name = f.name.substringBefore("$")
        if (values.containsKey(name)) {
            val v = values[name]!!
            if(f.isPrimitive || v.javaClass.checkIsPrimitive) {
                f.setFieldValue(
                    valueProcessor.process(v!!)
                )
            } else {
                var newObj = f.get(obj)
                if(newObj != null) {
                    v as Map<String, Any>
                    val clsName =  v.keys.first()
                    if(newObj.javaClass.simpleName != clsName) {
                        newObj = createObj(clsName)
                        f.set(obj, newObj)
                    }

                    PropParser(
                        newObj,
                        ModelExtractor(v),
                        valueProcessor
                    ).parse()
                }
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
        rs.innerObjects.forEach {
            try {
                PropParser(it, ModelExtractor(values), valueProcessor).parse()
            } catch (e: IllegalArgumentException) { }
        }
    }

    private fun Field.toProp(obj: Any): KMutableProperty<*>? {
        return obj::class.members.find {
            it.name == this.name.substringBefore("$")
        } as? KMutableProperty<*>
    }
}