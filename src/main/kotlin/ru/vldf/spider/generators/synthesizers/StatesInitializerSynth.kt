package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class StatesInitializerSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        var i = 0

        for (automaton in library.automata) {
            automaton.states.forEach { state ->
                val name = state.name.getStateName(automaton)
                ctx.statesMap[name] = ++i
                if (state.isFinish) {
                    ctx.finishstates.putIfAbsent(automaton, mutableListOf(name))?.add(name)
                }
            }
        }
    }
}