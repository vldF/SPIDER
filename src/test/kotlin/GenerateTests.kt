import java.io.File

private const val baseTestDataRoot = "./src/test/resources/testData/"

fun main() {
    val testDataDir = File(baseTestDataRoot)
    val testDataDirs = testDataDir.listFiles() ?: throw IllegalArgumentException("testData is empty")
    val codeFile = File("./src/test/kotlin/Tests.kt")
    val testsCode = buildString {
        appendLine("import org.junit.jupiter.api.Test")
        appendLine()
        appendLine("class Tests {")
        for (testData in testDataDirs.sorted()) {
            append(generateTestFunction(testData.name))
        }
        appendLine("}")
    }

    codeFile.writeText(testsCode)

    val wiperCode = generateWiperFunction(testDataDirs.map { it.name })

    val wiperCodeFile = File("./src/test/kotlin/RegenerateAllTestData.kt")
    wiperCodeFile.writeText(wiperCode)
}

/*
    @Test
    fun NAME() {
        runTest(PATH)
    }
*/
private fun generateTestFunction(name: String): String {
    return buildString {
        appendLine()
        appendLine("    @Test")
        appendLine("    fun $name() {")
        appendLine("        runTest(\"$baseTestDataRoot$name\")")
        appendLine("    }")
    }
}

/*
fun main() {
    wipeTestDataAndGenerateAllFiles(PATH1)
    wipeTestDataAndGenerateAllFiles(PATH2)
    wipeTestDataAndGenerateAllFiles(PATH3)
    ...
}
*/
private fun generateWiperFunction(names: List<String>): String {
    return buildString {
        appendLine("fun main() {")

        for (name in names) {
            append(" ".repeat(4))
            appendLine("wipeTestDataAndGenerateAllFiles(\"$baseTestDataRoot$name\")")
        }

        appendLine("}")
    }
}