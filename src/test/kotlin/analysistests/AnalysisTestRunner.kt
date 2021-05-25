package analysistests

import codegen.runCodegenTest
import java.io.File

private val testNameRegex = Regex("(testData\\/)(.+)(\\/)")
private const val generatedCodeDir = "./src/test/generated/"
/**
 * @param lslsPath: path to dir contains lsl files .../resources/testData/TEST_DIR/
 */
fun runAnalysisTest(lslsPath: String) {
    // is test's codegen exists?
    val testName = testNameRegex.find(lslsPath)
    val testGeneratedCodePath = "${generatedCodeDir}$testName"
    val generatedTestsFile = File(testGeneratedCodePath)

    if (!(generatedTestsFile.exists() && generatedTestsFile.isDirectory)) {
        runCodegenTest(lslsPath)
    }


}