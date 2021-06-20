package org.xpathqs.prop.scan

import java.io.File

class ClassResourceMatcher(
    private val root: String,
    private val classes: Collection<Class<*>>,
    private val files: Collection<File>,
    private val matchers: Collection<RuleMatcher> = listOf(
        YmlMatcherRule(root)
    )
) {
    fun match(): Collection<Pair<Class<*>, File>> {
        val res = ArrayList<Pair<Class<*>, File>>()

        classes.forEach {
            findPair(it)?.let {
                res.add(it)
            }
        }

        return res
    }

    fun findPair(cls: Class<*>): Pair<Class<*>, File>? {
        files.forEach {
            if (isMatched(cls, it)) {
                return cls to it
            }
        }

        return null
    }

    fun isMatched(cls: Class<*>, file: File): Boolean {
        matchers.forEach {
            if(it.isMatched(cls, file)) {
                return true
            }
        }
        return false
    }

}