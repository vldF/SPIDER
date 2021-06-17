package ru.vldf.spider.generators.descriptors

data class FileDescriptor (
    val path: String,
    val nameWithoutExtension: String,
    val extension: String
) {
    val fullPath: String
        get() = "$path/$name"
    val fullPathWithoutExtension: String
        get() = "$path$nameWithoutExtension"
    val pathWithSlash: String
        get() = if (path.endsWith("/") || path.endsWith("\\")) path else "$path/"
    val name: String
        get() = "$nameWithoutExtension.$extension"
}