package org.xpathqs.prop.scan

import java.io.*
import java.lang.invoke.MethodHandles
import java.nio.file.FileSystems
import java.nio.file.Files

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.*
import java.util.jar.JarFile
import java.util.stream.Collectors


class ResourceScanner(
    private val path: String,
    private val loader: ClassLoader = Thread.currentThread().contextClassLoader
) {

    fun listFilesRecursively(basePath: String): List<File> {

        val url: URL = loader.getResource(basePath)
        val path: String = url.path.substringBeforeLast(File.separator)

        return if(path.contains("!")) {
            listFilesFromJar(url, basePath)
        } else {
            listFilesFromFileSystem(path)
        }
    }

    private fun getResourceUrl(path: String): Any? {
        val resource = loader.javaClass.getResource("/$path")
            ?: return Paths.get(path).toUri()
        return try {
            resource.toURI()
        } catch (e: FileSystemNotFoundException) {
            resource
        } catch (e: Exception) {
            Paths.get(path).toUri()
        }
    }

    private fun listFilesFromJar(resourceUrl: URL, basePath: String): List<File> {
        val (jarPath, jarEntryPath) = resourceUrl.file.split("!", limit = 2)
        val jarFile = JarFile(jarPath.removePrefix("file:"))
        val normalizedBasePath = jarEntryPath.removePrefix("/").let {
            if (it.isEmpty() || it.endsWith("/")) it else "$it/"
        }

        return jarFile.entries().asSequence()
            .filter { !it.isDirectory && it.name.startsWith(normalizedBasePath) }
            .map { JarVirtualFile(it.name, jarPath) }
            .toList()
            .also { jarFile.close() }
    }

    private fun listFilesFromFileSystem(uri: String): List<File> {
        return Files.walk(Paths.get(uri))
            .filter { !Files.isDirectory(it) }
            .map { it.toFile() }
            .collect(Collectors.toList())
    }

    /**
     * Virtual representation of a file inside a JAR
     */
    class JarVirtualFile(
        private val entryPath: String,
        private val jarPath: String
    ) : File(entryPath) {
        override fun exists(): Boolean = true
        override fun getName(): String = entryPath.substringAfterLast('/')
        override fun getPath(): String = "jar:file:$jarPath!/$entryPath"
        override fun toString(): String = path

        // Add other overrides as needed for your use case
        override fun isFile(): Boolean = true
        override fun isDirectory(): Boolean = false
        override fun length(): Long = -1L  // Unknown without reading the JAR entry
    }

    fun getAll(): Collection<File> {

        return listFilesRecursively(path)
    }
}