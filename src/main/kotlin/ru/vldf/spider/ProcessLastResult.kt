package ru.vldf.spider

import ru.spbstu.insys.libsl.parser.ModelParser
import ru.vldf.spider.configs.targetDir
import ru.vldf.spider.configs.tmpDir
import ru.vldf.spider.generators.SynthesizerPipelineBuilder
import java.io.File

fun main() {
    val lslPath = "./src/test/resources/testData/simpleLibrary/Computer.lsl"
    val parser = ModelParser()
    val stream = File(lslPath).inputStream()
    val parsed = parser.parse(stream)
    val codeSynthesizer = SynthesizerPipelineBuilder().build(parsed)
    val generatedContext = codeSynthesizer.generateCode()
    processKexResult(File(tmpDir.absolutePath + "/defects.json"), generatedContext, targetDir.absolutePath)
}