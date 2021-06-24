package ru.vldf.spider.generators.synthesizers

import com.squareup.javapoet.*
import ru.spbstu.insys.libsl.parser.AutomatonVariableStatement
import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext
import ru.vldf.spider.generators.descriptors.FileDescriptor
import javax.lang.model.element.Modifier

class ClassesInitializerSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (automaton in library.automata) {
            val classBuilder = TypeSpec.classBuilder(automaton.stringName)
            ctx.javaClassBuilders[automaton] = classBuilder

            classBuilder.addModifiers(Modifier.PUBLIC)

            // add states constants
            var i = 0
            automaton.states.forEach { state ->
                val field = FieldSpec.builder(
                    TypeName.INT,
                    state.name.getStateName(automaton).withoutDoubleDollar,
                    Modifier.PRIVATE,
                    Modifier.FINAL
                )
                    .initializer((i++).toString())
                    .build()
                classBuilder.addField(field)
            }

            // add STATE field
            classBuilder.addField(TypeName.INT, "STATE", Modifier.PUBLIC)

            // add variables
            automaton.statements.filterIsInstance<AutomatonVariableStatement>().forEach { variable ->
                val typeAlias = ctx.typesAliases[variable.type]
                val type = ClassName.get("", typeAlias)
                classBuilder.addField(type, variable.name, Modifier.PUBLIC)
            }

            for (method in ctx.functionsByAutomaton[automaton].orEmpty()) {
                val javaMethod = MethodSpec.methodBuilder(method.name)
                method.args.forEach { arg ->
                    val argType = ClassName.get("", ctx.typesAliases[arg.type.typeName])
                    javaMethod.addParameter(argType, arg.name)
                }

                javaMethod.addModifiers(Modifier.PUBLIC)
                if (method.returnValue != null) {
                    val realType = ctx.typesAliases[method.returnValue!!.type.typeName]
                    val returnType = ClassName.get("", realType)
                    javaMethod.returns(returnType)
                }
                ctx.functionToJavaMethod[method] = javaMethod
            }
        }
    }
}