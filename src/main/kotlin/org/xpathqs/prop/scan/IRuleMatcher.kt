package org.xpathqs.prop.scan

import java.io.File

abstract class RuleMatcher(
    private val root: String
) {
    abstract fun isMatched(cls: Class<*>, file: File): Boolean

    protected fun getClassNameFromRoot(cls: Class<*>): String {
        return cls.name.substringAfter(".$root.")
    }

    protected fun getPathFromRoot(file: File): String {
        val s = File.separator
        return file.absolutePath.substringAfter(s + root + s)
            .replace(s, ".")
    }
}