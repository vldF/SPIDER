import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import generators.Generator
import generators.descriptors.FileDescriptor
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
private const val kexJarPath = "/home/vldf/IdeaProjects/kex/kex-runner/target/kex-runner-0.0.1-jar-with-dependencies.jar"
private const val kexBaseDir = "/home/vldf/IdeaProjects/kex/"

fun main(args: Array<String>) {
    val argParser = ArgParser("libsl analyzer")
    val lslPath by argParser.option(ArgType.String, "lsl", "i", "lsl file path").required()
    val jarPath by argParser.option(ArgType.String, "libJar", "j", "library jar file path")
    val libraryDirPath by argParser.option(ArgType.String, "libDir", "d", "library dir path")
    val subject by argParser.option(ArgType.String, "subject", "s", "Class, package or method").required()
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
            System.err.println("You must to specify libJar or libDir")
        }
    }
    saveGeneratedCodeToFile(generatedCodeFiles, saveToFile = tmpDir)
    compileMockCode(codeFromDir = tmpDir, generatedFileNames = generatedCodeFiles.keys.toList(), targetDir)

    println("the code was instrumented")
    println("running KEX...")
    runKex(kexJarPath, classPath = targetDir.absolutePath, tmpDir, subject)
    processKexResult(File(tmpDir.absolutePath + "/" + "defect.json"), codeGenerator)
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

private fun saveGeneratedCodeToFile(generated: Map<FileDescriptor, String>, saveToFile: File) {
    for ((fileName, code) in generated) {
        val file = File(saveToFile.absolutePath + "/" + fileName + ".java")
        file.parentFile.mkdirs()
        file.writeText(code)
    }
}

private fun compileMockCode(codeFromDir: File, generatedFileNames: List<FileDescriptor>, target: File) {
    deleteFilesThatNamesEqualsWithGenerated(generatedFileNames, target)
    File(codeFromDir.absolutePath + "/" + "@sources.txt").writeText(generatedFileNames.joinToString("\n") { "${codeFromDir.absolutePath}/$it.java" })
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
    val runtime = Runtime.getRuntime()
    val javacProcess = runtime.exec(javacArgs)
    javacProcess.waitFor()
    javacProcess.printOutput()
}

private fun deleteFilesThatNamesEqualsWithGenerated(fileNames: List<FileDescriptor>, target: File) {
    for (name in fileNames) {
        File(target.absolutePath + "/" + name.name + ".class").delete()
    }
}

private fun runKex(kexPath: String, classPath: String, tmpDir: File, subject: String) {
    val workingDir = File(kexBaseDir)
    val kexArgs = arrayOf(
        "$javaPath/java",
        "-Xmx16384m",
        "-Djava.security.manager",
        "-Djava.security.policy==kex.policy",
        "-jar",
        kexPath,
        "-cp",
        classPath,
        "-m",
        "checker",
        "-t",
        subject,
        "--option",
        "defect:outputFile",
        "${tmpDir.absolutePath}/defect.json"
    )
    val runtime = Runtime.getRuntime()
    val kexProcess = runtime.exec(kexArgs, null, workingDir)
    kexProcess.waitFor()
    kexProcess.printOutput()
}

private fun processKexResult(defectFile: File, codeGenerator: Generator) {
    val gson = Gson()
    val defectsArrayType = (object : TypeToken<Array<Defect>>() {}).type
    val report = gson.fromJson<Array<Defect>>(defectFile.readText(), defectsArrayType)
    for (defect in report) {
        if (defect.type != "ASSERT") continue
        val description = codeGenerator.errorIdMap[defect.id] ?: "unknown wrong shift"
        println("Error on ${defect.location.`package`.name}/${defect.location.file}:${defect.location.line}:")
        println(description)
        println("Case on ${defect.testFile}:${defect.testCaseName}")
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