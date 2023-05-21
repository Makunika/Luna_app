package ru.pshiblo.luna.utils

import java.io.File

fun recursiveDelete(pathStr: String) {
    recursiveDelete(File(pathStr))
}

fun recursiveDelete(file: File) {
    if (!file.exists()) return
    if (file.isDirectory) {
        for (f in file.listFiles()) {
            recursiveDelete(f)
        }
    }
    file.delete()
}

fun getResourceAsFile(path: String): File? =
    object {}.javaClass.getResource(path)?.file?.let { File(it) }