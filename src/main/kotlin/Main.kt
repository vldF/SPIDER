import generators.Generator
import ru.spbstu.insys.libsl.parser.ModelParser
import java.io.File

fun main() {
    val parser = ModelParser()
    val stream = File("./testLibrary/src/resources/model/Computer.lsl").inputStream()
    val parsed = parser.parse(stream)

    println(Generator().generateCode(parsed))
}
