package org.xpathqs.prop.scan

import java.io.File

abstract class RuleMatcher(
    private val root: String
) {
    private val systemRoot: String
        get() = root.replace("/", File.separator)

    abstract fun isMatched(cls: Class<*>, file: File): Boolean

    protected fun getClassNameFromRoot(cls: Class<*>): String {
        return cls.name.substringAfter(".$systemRoot.")
    }

    protected fun getPathFromRoot(file: File): String {
        val s = File.separator

        return file.absolutePath.substringAfter(s + systemRoot + s)
            .replace(s, ".")
    }
}