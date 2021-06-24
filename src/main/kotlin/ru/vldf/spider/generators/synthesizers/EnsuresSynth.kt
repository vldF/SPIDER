package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class EnsuresSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (automaton in library.automata) {
            for ((function, javaMethod) in ctx.functionToJavaMethod) {
                val ensures = function.contracts.ensures
                if (ensures != null) {
                    javaMethod.addKexAssert(
                        "`ensures` contract $ensures failed for function ${automaton.name}.${function.name}",
                        ensures,
                        ctx
                    )
                }
            }
        }
    }
}