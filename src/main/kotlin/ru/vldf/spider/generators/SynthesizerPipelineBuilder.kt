package ru.vldf.spider.generators

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.synthesizers.*

class SynthesizerPipelineBuilder {
    private val pipeline = listOf(
        FunctionsInitializerSynth(),
        TypeAliasesInitializerSynth(),
        StatesInitializerSynth(),
        ClassesInitializerSynth(),

        RequiresSynth(),
        FinishstatesCheckerSynth(),
        ShiftsSynth(),
        FunctionPropertySynth(),
        EnsuresSynth(),
        ReturnStatementSynth(),

        BuildClasses()
    )

    fun build(library: LibraryDecl): AutomataSynthesizer {
        return AutomataSynthesizer(library).apply {
            initPipeline {
                pipeline.forEach { synth ->
                    addSynthesizer(synth)
                }
            }
        }
    }
}