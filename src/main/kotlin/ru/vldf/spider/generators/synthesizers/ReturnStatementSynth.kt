package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class ReturnStatementSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for ((function, javaMethod) in ctx.functionToJavaMethod) {
            if (function.isConstructor) {
                continue
            }
            if (function.returnValue != null) {
                val resultAssignments = function.variableAssignments.filter { it.name == "result" }
                when {
                    resultAssignments.isEmpty() -> {
                        javaMethod.addStatement("return \$T.kexUnknown()", kexIntrinsicsObjectsClassName)
                    }
                    resultAssignments.size == 1 -> {
                        val resultAssignment = resultAssignments.first()
                        javaMethod.addStatement("${resultAssignment.calleeAutomatonName} tmpRes = new ${resultAssignment.calleeAutomatonName}()")
                        val state = resultAssignment.calleeArguments.firstOrNull()
                            ?: error("You should to specify state in ${function.name}")
                        val stateConstValue = ctx.statesMap[state.getStateName(resultAssignment.calleeAutomatonName)]
                        javaMethod.addStatement("tmpRes.STATE = $stateConstValue")
                        javaMethod.addStatement("return tmpRes")
                    }
                    else -> {
                        error("too much `result` variables")
                    }
                }
            }
        }
    }

}