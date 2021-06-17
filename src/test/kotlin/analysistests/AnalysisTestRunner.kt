package analysistests

import ru.vldf.spider.SEP
import codegen.recursiveFileFinder
import codegen.runCodegenTest
import com.google.gson.GsonBuilder
import ru.vldf.spider.generators.descriptors.FileDescriptor
import ru.vldf.spider.javaPath
import ru.vldf.spider.kexIntrinsicsJarPath
import ru.vldf.spider.kexJarPath
import org.junit.jupiter.api.Assertions
import ru.vldf.spider.programrunners.JavacRunner
import ru.vldf.spider.runKex
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

private val testNameRegex = Regex("(testData\\/)(.+)(\\/)?")
private val generatedCodeDir = ".${SEP}src${SEP}test${SEP}generated$SEP"
private val testDataBaseDir = ".${SEP}src${SEP}test${SEP}resources${SEP}testData$SEP"
private val testsBaseDir = ".${SEP}src${SEP}test$SEP"
private val tmpDir = ".${SEP}tmp$SEP"
private val gson = GsonBuilder().setPrettyPrinting().create()
/**
 * @param lslsPath: path to dir contains lsl files .../resources/testData/TEST_DIR/
 */
fun runAnalysisTest(lslsPath: String) {
    cleanTmp()
    val testName = testNameRegex.find(lslsPath)?.groupValues?.get(2)
    val testGeneratedCodePath = "${generatedCodeDir}$testName"
    val generatedTestsFile = File(testGeneratedCodePath)
    val clientSourceFiles = File("$testDataBaseDir$testName${SEP}java")
    val librarySourceFiles = File("$testDataBaseDir$testName${SEP}javaLibrary")
    var targetLibraryFile = File("$testDataBaseDir$testName${SEP}javaClasses")

    if (!(generatedTestsFile.exists() && generatedTestsFile.isDirectory)) {
        runCodegenTest(lslsPath)
    }

    val generatedFileDescriptors = recursiveFileFinder(generatedTestsFile).map { file ->
        FileDescriptor(file.parentFile.path + SEP, file.nameWithoutExtension, file.extension)
    }
    val targetFile = File(tmpDir + testName + "Client").apply { mkdirs() }

    if (librarySourceFiles.exists()) {
        targetLibraryFile = File(tmpDir + testName + "Library").apply { mkdirs() }

        val resLibraryJavac = compileJavaLibrarySources(librarySourceFiles, targetLibraryFile)
        if (!resLibraryJavac) {
            throw IllegalStateException("library compilation failed")
        }
    }
    val resCompileMock = compileMockCode(
        codeFromDir = generatedTestsFile,
        generatedFileNames = generatedFileDescriptors,
        generatedTestsFile.path,
        targetLibraryFile
    )
    if (!resCompileMock) {
        throw IllegalStateException("mock compilation failed")
    }
    val resClientJavac = compileJavaClientSources(clientSourceFiles, targetLibraryFile, targetFile)
    if (!resClientJavac) {
        throw IllegalStateException("client compilation failed")
    }

    val mainFile = recursiveFileFinder(clientSourceFiles).firstOrNull { it.name == "Main.java" }?.parentFile?.path
        ?: throw IllegalArgumentException("Testdata not contains Main.java")
    val mainFileJavaLikePath = mainFile
        .removePrefix(clientSourceFiles.path+ SEP)
        .replace(SEP, ".") + ".*"

    val libraryPackage = findLibraryPackage(librarySourceFiles)
        ?: throw IllegalArgumentException("Missing library package")

    println("running kex")

    runKex(
        kexJarPath,
        classPath = targetLibraryFile.absolutePath + ";" + targetFile.absolutePath,
        targetFile,
        "$libraryPackage.*",
        mainFileJavaLikePath
    )

    val expectedResultFile = File(testsBaseDir + "analysistests$SEP" + testName + ".json")
    val actualResultFile = File(targetFile.canonicalPath + SEP + "defects.json")

    val actualResult = gson.toJson(gson.fromJson(actualResultFile.readText(), Any::class.java))
    if (!expectedResultFile.exists()) {
        expectedResultFile.run {
            parentFile.mkdirs()
            createNewFile()
            writeText(actualResult)
        }

        throw FileNotFoundException("new file was created: $testName.json")
    } else {
        val expectedResult = gson.toJson(gson.fromJson(expectedResultFile.readText(), Any::class.java))
        Assertions.assertEquals(expectedResult, actualResult)

    }

}

private fun cleanTmp() {
    File(tmpDir).apply {
        deleteRecursively()
        mkdirs()
    }
}

private fun compileJavaLibrarySources(sourceCodeDir: File, resultDir: File): Boolean {
    val sourceFiles = recursiveFileFinder(sourceCodeDir).map { it.absolutePath }
    File(tmpDir + "javaLibrary" + SEP + "sources.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(sourceFiles.joinToString("\n"))
    }
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-sourcepath",
        sourceCodeDir.absolutePath,
        "-d",
        "$resultDir",
        "@" + tmpDir + "javaLibrary" + SEP + "sources.txt"
    )
    val runtime = Runtime.getRuntime()
    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    val error = BufferedReader(InputStreamReader(javacProcess.errorStream)).readText()
    if (error.isNotEmpty()) {
        System.err.println("javac error: $error")
        return false
    }

    return true
}

private fun compileJavaClientSources(sourceCodeDir: File, libDir: File, resultDir: File): Boolean {
    val sourceFiles = recursiveFileFinder(sourceCodeDir).map { it.absolutePath }
    File(tmpDir + "javaClient" + SEP + "sources.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(sourceFiles.joinToString("\n"))
    }
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-cp",
        "$kexIntrinsicsJarPath;${libDir.absolutePath}",
        "-sourcepath",
        sourceCodeDir.absolutePath,
        "-d",
        resultDir.absolutePath,
        "@" + tmpDir + "javaClient" + SEP + "sources.txt"
    )
    val runtime = Runtime.getRuntime()
    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    val error = BufferedReader(InputStreamReader(javacProcess.errorStream)).readText()
    if (error.isNotEmpty()) {
        System.err.println("javac error: $error")
        return false
    }

    return true
}

private fun compileMockCode(codeFromDir: File, generatedFileNames: List<FileDescriptor>, basePath: String, target: File): Boolean {
    println("compiling the mock")
    deleteFilesThatNamesEqualsWithGenerated(generatedFileNames, target, basePath)

    val sourceFile = File(codeFromDir.absolutePath + SEP + "sources.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(generatedFileNames.joinToString("\n") { it.fullPath })
    }

    return JavacRunner()
        .addArg("-cp", "$kexIntrinsicsJarPath;${target}")// todo: stupid windows
        .addArg("-sourcepath", codeFromDir.absolutePath)
        .addArg("-verbose")
        .addArg("-d", target.toString())
        .addArg("@" + sourceFile.absolutePath)
        .runAndWait()
}

private fun deleteFilesThatNamesEqualsWithGenerated(fileDescriptors: List<FileDescriptor>, target: File, basePath: String) {
    for (descriptor in fileDescriptors) {
        File(target.absolutePath + SEP + descriptor.fullPathWithoutExtension.removePrefix(basePath) + ".class").delete()
    }
}

private fun findLibraryPackage(dir: File): String? {
    return if (dir.listFiles()?.size == 1) {
        val nextPart = findLibraryPackage(dir.listFiles()!!.first())
        when {
            dir.name == "javaLibrary" -> {
                nextPart
            }
            nextPart != null -> {
                dir.name + "." + nextPart
            }
            else -> {
                dir.name
            }
        }
    } else dir.name
}