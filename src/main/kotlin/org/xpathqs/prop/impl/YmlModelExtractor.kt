package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IModelExtractor
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream

class YmlModelExtractor(
    private val stream: InputStream
) : IModelExtractor {
    override fun getModel(): Map<String, Any> {
        return Yaml().load(stream)
    }
    companion object {
        fun fromFile(filename: String): YmlModelExtractor {
            return YmlModelExtractor(
                File(filename).inputStream()
            )
        }
    }
}