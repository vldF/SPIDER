package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

class TypeAliasesInitializerSynth : SynthesizerInterface {
    override fun generate(ctx: SynthContext, library: LibraryDecl) {
        for (type in library.types) {
            ctx.typesAliases[type.semanticType.typeName] = type.codeType.typeName
        }
    }
}