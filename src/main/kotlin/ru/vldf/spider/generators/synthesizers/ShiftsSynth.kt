package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class ShiftsSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (automaton in library.automata) {
            for ((function, javaMethod) in ctx.functionToJavaMethod) {
                val shiftsMap = automaton
                    .shifts
                    .filter { it.functions.contains(function.name) }
                    .map { it.from.getStateName(automaton) to it.to.getStateName(automaton) }

                val anyShifts = shiftsMap.filter { it.first == "any".getStateName(automaton) }
                if (anyShifts.size > 1 || anyShifts.isNotEmpty() && shiftsMap.size > anyShifts.size) {
                    throw IllegalStateException("Ambiguity `any` transition in ${automaton.name}.${function.name}")
                }
                if (anyShifts.isEmpty()) {
                    javaMethod.apply {
                        var isFirstLoop = true
                        for ((fromState, toState) in shiftsMap) {
                            if (isFirstLoop) {
                                beginControlFlow("if (STATE == $fromState)")
                                isFirstLoop = false
                            } else {
                                nextControlFlow("else if (STATE == $fromState)")
                            }

                            if (toState != "self".getStateName(automaton)) {
                                addStatement("STATE = $toState")
                            }
                        }

                        if (!isFirstLoop) {
                            // if loop was run at least once...
                            nextControlFlow("else")
                            addKexAssert("Invalid shift by calling method ${function.name}", "false", ctx)
                            endControlFlow()
                        }
                    }
                } else {
                    javaMethod.addStatement("STATE = ${anyShifts.first().second}")
                }
            }
        }
    }
}