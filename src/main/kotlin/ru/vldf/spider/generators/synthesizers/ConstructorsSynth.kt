package ru.vldf.spider.generators.synthesizers

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.ParameterSpec
import ru.spbstu.insys.libsl.parser.FunctionDecl
import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext
import javax.lang.model.element.Modifier

class ConstructorsSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for ((automaton, functions) in ctx.functionsByAutomaton) {
            val constructors = functions.filter(FunctionDecl::isConstructor)
            if (constructors.isEmpty()) continue

            functions.removeAll(constructors)

            val javaClass = ctx.javaClassBuilders[automaton] ?: error("unknown automaton")

            for (constructor in constructors) {
                val javaConstructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC)

                constructor.args.forEach {
                    javaConstructor.addParameter(ParameterSpec.builder(it.type.toJavaType(ctx), it.name).build())
                }

                for (assignment in constructor.variableAssignments) {
                    if (assignment.name == "result") {
                        val state = assignment.calleeArguments.first()
                        javaConstructor.addStatement(
                            "STATE = ${state.getStateName(automaton)}"
                        )
                    }
                    else {
                        javaConstructor.addStatement(
                            "${assignment.name} = new ${assignment.calleeAutomatonName}(${
                                assignment.calleeArguments.joinToString(
                                    separator = ", "
                                )
                            })"
                        )
                    }
                }

                javaClass.addMethod(javaConstructor.build())
            }
        }
    }
}