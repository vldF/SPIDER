package generators

import ru.spbstu.insys.libsl.parser.*

fun Term.toJava(): String {
    return when (this) {
        is VariableTerm -> name
        is AndAndTerm -> "${left.toJava()} && ${right.toJava()}"
        is OrOrTerm -> "${left.toJava()} || ${right.toJava()}"
        is ArithmeticTerm -> "(${left.toJava()} ${type.text} ${right.toJava()})"
        is InversionTerm -> "!${term.toJava()}"
        is Literal -> text
    }
}
