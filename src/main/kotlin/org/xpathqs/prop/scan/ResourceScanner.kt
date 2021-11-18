package org.xpathqs.prop.scan

import java.io.*
import java.net.URL

class ResourceScanner(
    private val path: String,
    private val loader: ClassLoader = Thread.currentThread().contextClassLoader
) {
    fun getAll(): Collection<File> {
        val url: URL = loader.getResource(path)
        val path: String = url.path
        val res = ArrayList<File>()

        File(path).listFiles().forEach {
            if(it.isDirectory) {
                res.addAll(
                    ResourceScanner(
                        this.path + it.path.substringAfter(path),
                        loader
                    ).getAll()
                )
            } else {
                res.add(it)
            }
        }

        return res
    }
}