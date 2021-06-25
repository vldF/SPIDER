package ru.vldf.spider.programrunners

import ru.vldf.spider.configs.javaPath
import java.io.File

class KexRunner(
    override val workingDir: File,
    private val kexPath: String,
    private val classPath: String,
    private val clientTarget: String,
    private val libraryTarget: String,
    private val tmpDir: File
) : ProgramsRunner(javaPath + "java") {
    override fun beforeRun() {
        addArgs(
            "-Xmx16384m",
            "-Djava.security.manager",
            "-Djava.security.policy==kex.policy",
            "-jar",
            kexPath,
            "--output",
            "kex-instrumented",
            "--mode",
            "libchecker",
            "--log",
            "kex.log",
            "--classpath",
            classPath,
            "--libCheck",
            clientTarget,
            "--option",
            "defect:outputFile:${tmpDir.absolutePath}/defects.json",
            "--target",
            libraryTarget,
            "--config",
            "/home/vldf/IdeaProjects/kex/kex.ini"
        )
    }
}