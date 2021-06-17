package ru.vldf.spider

import java.io.File

val SEP = File.separator
val executableFileExtension = if (System.getProperty("os.name").startsWith("Windows")) ".exe" else ""