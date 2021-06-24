package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class FunctionsInitializerSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (function in library.functions) {
            if (library.automata.any { it.name.typeName == function.name }) {
                continue
            }

            val automatonName = function.entity.type.typeName
            val automaton = library.automata.firstOrNull{ it.name.typeName == automatonName }
                ?: error("unknown automaton")
            ctx.functionsByAutomaton.putIfAbsent(automaton, mutableListOf(function))?.add(function)
        }
    }
}