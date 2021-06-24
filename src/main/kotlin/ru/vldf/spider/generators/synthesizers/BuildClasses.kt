package ru.vldf.spider.generators.synthesizers

import com.squareup.javapoet.JavaFile
import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.SEP
import ru.vldf.spider.generators.SynthContext
import ru.vldf.spider.generators.descriptors.FileDescriptor

class BuildClasses : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for ((automaton, javaClass) in ctx.javaClassBuilders) {
            ctx.functionsByAutomaton[automaton]?.forEach { function ->
                javaClass
                    .addMethod(ctx.functionToJavaMethod[function]?.build())
            }

            val clazz = javaClass.build()
            val file = JavaFile.builder(automaton.javaPackage?.name, clazz).indent(" ".repeat(4))
            val resultFileDescriptor = FileDescriptor(
                automaton.javaPackage?.name?.replace(".", SEP) + SEP,
                automaton.stringName,
                "java"
            )
            ctx.result[resultFileDescriptor] = file.build().toString()
        }
    }
}