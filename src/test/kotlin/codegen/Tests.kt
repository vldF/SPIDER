package codegen

import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun simpleLibrary() {
        codegen.runTest("./src/test/resources/testData/simpleLibrary")
    }
}
