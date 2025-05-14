package org.xpathqs.prop

import org.xpathqs.log.Log
import org.xpathqs.prop.base.IValueProcessor
import org.xpathqs.prop.impl.NoValueProcessor
import org.xpathqs.prop.impl.YmlModelExtractor
import org.xpathqs.prop.scan.ClassResourceMatcher
import org.xpathqs.prop.scan.ClassScanner
import org.xpathqs.prop.scan.ResourceScanner
import org.xpathqs.prop.util.getObject

open class PropScanner(
    private val rootPackage: String,
    private val resourceRoot: String,
    private val valueProcessor: IValueProcessor = NoValueProcessor(),
) {
    fun scan() {
        Log.action("Scaning $rootPackage") {
            val matcher = ClassResourceMatcher(
                resourceRoot.substringAfterLast("/"),
                ClassScanner(rootPackage).getAll(),
                ResourceScanner(resourceRoot).getAll()
            )

            val res = matcher.match()

            res.forEach {
                Log.action("Parsing ${it.first.name}") {
                    PropParser(
                        obj = it.first.getObject(),
                        modelExtractor = YmlModelExtractor(it.second.inputStream()),
                        valueProcessor = valueProcessor
                    ).parse()
                }
            }
        }
    }
}