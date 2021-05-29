package analysistests

import codegen.runCodegenTest
import compileMockCode
import generators.descriptors.FileDescriptor
import kexJarPath
import processKexResult
import runKex
import java.io.File

private val testNameRegex = Regex("(testData\\/)(.+)(\\/)")
private const val generatedCodeDir = "./src/test/generated/"
private const val tmpDir = "./tmp/"
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

    val generatedFileDescriptors = generatedTestsFile.listFiles()!!.map { file ->
        FileDescriptor(file.path, file.nameWithoutExtension, file.extension)
    }
    val targetFile = File(tmpDir + testName)

    compileMockCode(codeFromDir = generatedTestsFile, generatedFileNames = generatedFileDescriptors, targetFile)
    runKex(kexJarPath, classPath = targetFile.absolutePath, targetFile, "", "")
}