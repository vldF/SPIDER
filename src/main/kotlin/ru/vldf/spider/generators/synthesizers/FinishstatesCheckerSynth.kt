package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class FinishstatesCheckerSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (automaton in library.automata) {
            val finishstates = automaton.states.filter { it.isFinish }
            if (finishstates.isEmpty()) {
                continue
            }
            val functions = ctx.functionsByAutomaton[automaton].orEmpty()
            for (method in functions.map { ctx.functionToJavaMethod[it] }) {
                method?.apply {
                    val condition = finishstates.joinToString(prefix = "STATE != ", separator = "||") { it.name.getStateName(automaton) }
                    addKexAssert("Shift from finishstate: $condition", condition, ctx)
                } ?: throw IllegalStateException("non-bidirectional method relation: ${functions.joinToString()}")
            }
        }
    }
}