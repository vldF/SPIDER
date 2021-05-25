package codegen

import org.junit.jupiter.api.Test

class Tests {

    @Test
    fun simpleLibrary() {
        runCodegenTest("./src/test/resources/testData/simpleLibrary")
    }
}
