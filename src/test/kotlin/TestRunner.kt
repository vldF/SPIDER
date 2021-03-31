import generators.Generator
import org.junit.jupiter.api.Assertions
import ru.spbstu.insys.libsl.parser.ModelParser
import java.io.File

private const val basePath = "./src/test/generated/" // / in the end of path is important

fun runTest(dirPath: String) {
    val dir = File(dirPath)
    val testName = dir.name
    // todo: add multipy files supportions

    val lslFile = dir.listFiles()!!.first { it.name.endsWith(".lsl") }
    val parser = ModelParser()
    val stream = lslFile.inputStream()
    val parsed = parser.parse(stream)

    val generated = Generator().generateCode(parsed)

    val oldTestFile = File("$basePath$testName")
    if (!oldTestFile.exists()) {
        oldTestFile.mkdirs()
        for ((path, code) in generated) {
            val file = File("$basePath$testName/$path.java")
            val dirToBeCreated = file.parentFile
            dirToBeCreated.mkdirs()
            file.createNewFile()
            file.writeText(code)
        }

        error("New test data was created: $testName")
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
        val file = File("$basePath$testName/$newFile.java")
        file.parentFile.mkdirs()
        file.createNewFile()
        file.writeText(generated[newFile]!!)
    }

    if (deltaFiles.isNotEmpty()) {
        error("New files ${deltaFiles.joinToString(",")} were generated")
    }
}

fun wipeTestDataAndGenerateAllFiles(dirPath: String) {
    val dir = File(dirPath)
    val testName = dir.name

    File(basePath+testName).deleteRecursively()

    // todo: add multipy files supportions
    val lslFile = dir.listFiles()!!.first { it.name.endsWith(".lsl") }

    val parser = ModelParser()
    val stream = lslFile.inputStream()
    val parsed = parser.parse(stream)

    val generated = Generator().generateCode(parsed)

    for ((path, code) in generated) {
        val codeFile = File("$basePath$testName/$path.java")
        codeFile.parentFile.mkdirs()
        codeFile.writeText(code)
    }
}

private fun recursiveFileFinder(baseDir: File): List<File> {
    val dirContent = baseDir.listFiles() ?: return listOf()
    return dirContent.filter{ it.isFile && it.name.endsWith(".java") } +
            dirContent.filter { it.isDirectory }.flatMap { recursiveFileFinder(it) }
}

private fun String.toSimpleFileNameWithoutExtension(oldTestFile: File): String {
    return removePrefix(oldTestFile.path+"/").removeSuffix(".java")
}