package org.xpathqs.prop

import org.xpathqs.prop.scan.ClassResourceMatcher
import org.xpathqs.prop.scan.ResourceScanner

import org.xpathqs.prop.scan.ClassScanner
import org.xpathqs.prop.util.getObject
import java.io.File

//import org.reflections8.scanners.ResourcesScanner


class PropScanner(
    private val rootPackage: String,
    private val resourceRoot: String
) {

    fun scan() {
        val matcher = ClassResourceMatcher(
            resourceRoot.substringAfterLast(File.separator),
            ClassScanner(rootPackage).getAll(),
            ResourceScanner(resourceRoot).getAll()
        )

        val res = matcher.match()

        res.forEach {
            PropParser(
                it.first.getObject(),
                it.second.inputStream()
            ).parse()
        }
    }
}