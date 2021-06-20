package org.xpathqs.prop.scan

import java.io.File

class YmlMatcherRule(root: String) : RuleMatcher(root) {
    override fun isMatched(cls: Class<*>, file: File): Boolean {
        val cls = getClassNameFromRoot(cls) + ".yml"
        val file = getPathFromRoot(file)

        return cls == file
    }
}