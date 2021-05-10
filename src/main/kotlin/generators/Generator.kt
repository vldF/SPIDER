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
    private val spiderClassName = ClassName.get("spider", "SPIDER\$SHIFTS")

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

        val allStates = library.automata.flatMap { automaton ->
            automaton.states.map { state ->
                state.name.getStateName(automaton)
            }
        }

        val result = mutableMapOf<FileDescriptor, String>()
        val shiftsFileDescriptor = FileDescriptor(
            path = "spider/",
            nameWithoutExtension = shiftsObjectName,
            extension = "java"
        )
        result[shiftsFileDescriptor] = generateShiftsObject(library.automata, allStates)

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

    /*
        package spider;

        import org.jetbrains.research.kex.Intrinsics;

        public class SPIDER$SHIFTS {
            private final int STATE$CONST$Automaton1$State1 = 0;

            private final int STATE$CONST$Automaton1$State2 = 1;

            private final int STATE$CONST$Automaton2$State1 = 2;

            private final int STATE$CONST$Automaton2$State2 = 3;

            ...

            public int STATE$AUTOMATON1 = STATE$CONST$Automaton1$State1;

            public int STATE$AUTOMATON2 = STATE$CONST$Automaton2$State1;

            TRANSITION_FUNCTIONS
        }
     */

    @Suppress("NAME_SHADOWING")
    private fun generateShiftsObject(automata: List<Automaton>, allStates: List<String>): String {
        return buildJavaFile("spider") {
            indentSize = 4
            addClass(shiftsObjectName) {
                addModifiers(Modifier.PUBLIC)
                for ((stateIndex, state) in allStates.withIndex()) {
                    // state.withoutDoubleDollar is used due strange behaviour of javapoet-ktx
                    fields.add(TypeName.INT, state.withoutDoubleDollar, Modifier.FINAL,Modifier.PRIVATE) {
                        initializer((stateIndex).toString())
                    }
                    statesMap[state] = stateIndex
                }

                for (automaton in automata) {
                    val stateFieldName = getAutomatonStateName(automaton.name.toString())
                    fields.add(TypeName.INT, stateFieldName.withoutDoubleDollar, Modifier.PUBLIC) {
                        val defaultStateName = getAutomatonDefaultState(automaton)
                        initializer(defaultStateName)
                    }
                    
                    for (method in functions[automaton.name.typeName].orEmpty()) {
                        if (method.name == automaton.name.toString()) {
                            continue
                        }
                        val methodName = method.getTransitionFunctionName(automaton)

                        methods.add(methodName) {
                            addModifiers(Modifier.PUBLIC)
                            returns = TypeName.VOID

                            val shiftsMap = automaton.shifts.filter { it.functions.contains(method.name) }.map { it.from to it.to }
                            if (shiftsMap.isNotEmpty()) {

                                val (from, to) = shiftsMap.first()
                                append("if ($stateFieldName == ${from.getStateName(automaton)}) {\n")
                                if (to != "self") {
                                    append("    $stateFieldName = ${to.getStateName(automaton)};\n")
                                }
                                append("}")

                                for ((from, to) in shiftsMap.subList(1, shiftsMap.size)) {
                                    append(" else if ($stateFieldName == ${from.getStateName(automaton)}) {\n")
                                    append("    $stateFieldName = ${to.getStateName(automaton)};\n")
                                    append("}")
                                }

                                val errorMessage = "Invalid shift by calling method `${method.name}`"
                                val errorId = "id${assertionId++}"
                                errorIdMap[errorId] = errorMessage

                                append(" else {\n")
                                append("    %T.kexAssert(\"$errorId\", false);\n", kexIntrinsicsClassName)
                                append("}\n")
                            }
                        }
                    }
                }
            }
        }.toString()
    }

    /*
        package ru.vldf.testlibrary;

        import spider.SPIDER$SHIFTS;

        class Automation {
            SPIDER$SHIFTS SHIFTS_MANAGER = new spider.SPIDER$SHIFTS();

            PROPERTIES

            FUNCTIONS
     */
    private fun generateAutomaton(automaton: Automaton): String {
        return buildJavaFile(automaton.javaPackage.name) {
            indentSize = 4
            addClass(automaton.name.typeName) {
                val variables = automaton.statements.filterIsInstance<AutomatonVariableStatement>()

                fields.add(spiderClassName, "SHIFTS_MANAGER") {
                    initializer("new spider.SPIDER\$\$SHIFTS()")
                }

                variables.forEach { variable ->
                    fields.add(ClassName.get("", variable.type), variable.name)
                }

                for (method in functions[automaton.name.typeName].orEmpty()) {
                    if (method.name == automaton.name.toString()) {
                        continue
                    }
                    methods.add(method.name) {
                        val returnTypeName = method.returnValue?.type?.typeName
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

                        val transitionFunctionName = method.getTransitionFunctionName(automaton)
                        appendLine("SHIFTS_MANAGER.$transitionFunctionName()")
                        method.variableAssignments.forEach { assignment ->
                            appendLine("${assignment.name} = new ${assignment.calleeAutomatonName}()")
                            val foreignAutomatonNewState = assignment.calleeArguments.firstOrNull()
                            if (foreignAutomatonNewState != null) {
                                val newStateName = getAutomatonStateName(assignment.calleeAutomatonName)

                                val targetStateConstValue = statesMap[foreignAutomatonNewState.getStateName(assignment.calleeAutomatonName)]

                                appendLine("${assignment.name}.SHIFTS_MANAGER.$newStateName = $targetStateConstValue")
                            }
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