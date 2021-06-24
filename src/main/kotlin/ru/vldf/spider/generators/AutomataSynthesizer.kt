package ru.vldf.spider.generators

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.synthesizers.SynthesizerInterface

class AutomataSynthesizer(val library: LibraryDecl) {
    private val synthesizersPipeline = mutableListOf<SynthesizerInterface>()
    private val ctx = SynthContext()

    fun initPipeline(initializer: Initializer.() -> Unit){
        Initializer(this).initializer()
    }

    fun generateCode(): SynthContext {
        for (generator in synthesizersPipeline) {
            generator.generate(ctx, library)
        }

        return ctx
    }

    class Initializer(private val automataSynthesizer: AutomataSynthesizer) {
        fun addSynthesizer(synth: SynthesizerInterface) {
            automataSynthesizer.synthesizersPipeline.add(synth)
        }
    }

}