package generators

import com.hendraanggrian.javapoet.buildJavaFile
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import generators.descriptors.FileDescriptor
import ru.spbstu.insys.libsl.parser.Automaton
import ru.spbstu.insys.libsl.parser.AutomatonVariableStatement
import ru.spbstu.insys.libsl.parser.FunctionDecl
import ru.spbstu.insys.libsl.parser.LibraryDecl
import javax.lang.model.element.Modifier
import kotlin.collections.set

class Generator {
    private val functions = mutableMapOf<String, MutableList<FunctionDecl>>()
    private val types = mutableMapOf<String, String>()
    private var assertionId = 0
    private val statesMap = mutableMapOf<String, Int>()
    val errorIdMap = mutableMapOf<String, String>()

    fun generateCode(library: LibraryDecl): Map<FileDescriptor, String> {
        for (function in library.functions) {
            val klass = function.entity.type.typeName
            if (functions[klass] == null) {
                functions[klass] = mutableListOf()
            }
            functions[klass]!!.add(function)
        }

        for (type in library.types) {
            types[type.semanticType.typeName] = type.codeType.typeName
        }

        val allStates = getAllStates(library)

        val result = mutableMapOf<FileDescriptor, String>()
        val shiftsObjectName = "SPIDER\$SHIFTS"
        val shiftsFileDescriptor = FileDescriptor(
            path = "",
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
            result[fileDescriptor] = generateAutomaton(automaton, library)
        }


        return result
    }

    private fun getAllStates(library: LibraryDecl) = library.automata.flatMap { automaton ->
        automaton.states.map {
                state -> state.name.getStateName(automaton)
        }
    }

    @Suppress("NAME_SHADOWING")
    private fun generateShiftsObject(automatons: List<Automaton>, allStates: List<String>): String {
        return buildJavaFile("") {
            indentSize = 4
            addClass("SPIDER\$SHIFTS") {
                for ((stateIndex, state) in allStates.withIndex()) {
                    fields.add(TypeName.INT, state, Modifier.FINAL,Modifier.PRIVATE) {
                        initializer((stateIndex).toString())
                    }
                    statesMap[state] = stateIndex
                }

                addModifiers(Modifier.PUBLIC)
                for (automaton in automatons) {
                    val stateFieldName = getAutomatonStateName(automaton.name.toString())
                    fields.add(TypeName.INT, stateFieldName, Modifier.PUBLIC) {
                        val defaultStateName = automaton.states.firstOrNull()?.name?.getStateName(automaton) ?: "0"
                        initializer(defaultStateName)
                    }
                    
                    for (method in functions[automaton.name.typeName].orEmpty()) {
                        val methodName = method.getTransitionFunctionName(automaton)

                        methods.add(methodName) {
                            returns = TypeName.VOID

                            val shiftsMap = automaton.shifts.filter { it.functions.contains(method.name) }.map { it.from to it.to }
                            if (shiftsMap.isNotEmpty()) {

                                val (from, to) = shiftsMap.first()
                                append("if ($stateFieldName == ${from.getStateName(automaton)})\n")
                                append("    $stateFieldName = ${to.getStateName(automaton)};\n")
                                append("}")

                                for ((from, to) in shiftsMap.subList(1, shiftsMap.size)) {
                                    append(" else if ($stateFieldName == ${from.getStateName(automaton)})\n")
                                    append("    $stateFieldName = ${to.getStateName(automaton)};\n")
                                    append("}")
                                }

                                val errorMessage = "Invalid shift by calling method `${method.name}`"
                                val errorId = "id${assertionId++}"
                                errorIdMap[errorId] = errorMessage

                                append(" else {\n")
                                append("    org.jetbrains.research.kex.Intrinsics.kexAssert(\"$errorId\", false);\n")
                                append("}\n")
                            }
                        }
                    }
                }
            }
        }.toString()
    }

    private fun FunctionDecl.getTransitionFunctionName(automaton: Automaton): String {
        return "transition${automaton.name.toString().capitalize()}Call${name.capitalize()}"
    }


    /*
        IMPORTS

        class NAME {
            final static int STATE0 = 0;
            final static int STATE1 = 1;
            ...

            %functions%
        }
     */
    private fun generateAutomaton(automaton: Automaton, library: LibraryDecl): String {
        return buildJavaFile(automaton.javaPackage.name) {
            indentSize = 4
            addClass(automaton.name.typeName) {
                val variables = automaton.statements.filterIsInstance<AutomatonVariableStatement>()

                fields.add(ClassName.get("", "SPIDER\$SHIFTS"), "SHIFTS_MANAGER") {
                    initializer("new SPIDER\$\$SHIFTS()")
                }

                variables.forEach { variable ->
                    fields.add(ClassName.get("", variable.type), variable.name)
                }

                functions[automaton.name.typeName]?.forEach { method ->
                    methods.add(method.name) {
                        val returnTypeName = method.returnValue?.type?.typeName
                        returns = if (returnTypeName != null) {
                            ClassName.get("", returnTypeName)
                        } else {
                            TypeName.VOID
                        }

                        parameters {
                            method.args.forEach { arg ->
                                val argType = ClassName.get("", arg.type.typeName)
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

    private fun String.getStateName(automaton: Automaton): String = this.getStateName(automaton.name.toString())

    private fun String.getStateName(automatonName: String) =
        "STATE\$CONST\$${automatonName}\$${toUpperCase()}".replace("$", "$$")

    private fun getAutomatonStateName(name: String) = "STATE\$\$${name.toUpperCase()}"
}