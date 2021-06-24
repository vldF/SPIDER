package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class RequiresSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for ((function, javaMethod) in ctx.functionToJavaMethod) {
            val requires = function.contracts.requires
            if (requires != null) {
                javaMethod.addKexAssert(
                    "`requires` contract $requires failed for function ${function.name}",
                    requires,
                    ctx
                )
            }
        }
    }
}