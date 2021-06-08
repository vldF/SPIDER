package analysistests

import codegen.recursiveFileFinder
import codegen.runCodegenTest
import com.google.gson.GsonBuilder
import generators.descriptors.FileDescriptor
import javaPath
import kexIntrinsicsJarPath
import kexJarPath
import org.junit.jupiter.api.Assertions
import runKex
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader

private val testNameRegex = Regex("(testData\\/)(.+)(\\/)?")
private const val generatedCodeDir = "./src/test/generated/"
private const val testDataBaseDir = "./src/test/resources/testData/"
private const val testsBaseDir = "./src/test/"
private const val tmpDir = "./tmp/"
private val gson = GsonBuilder().setPrettyPrinting().create()
/**
 * @param lslsPath: path to dir contains lsl files .../resources/testData/TEST_DIR/
 */
fun runAnalysisTest(lslsPath: String) {
    cleanTmp()
    val testName = testNameRegex.find(lslsPath)?.groupValues?.get(2)
    val testGeneratedCodePath = "${generatedCodeDir}$testName"
    val generatedTestsFile = File(testGeneratedCodePath)
    val clientSourceFiles = File("$testDataBaseDir$testName/java")
    val librarySourceFiles = File("$testDataBaseDir$testName/javaLibrary")
    var targetLibraryFile = File("$testDataBaseDir$testName/javaClasses")

    if (!(generatedTestsFile.exists() && generatedTestsFile.isDirectory)) {
        runCodegenTest(lslsPath)
    }

    val generatedFileDescriptors = recursiveFileFinder(generatedTestsFile).map { file ->
        FileDescriptor(file.parentFile.path + "/", file.nameWithoutExtension, file.extension)
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
        .removePrefix(clientSourceFiles.path+"/")
        .replace("/", ".") + ".*"

    val libraryPackage = findLibraryPackage(librarySourceFiles)
        ?: throw IllegalArgumentException("Missing library package")

    runKex(
        kexJarPath,
        classPath = targetLibraryFile.absolutePath + ":" + targetFile.absolutePath,
        targetFile,
        "$libraryPackage.*",
        mainFileJavaLikePath
    )

    val expectedResultFile = File(testsBaseDir + "analysistests/" + testName + ".json")
    val actualResultFile = File(targetFile.canonicalPath + "/" + "defects.json")

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
    File(tmpDir + "javaLibrary" + "/" + "sources.txt").apply {
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
        "@" + tmpDir + "javaLibrary" + "/" + "sources.txt"
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
    File(tmpDir + "javaClient" + "/" + "sources.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(sourceFiles.joinToString("\n"))
    }
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-cp",
        "${kexIntrinsicsJarPath}:${libDir.absolutePath}",
        "-sourcepath",
        sourceCodeDir.absolutePath,
        "-d",
        resultDir.absolutePath,
        "@" + tmpDir + "javaClient" + "/" + "sources.txt"
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
    deleteFilesThatNamesEqualsWithGenerated(generatedFileNames, target, basePath)
    val sourceFile = File(codeFromDir.absolutePath + "/" + "sources.txt").apply {
        parentFile.mkdirs()
        createNewFile()
        writeText(generatedFileNames.joinToString("\n") { it.fullPath })
    }
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-cp",
        "${kexIntrinsicsJarPath}:${target}",
        "-sourcepath",
        codeFromDir.absolutePath,
        "-d",
        "$target",
        "@" + sourceFile.absolutePath
    )
    val runtime = Runtime.getRuntime()
    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    val error = BufferedReader(InputStreamReader(javacProcess.errorStream)).readText()
    val stdout = BufferedReader(InputStreamReader(javacProcess.inputStream)).readText()
    if (error.isNotEmpty()) {
        System.err.println("javac error: $error")
        println("javac stdout: $stdout")
        return false
    }

    return true
}

private fun deleteFilesThatNamesEqualsWithGenerated(fileDescriptors: List<FileDescriptor>, target: File, basePath: String) {
    for (descriptor in fileDescriptors) {
        File(target.absolutePath + "/" + descriptor.fullPathWithoutExtension.removePrefix(basePath) + ".class").delete()
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