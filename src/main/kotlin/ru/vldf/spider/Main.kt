package ru.vldf.spider

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.vldf.spider.generators.Generator
import ru.vldf.spider.generators.descriptors.FileDescriptor
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import ru.spbstu.insys.libsl.parser.ModelParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

val targetDir = File("./result/")
val tmpDir = File("./tmp/")
const val javaPath = "C:\\Program Files\\Java\\jdk-15.0.2\\bin\\"
const val kexIntrinsicsJarPath = "C:\\Users\\vladi\\Desktop\\kex-intrinsics-0.0.4.jar"
const val kexJarPath = "C:\\Users\\vladi\\IdeaProjects\\kex\\kex-runner\\target\\kex-runner-0.0.1-jar-with-dependencies.jar"
const val kexBaseDir = "C:\\Users\\vladi\\IdeaProjects\\kex\\"

fun main(args: Array<String>) {
    val argParser = ArgParser("SPecification Based Integration ru.vldf.spider.Defect Reveler")
    val lslPath by argParser.option(ArgType.String, "lsl", "i", "lsl file path").required()
    val jarPath by argParser.option(ArgType.String, "libJar", "j", "library jar file path")
    val libraryDirPath by argParser.option(ArgType.String, "libDir", "d", "library dir path")
    val libraryPackage by argParser.option(ArgType.String, "libPackage", "lp", "library package").required()
    val targetPackage by argParser.option(ArgType.String, "targetPackage", "tp", "target package").required()
    argParser.parse(args)

    val parser = ModelParser()
    val stream = File(lslPath).inputStream()
    val parsed = parser.parse(stream)

    val codeGenerator = Generator()
    val generatedCodeFiles = codeGenerator.generateCode(parsed)
    targetDir.deleteRecursively()
    targetDir.mkdirs()

    when {
        jarPath != null -> {
            val libraryFile = File(jarPath!!)
            unzipLibToPath(libraryFile, targetDir)
        }
        libraryDirPath != null -> {
            val libraryFile = File(libraryDirPath!!)
            copyLibFiles(libraryFile, targetDir)
        }
        else -> {
            System.err.println("You must specify libJar or libDir")
        }
    }
    saveGeneratedCodeToFile(generatedCodeFiles, saveToFile = tmpDir)
    compileMockCode(codeFromDir = tmpDir, generatedFileNames = generatedCodeFiles.keys.toList(), targetDir)

    println("the code was instrumented")
    println("running KEX...")
    runKex(kexJarPath, classPath = targetDir.absolutePath, tmpDir, targetPackage, libraryPackage)
    processKexResult(File(tmpDir.absolutePath + "/defects.json"), codeGenerator, targetDir.absolutePath)
}

private fun unzipLibToPath(lib: File, target: File) {
    val jarArgs = arrayOf(
        "$javaPath/jar",
        "-xf",
        target.path,
        lib.path
    )
    val runtime = Runtime.getRuntime()
    val jarProcess = runtime.exec(jarArgs)
    jarProcess.waitFor()
    jarProcess.printOutput()
}

private fun copyLibFiles(libPath: File, target: File) {
    libPath.copyRecursively(target)
}

private fun saveGeneratedCodeToFile(generated: Map<FileDescriptor, String>, saveToFile: File) {
    for ((fileDescriptor, code) in generated) {
        val file = File(saveToFile.absolutePath + "/" + fileDescriptor.fullPath)
        file.parentFile.mkdirs()
        file.writeText(code)
    }
}

fun compileMockCode(codeFromDir: File, generatedFileNames: List<FileDescriptor>, target: File) {
    deleteFilesThatNamesEqualsWithGenerated(generatedFileNames, target)
    File(codeFromDir.absolutePath + "/" + "@sources.txt").writeText(generatedFileNames.joinToString("\n") { "${codeFromDir.absolutePath}/${it.fullPath}" })
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-cp",
        "$kexIntrinsicsJarPath:${target}",
        "-sourcepath",
        codeFromDir.absolutePath,
        "-d",
        "$target",
        "@./tmp/@sources.txt"
    )
    val runtime = Runtime.getRuntime()
    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    javacProcess.printOutput()
}

private fun deleteFilesThatNamesEqualsWithGenerated(fileDescriptors: List<FileDescriptor>, target: File) {
    for (descriptor in fileDescriptors) {
        File(target.absolutePath + "/" + descriptor.fullPath).delete()
    }
}

/*
./kex.sh
    --classpath ./vldf
    --target ru.vldf.testlibrary.*
    --output kex-instrumented
    --mode libchecker
    --libCheck ru.vldf.testlibrary.*
    --log vldf.log
 */
fun runKex(kexPath: String, classPath: String, tmpDir: File, libraryTarget: String, clientTarget: String) {
    val workingDir = File(kexBaseDir)
    val kexArgs = arrayOf(
        "$javaPath/java",
        "-Xmx16384m",
        "-Djava.security.manager",
        "-Djava.security.policy==kex.policy",
        "-jar",
        kexPath,
        "--classpath",
        classPath,
        "--output",
        "kex-instrumented",
        "--mode",
        "libchecker",
        "--libCheck",
        clientTarget,
        "--option",
        "defect:outputFile:${tmpDir.absolutePath}/defects.json",
        "--target",
        libraryTarget,
        "--log",
        "kex.log",
        "--config",
        "C:\\Users\\vladi\\IdeaProjects\\kex\\kex-test.ini"
    )
    val runtime = Runtime.getRuntime()
    val kexProcess = runtime.exec(kexArgs, null, workingDir)
    kexProcess.waitFor()
    kexProcess.printOutput()
}

fun processKexResult(defectFile: File, codeGenerator: Generator, basePath: String) {
    val gson = Gson()
    val defectsArrayType = (object : TypeToken<Array<Defect>>() {}).type
    val report: Array<Defect> = gson.fromJson<Array<Defect>>(defectFile.readText(), defectsArrayType)
    for ((i, defect) in report.withIndex()) {
        if (defect.type != "ASSERT") continue
        val description = codeGenerator.errorIdMap[defect.id] ?: "unknown wrong shift"
        printError("Error #$i:")
        printError(processStackTrace(defect.callStack, basePath))
        printError("description $description")
    }
}

private fun processStackTrace(trace: Array<String>, basePath: String): String {
    return trace
        .dropLast(1)
        .map { it.split(" - ") }
        .joinToString(separator = "\n") { (first, _) -> "on method $first" }
}

fun Process.printOutput() {
    val stdOutput = BufferedReader(InputStreamReader(inputStream)).readText()
    val errOutput = BufferedReader(InputStreamReader(errorStream)).readText()
    if (stdOutput.isNotBlank()) {
        println("std:")
        println(stdOutput)
    }

    if (errOutput.isNotBlank()) {
        System.err.println("err:")
        System.err.println(errOutput)
    }
}

private fun printError(str: String) {
    System.err.println(str)
}