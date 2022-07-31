package org.xpathqs.prop.impl

import org.xpathqs.prop.base.IModelExtractor
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.Path

class YmlModelExtractor(
    private val stream: InputStream
) : IModelExtractor {
    override fun getModel(): Map<String, Any> {
        return Yaml().load(stream)
    }
    companion object {
        fun fromFile(filename: String): YmlModelExtractor {
            val stream = if(Files.exists(Path.of(filename))) File(filename).inputStream()
            else this::class.java.classLoader.getResourceAsStream(filename)

            return YmlModelExtractor(stream)
        }
    }
}