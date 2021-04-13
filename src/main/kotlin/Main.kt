import generators.Generator
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.required
import ru.spbstu.insys.libsl.parser.ModelParser
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader

private val targetDir = File("./result/")
private val tmpDir = File("./tmp/")
private const val javaPath = "/usr/lib/jvm/default-runtime/bin/"
private const val kexIntrinsicsJarPath = "/home/vldf/.m2/repository/org/jetbrains/research/kex-intrinsics/0.0.1/kex-intrinsics-0.0.1.jar"

fun main(args: Array<String>) {
    val argParser = ArgParser("libsl analyzer")
    val lslPath by argParser.option(ArgType.String, "lsl", "i", "lsl file path").required()
    val jarPath by argParser.option(ArgType.String, "libJar", "j", "library jar file path")
    val libraryDirPath by argParser.option(ArgType.String, "libDir", "d", "library dir path")
    argParser.parse(args)

    val parser = ModelParser()
    val stream = File(lslPath).inputStream()
    val parsed = parser.parse(stream)

    val generatedCodeFiles = Generator().generateCode(parsed)
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
            System.err.println("You must to specify libJar or libDir")
        }
    }
    saveGeneratedCodeToFile(generatedCodeFiles, saveToFile = tmpDir)
    compileMockCode(codeFromDir = tmpDir, generatedFileNames = generatedCodeFiles.keys.toList(), targetDir)
}

private fun unzipLibToPath(lib: File, target: File) {
    val jarArgs = arrayOf(
        "${javaPath}/jar",
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

private fun saveGeneratedCodeToFile(generated: Map<String, String>, saveToFile: File) {
    for ((fileName, code) in generated) {
        val file = File(saveToFile.absolutePath + "/" + fileName + ".java")
        file.parentFile.mkdirs()
        file.writeText(code)
    }
}

private fun compileMockCode(codeFromDir: File, generatedFileNames: List<String>, target: File) {
    deleteFilesThatNamesEqualsWithGenerated(generatedFileNames, target)
    File(codeFromDir.absolutePath + "/" + "@sources.txt").writeText(generatedFileNames.joinToString("\n") { "${codeFromDir.absolutePath}/$it.java" })
    val runtime = Runtime.getRuntime()
    val javacArgs = arrayOf(
        "${javaPath}javac",
        "-cp",
        "${kexIntrinsicsJarPath}:${target}",
        "-sourcepath",
        codeFromDir.absolutePath,
        "-d",
        "$target",
        "@./tmp/@sources.txt"
    )

    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    javacProcess.printOutput()
}

private fun deleteFilesThatNamesEqualsWithGenerated(fileNames: List<String>, target: File) {
    for (name in fileNames) {
        File(target.absolutePath + "/" + name + ".class").delete()
    }
}

private fun Process.printOutput() {
    val stdOutput = BufferedReader(InputStreamReader(inputStream)).readText()
    val errOutput = BufferedReader(InputStreamReader(errorStream)).readText()
    if (stdOutput.isNotBlank()) {
        println("javac std:")
        println(stdOutput)
    }

    if (errOutput.isNotBlank()) {
        System.err.println("javac err:")
        System.err.println(errOutput)
    }
}