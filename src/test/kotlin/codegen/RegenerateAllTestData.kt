package codegen

fun main() {
    wipeTestDataAndGenerateAllFiles("./src/test/resources/testData/simpleLibrary")
    wipeTestDataAndGenerateAllFiles("./src/test/resources/testData/okhttp")
}
