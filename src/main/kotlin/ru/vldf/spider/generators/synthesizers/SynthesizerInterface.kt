package ru.vldf.spider.generators.synthesizers

import ru.spbstu.insys.libsl.parser.LibraryDecl
import ru.vldf.spider.generators.SynthContext

interface SynthesizerInterface {
    fun generate(ctx: SynthContext, library: LibraryDecl)
}