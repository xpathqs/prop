package org.xpathqs.prop

import org.apache.commons.lang3.ClassUtils
import org.xpathqs.prop.util.ReflectionScanner
import org.xpathqs.prop.util.isObject
import org.xpathqs.prop.util.isPrimitive
import org.yaml.snakeyaml.Yaml
import java.io.InputStream
import java.lang.Exception

class PropParser(
    private val obj: Any,
    private val model: Map<String, Any>,
    private val rs: ReflectionScanner = ReflectionScanner(obj)
) {
    constructor(obj: Any, stream: InputStream)
            : this(obj, model = Yaml().load(stream))

    constructor(obj: Any, resourcePath: String)
            : this(
        obj,
        PropParser::class.java.classLoader.getResource(resourcePath)?.openStream()
            ?: throw IllegalArgumentException("'$resourcePath' Resource can't be found")
    )

    @Suppress("UNCHECKED_CAST")
    fun parse() {
        val values = model[obj::class.simpleName] as? Map<String, Any> ?:
            throw IllegalArgumentException("Can't parse provided model")

        val fields = rs.fields
        fields.forEach {
            if (values.containsKey(it.name)) {
                if(it.isPrimitive) {
                    if(obj.isObject()) {
                        it.set(null, values[it.name])
                    } else {
                        it.set(obj, values[it.name])
                    }
                } else {
                    PropParser(it.get(obj), values[it.name] as Map<String, Any>)
                        .parse()
                }
            }
        }

        val objects = rs.innerObjects
        objects.forEach {
            try {
                PropParser(it, values)
                    .parse()
            } catch (e: IllegalArgumentException) {
            }
        }
    }
}