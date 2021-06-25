package ru.vldf.spider.generators.synthesizers

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.MethodSpec
import ru.spbstu.insys.libsl.parser.Automaton
import ru.spbstu.insys.libsl.parser.FunctionDecl
import ru.spbstu.insys.libsl.parser.SemanticType
import ru.vldf.spider.generators.SynthContext

private val kexIntrinsicsClassName = ClassName.get("org.jetbrains.research.kex", "Intrinsics")!!
val kexIntrinsicsObjectsClassName = ClassName.get("org.jetbrains.research.kex", "Objects")!!

fun String.getStateName(automaton: Automaton): String = this.getStateName(automaton.name.toString())

fun String.getStateName(automatonName: String) =
    "STATE\$CONST\$${automatonName}\$${toUpperCase()}".replace("$", "$$")

val String.withoutDoubleDollar: String
    get() = this.replace("\$\$", "\$")

val Automaton.javaLikePath: String
    get() = javaPackage?.name + "." + name.typeName

val Automaton.stringName: String
    get() = name.toString()

val String.automatonNameFromQualifiedName: String
    get() = this.split(".").dropLast(1).joinToString(".")

fun MethodSpec.Builder.addKexAssert(errorMessage: String, condition: String, ctx: SynthContext) {
    val errorId = "id${ctx.assertionId++}"
    ctx.errorIdMap[errorId] = errorMessage

    addStatement("\$T.kexAssert(\"$errorId\", $condition)", kexIntrinsicsClassName)
}

val FunctionDecl.isConstructor: Boolean
    get() = entity.type.typeName == name

fun SemanticType.toJavaType(ctx: SynthContext): ClassName {
    return ClassName.get("", ctx.typesAliases[typeName])
}