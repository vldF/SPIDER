package ru.vldf.spider

import ru.vldf.spider.generators.Generator
import ru.spbstu.insys.libsl.parser.ModelParser
import java.io.File

fun main() {
    val lslPath = "./src/test/resources/testData/simpleLibrary/Computer.lsl"
    val parser = ModelParser()
    val stream = File(lslPath).inputStream()
    val parsed = parser.parse(stream)
    val codeGenerator = Generator()
    codeGenerator.generateCode(parsed)
    processKexResult(File(tmpDir.absolutePath + "/defects.json"), codeGenerator, targetDir.absolutePath)
}