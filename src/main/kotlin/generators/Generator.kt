package generators

import com.hendraanggrian.javapoet.buildJavaFile
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import exceptions.SemanticException
import exceptions.UnknownStateException
import generators.descriptors.FileDescriptor
import ru.spbstu.insys.libsl.parser.Automaton
import ru.spbstu.insys.libsl.parser.AutomatonVariableStatement
import ru.spbstu.insys.libsl.parser.FunctionDecl
import ru.spbstu.insys.libsl.parser.LibraryDecl
import javax.lang.model.element.Modifier
import kotlin.collections.set

class Generator {
    private val functions = mutableMapOf<String, MutableList<FunctionDecl>>()
    private val typesAliases = mutableMapOf<String, String>()
    private var assertionId = 0
    private val statesMap = mutableMapOf<String, Int>()
    val errorIdMap = mutableMapOf<String, String>()

    private val shiftsObjectName = "SPIDER\$SHIFTS"
    private val kexIntrinsicsClassName = ClassName.get("org.jetbrains.research.kex", "Intrinsics")

    fun generateCode(library: LibraryDecl): Map<FileDescriptor, String> {
        for (function in library.functions) {
            val klass = function.entity.type.typeName
            if (functions[klass] == null) {
                functions[klass] = mutableListOf()
            }
            functions[klass]!!.add(function)
        }

        for (type in library.types) {
            typesAliases[type.semanticType.typeName] = type.codeType.typeName
        }

        initStatesMap(library)

        val result = mutableMapOf<FileDescriptor, String>()

        for (automaton in library.automata) {
            val javaPackage = automaton.javaPackage.name
            val packageLikeFilePath = javaPackage.replace(".", "/") + "/"
            val fileDescriptor = FileDescriptor(
                path = packageLikeFilePath,
                nameWithoutExtension = automaton.name.typeName,
                "java"
            )
            result[fileDescriptor] = generateAutomaton(automaton)
        }

        return result
    }

    private fun initStatesMap(library: LibraryDecl) {
        var i = 0
        for (automaton in library.automata) {
            automaton.states.forEach { state ->
                statesMap[state.name.getStateName(automaton)] = ++i
            }
        }
    }

    private fun generateAutomaton(automaton: Automaton): String {
        return buildJavaFile(automaton.javaPackage.name) {
            indentSize = 4
            addClass(automaton.name.typeName) {
                val variables = automaton.statements.filterIsInstance<AutomatonVariableStatement>()

                for ((stateIndex, state) in automaton.states.withIndex()) {
                    // state.withoutDoubleDollar is used due strange behaviour of javapoet-ktx
                    fields.add(
                        TypeName.INT,
                        state.name.getStateName(automaton).withoutDoubleDollar,
                        Modifier.FINAL,
                        Modifier.PRIVATE
                    ) {
                        initializer((stateIndex).toString())
                    }
                }

                variables.forEach { variable ->
                    fields.add(ClassName.get("", variable.type), variable.name)
                }

                fields.add(ClassName.INT, "STATE") {
                    addModifiers(Modifier.PUBLIC)
                    initializer(getAutomatonDefaultState(automaton))
                }

                for (method in functions[automaton.name.typeName].orEmpty()) {
                    if (method.name == automaton.name.toString()) {
                        continue
                    }
                    methods.add(method.name) {
                        val returnTypeName = typesAliases[method.returnValue?.type?.typeName]
                        returns = if (returnTypeName != null) {
                            ClassName.get("", returnTypeName)
                        } else {
                            TypeName.VOID
                        }

                        parameters {
                            method.args.forEach { arg ->
                                val argType = ClassName.get("", typesAliases[arg.type.typeName])
                                this.add(argType, arg.name)
                            }
                        }

                        val shiftsMap = automaton.shifts.filter { it.functions.contains(method.name) }.map { it.from to it.to }
                        if (shiftsMap.isNotEmpty()) {

                            val (firstFlowFrom, firstFlowTo) = shiftsMap.first()
                            append("if (STATE == ${firstFlowFrom.getStateName(automaton)}) {\n")
                            if (firstFlowTo != "self") {
                                append("    STATE = ${firstFlowTo.getStateName(automaton)};\n")
                            }
                            append("}")

                            for ((from, to) in shiftsMap.subList(1, shiftsMap.size)) {
                                append(" else if (STATE == ${from.getStateName(automaton)}) {\n")
                                if (to != "self") {
                                    append("    STATE = ${to.getStateName(automaton)};\n")
                                }
                                append("}")
                            }

                            val errorMessage = "Invalid shift by calling method `${method.name}`"
                            val errorId = "id${assertionId++}"
                            errorIdMap[errorId] = errorMessage

                            append(" else {\n")
                            append("    %T.kexAssert(\"$errorId\", false);\n", kexIntrinsicsClassName)
                            append("}\n")
                        }

                        method.variableAssignments.forEach { assignment ->
                            appendLine("${assignment.name} = new ${assignment.calleeAutomatonName}()")
                            val foreignAutomatonNewState = assignment.calleeArguments.firstOrNull()
                            if (foreignAutomatonNewState != null) {
                                val targetStateConstValue = statesMap[foreignAutomatonNewState.getStateName(assignment.calleeAutomatonName)]

                                appendLine("${assignment.name}.STATE = $targetStateConstValue")
                            }
                        }

                        if (method.returnValue != null) {
                            appendLine("return org.jetbrains.research.kex.Objects.kexUnknown<$returnTypeName>()")
                        }

                    }
                }
            }
        }.toString()
    }

    private fun getAutomatonDefaultState(automaton: Automaton): String {
        val func = functions
            .entries
            .firstOrNull { it.key == automaton.name.toString() }
            ?.value
            ?.firstOrNull { it.name == automaton.name.toString() }
            ?: throw UnknownStateException("Constructor function not found for automaton ${automaton.name}")

        val rawStateName = func
            .variableAssignments
            .firstOrNull { it.name == "result" }
            ?.calleeArguments
            ?.firstOrNull()
            ?: throw SemanticException("Constructor function for automaton ${automaton.name} hasn't found")

        return rawStateName.getStateName(automaton)
    }

    private fun String.getStateName(automaton: Automaton): String = this.getStateName(automaton.name.toString())

    private fun String.getStateName(automatonName: String) =
        "STATE\$CONST\$${automatonName}\$${toUpperCase()}".replace("$", "$$")

    private fun getAutomatonStateName(name: String) = "STATE\$\$${name.toUpperCase()}"

    private fun FunctionDecl.getTransitionFunctionName(automaton: Automaton): String {
        return "transition${automaton.name.toString().capitalize()}Call${name.capitalize()}"
    }

    private val String.withoutDoubleDollar: String
        get() = this.replace("\$\$", "\$")
}