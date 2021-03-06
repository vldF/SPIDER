package codegen

import ru.vldf.spider.SEP
import org.junit.jupiter.api.Assertions
import ru.spbstu.insys.libsl.parser.ModelParser
import ru.vldf.spider.generators.SynthesizerPipelineBuilder
import java.io.File

private val basePath = ".${SEP}src${SEP}test${SEP}generated$SEP" // / in the end of path is important

fun runCodegenTest(dirPath: String, throwException: Boolean = true) {
    val dir = File(dirPath)
    val testName = dir.name
    // todo: add multiply files supporting

    val lslFile = dir.listFiles()!!.first { it.name.endsWith(".lsl") }
    val parser = ModelParser()
    val stream = lslFile.inputStream()
    val parsed = parser.parse(stream)

    val synthContext = SynthesizerPipelineBuilder().build(parsed).generateCode()
    val generated = synthContext
        .result
        .entries
        .associate { it.key.fullPathWithoutExtension to it.value }

    val oldTestFile = File("$basePath$testName")
    if (!oldTestFile.exists()) {
        oldTestFile.mkdirs()
        for ((path, code) in generated) {
            val file = File("$basePath$testName$SEP$path.java")
            val dirToBeCreated = file.parentFile
            dirToBeCreated.mkdirs()
            file.createNewFile()
            file.writeText(code)
        }
        if (throwException) {
            error("New test data was created: $testName")
        }
    }

    val oldTestFiles = recursiveFileFinder(oldTestFile)

    for (oldGeneratedCode in oldTestFiles) {
        if (oldGeneratedCode.isDirectory || !oldGeneratedCode.name.endsWith(".java")) continue

        val fileName = oldGeneratedCode.path.toSimpleFileNameWithoutExtension(oldTestFile)
        val oldCode = oldGeneratedCode.readText()
        val newCode = generated[fileName]

        Assertions.assertEquals(oldCode, newCode)
    }

    val newGeneratedFiles = generated.keys.toSet()
    val deltaFiles = newGeneratedFiles - oldTestFiles.map { it.path.toSimpleFileNameWithoutExtension(oldTestFile) }.toSet()

    for (newFile in deltaFiles) {
        val file = File("$basePath$testName$SEP$newFile.java")
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(generated[newFile]!!)
    }

    if (deltaFiles.isNotEmpty() && throwException) {
        error("New files ${deltaFiles.joinToString(",")} were generated")
    }
}

fun wipeTestDataAndGenerateAllFiles(dirPath: String) {
    val dir = File(dirPath)
    val testName = dir.name

    File(basePath +testName).deleteRecursively()

    // todo: add multipy files supportions
    val lslFile = dir.listFiles()!!.first { it.name.endsWith(".lsl") }

    val parser = ModelParser()
    val stream = lslFile.inputStream()
    val parsed = parser.parse(stream)

    val synthContext = SynthesizerPipelineBuilder().build(parsed).generateCode()
    val generated = synthContext
        .result
        .entries
        .associate { it.key.fullPathWithoutExtension to it.value }

    for ((path, code) in generated) {
        val codeFile = File("$basePath$testName$SEP$path.java")
        codeFile.parentFile.mkdirs()
        codeFile.writeText(code)
    }
}

fun recursiveFileFinder(baseDir: File): List<File> {
    val dirContent = baseDir.listFiles() ?: return listOf()
    return dirContent.filter{ it.isFile && it.name.endsWith(".java") } +
            dirContent.filter { it.isDirectory }.flatMap { recursiveFileFinder(it) }
}

private fun String.toSimpleFileNameWithoutExtension(oldTestFile: File): String {
    return removePrefix(oldTestFile.path + SEP).removeSuffix(".java")
}