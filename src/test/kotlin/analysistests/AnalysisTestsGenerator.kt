package analysistests

import java.io.File

const val codegenPath = "./src/test/kotlin/analysistests/"
const val testDataPath = "./src/test/resources/testData/"

/*
These tests run full analysis pipeline (codegen + mocking + running KEX)
 */
fun main() {
    val testsFile = buildString {
        appendLine("package analysistests")
        appendLine()
        appendLine("import org.junit.jupiter.api.Test")
        appendLine()
        appendLine("class AnalysisTestsCodegen {")
        val testDataFile = File(testDataPath)
        for (dir in testDataFile.listFiles()!!.sorted()) {
            if (!dir.isDirectory) {
                continue
            }

            val testName = dir.name
            appendLine(generateTestFunction(dir.absolutePath.removePrefix(testDataPath), testName))
        }
        appendLine("}")
    }

    File(codegenPath + "AnalysisTestsCodegen.kt").writeText(testsFile)
}

private fun generateTestFunction(path: String, name: String): String {
    return buildString {
        appendLine("@Test")
        appendLine("fun $name() {")
        appendLine("    runAnalysisTest(\"$path\")")
        appendLine("}")
    }.insertIndent(4)
}

private fun String.insertIndent(n: Int): String {
    return this
        .lines()
        .joinToString(separator = "\n") { line -> " ".repeat(n) + line }
}
