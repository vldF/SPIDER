package generators

import ru.spbstu.insys.libsl.parser.*

class Generator {
    private val functions = mutableMapOf<String, MutableList<FunctionDecl>>()
    private val types = mutableMapOf<String, String>()

    fun generateCode(library: LibraryDecl): Map<String, String> {
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
        
        val result = mutableMapOf<String, String>()
            for (automaton in library.automata) {
                val javaPackage = automaton.javaPackage.name
                val packageLikeFilePath = javaPackage.replace(".", "/")
                val fileName = "$packageLikeFilePath/${automaton.name}"
                result[fileName] = generateAutomaton(automaton)
            }

        return result
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

    private fun generateAutomaton(automaton: Automaton): String {
        // TODO: add State -> self();
        return buildString {
            val javaPackageName = automaton.javaPackage.name
            val subAutomatons = automaton.statements.filterIsInstance<AutomatonVariableStatement>()

            appendLine("package $javaPackageName;")
            appendLine()
            appendLine("import org.jetbrains.research.kex.Intrinsics;")
            appendLine()
            appendLine("public class ${automaton.name} {")
            subAutomatons.forEach { variable ->
                appendLineWithIndent(
                    "private ${variable.type.toSemanticType.removePrefix("$javaPackageName.")} ${variable.name.automatonVariableName};",
                    indent = 4
                )
            }
            appendLineWithIndent("int state = 0;", indent = 4)
            appendLine()
            automaton.states.withIndex().forEach { (index, state) ->
                appendLineWithIndent("final static int ${state.name.stateName} = $index;", indent = 4)
            }
            appendLine()
            functions[automaton.name.typeName]?.forEach { function ->
                appendMultilineTextWithIntent(generateFunction(function, automaton.shifts), indent = 4)
            }
            appendLine("}")
        }
    }

    /*
    TYPE NAME(ARGS) {
        if (STATE==STATE0) {
            STATE = NEW_STATE
        } else if ...

        } else {
            throw IllegalStateException()
        }
    ]
     */
    private fun generateFunction(function: FunctionDecl, allShifts: List<ShiftDecl>): String {
        val semanticType = function.returnValue
        val type: String = semanticType?.type.resolvedSemanticType

        val name = function.name
        val args = function.args.joinToString(",") { arg -> "${arg.type.resolvedSemanticType} ${arg.name}" }
        val shifts = allShifts.filter { it.functions.contains(name) }
        val assignments = function.variableAssignments

        return buildString {
            appendLine("$type $name($args) {")

            for (assignment in assignments) {
                appendLineWithIndent(
                    "${assignment.name.automatonVariableName} = new ${assignment.calleeAutomatonName}();",
                    indent = 4
                )
                appendLineWithIndent(
                    "${assignment.name.automatonVariableName}.state = ${assignment.name.automatonVariableName}.${assignment.calleeArguments.first().stateName};",
                    indent = 4
                )
            }

            if (shifts.isNotEmpty()) {
                append("    ")
                for (shift in shifts) {
                    var toName = shift.to.stateName
                    if (toName == "STATE\$SELF") {
                        toName = shift.from.stateName
                    }
                    appendLine("if (state == ${shift.from.stateName}) {")
                    appendLineWithIndent("state = $toName;", indent = 8)
                    append("    } else ")
                }
                appendLine("{")
                appendLineWithIndent("Intrinsics.kexAssert(false);", indent = 8)
                appendLineWithIndent("}", indent = 4)
            }
            appendLine("}")
        }
    }

    private fun StringBuilder.appendLineWithIndent(value: String, indent: Int) {
        append(" ".repeat(indent))
        append(value)
        appendLine()
    }

    private fun StringBuilder.appendMultilineTextWithIntent(value: String, indent: Int) {
        append(addIndent(value, indent))
    }

    private fun addIndent(value: String, n: Int): String {
        return buildString {
            for (line in value.lines()) {
                if (line.isNotBlank()) append(" ".repeat(n)) // we don't would to add intent for empty lines
                appendLine(line)
            }
        }
    }

    private val String.stateName
        get () = "STATE\$${toUpperCase()}"

    private val String.automatonVariableName
        get() = "VARIABLE\$$this"

    private val String?.toSemanticType
        get() = if (this == null){
            "void"
        } else{
            types[this] ?: throw IllegalStateException("wrong type: $this")
        }

    private val SemanticType?.resolvedSemanticType: String
        get() = if (this == null){
            "void"
        } else{
            types[this.typeName] ?: throw IllegalStateException("wrong type: ${this.typeName}")
        }
}