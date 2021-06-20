package org.xpathqs.prop.scan

import org.reflections8.Reflections
import org.xpathqs.prop.Model

class ClassScanner(
    val path: String) {

    fun getAll(): Collection<Class<*>> {
        val reflections = Reflections(path)
        return reflections.getTypesAnnotatedWith(Model::class.java)
    }
}