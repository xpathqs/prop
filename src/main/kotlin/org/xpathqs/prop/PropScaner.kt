package org.xpathqs.prop

import org.xpathqs.log.Log
import org.xpathqs.prop.base.IValueProcessor
import org.xpathqs.prop.impl.NoValueProcessor
import org.xpathqs.prop.impl.YmlModelExtractor
import org.xpathqs.prop.scan.ClassResourceMatcher
import org.xpathqs.prop.scan.ClassScanner
import org.xpathqs.prop.scan.ResourceScanner
import org.xpathqs.prop.util.getObject
import java.io.File
import java.nio.file.Files

open class PropScanner(
    private val rootPackage: String,
    private val resourceRoot: String,
    private val valueProcessor: IValueProcessor = NoValueProcessor(),
) {
    fun scan() {
        Log.action("Scaning $rootPackage") {
            val matcher = ClassResourceMatcher(
                root = resourceRoot.substringAfterLast("/"),
                classes = ClassScanner(rootPackage).getAll(),
                files = ResourceScanner(resourceRoot).getAll()
            )

            val res = matcher.match()

            res.forEach {
                Log.action("Parsing ${it.first.name}") {
                    PropParser(
                        obj = it.first.getObject(),
                        modelExtractor = YmlModelExtractor(object {}.javaClass.classLoader.getResourceAsStream(resourceRoot + File.separator + it.second.name)),
                        valueProcessor = valueProcessor
                    ).parse()
                }
            }
        }
    }
}