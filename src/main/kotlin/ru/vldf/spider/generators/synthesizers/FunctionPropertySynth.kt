package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class FunctionPropertySynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for ((function, javaMethod) in ctx.functionToJavaMethod) {
            if (function.isConstructor) {
                continue
            }
            for (assignment in function.variableAssignments) {
                val name = assignment.name
                if (name == "result") {
                    continue
                }
                javaMethod.apply {
                    addStatement("$name = new ${assignment.calleeAutomatonName}()")
                    val state = assignment.calleeArguments.firstOrNull()
                        ?: error("You should to specify state in ${function.name}")
                    val stateConstValue = ctx.statesMap[state.getStateName(assignment.calleeAutomatonName)]
                    addStatement("$name.STATE = $stateConstValue")
                }
            }
        }
    }
}