package ru.vldf.spider.generators

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import ru.spbstu.insys.libsl.parser.Automaton
import ru.spbstu.insys.libsl.parser.FunctionDecl
import ru.vldf.spider.generators.descriptors.FileDescriptor

class SynthContext {
    val javaClassBuilders = mutableMapOf<Automaton, TypeSpec.Builder>()
    val functionsByAutomaton = mutableMapOf<Automaton, MutableList<FunctionDecl>>()
    val functionToJavaMethod = mutableMapOf<FunctionDecl, MethodSpec.Builder>()
    val typesAliases = mutableMapOf<String, String>()
    var assertionId = 0
    val statesMap = mutableMapOf<String, Int>()
    val finishstates = mutableMapOf<Automaton, MutableList<String>>()
    val errorIdMap = mutableMapOf<String, String>()

    val result = mutableMapOf<FileDescriptor, String>()
}