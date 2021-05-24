package org.xpathqs.i18n

import org.xpathqs.i18n.util.ReflectionScanner
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class I18nParser(
    private val obj: Any,
    private val model: Map<String, Any>,
    private val rs: ReflectionScanner = ReflectionScanner(obj)
) {
    constructor(obj: Any, stream: InputStream)
            : this(obj, model = Yaml().load(stream))

    constructor(obj: Any, resourcePath: String)
            : this(
        obj,
        I18nParser::class.java.classLoader.getResource(resourcePath)?.openStream()
            ?: throw IllegalArgumentException("'$resourcePath' Resource can't be found")
    )

    @Suppress("UNCHECKED_CAST")
    fun parse() {
        val values = model[obj::class.simpleName] as? Map<String, Any> ?:
            throw IllegalArgumentException("Can't parse provided model")

        val fields = rs.fields
        fields.forEach {
            if (values.containsKey(it.name)) {
                it.set(null, values[it.name])
            }
        }

        val objects = rs.innerObjects
        objects.forEach {
            I18nParser(it, values)
                .parse()
        }
    }
}