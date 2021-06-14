package generators

import exceptions.UnknownTerm
import ru.spbstu.insys.libsl.parser.*
import ru.spbstu.insys.libsl.parser.ArithmeticTermType.*

fun Term.toJava(): String {
    return when (this) {
        is VariableTerm -> name
        is Const -> value.toString()
        is AndAndTerm -> "${left.toJava()} && ${right.toJava()}"
        is OrOrTerm -> "${left.toJava()} || ${right.toJava()}"
        is ArithmeticTerm -> "(${left.toJava()} ${type.text} ${right.toJava()})"
        is InversionTerm -> "!${term.toJava()}"
        else -> throw UnknownTerm("$this")
    }
}

private val ArithmeticTermType.text: String
    get() = when (this) {
        GT -> ">"
        GT_EQ -> ">="
        LT -> "<"
        LT_EQ -> "<="
        EQ_EQ -> "=="
        NOT_EQ -> "!="
    }