package codegen

import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun okhttp() {
        runCodegenTest("./src/test/resources/testData/okhttp")
    }

    @Test
    fun simpleLibrary() {
        runCodegenTest("./src/test/resources/testData/simpleLibrary")
    }
}
