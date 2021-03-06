package ru.vldf.spider.programrunners

import java.io.File
import java.io.FileNotFoundException

open class ProgramsRunner(programPath: String) {
    private val args = mutableListOf<String>()
    private lateinit var process: Process
    protected open val workingDir: File? = null

    init {
        if (!File(programPath).exists()) {
            throw FileNotFoundException("File $programPath not found")
        }

        args.add(programPath)
    }

    fun addArg(key: String, value: String): ProgramsRunner {
        args.add(key)
        args.add(value)

        return this
    }

    fun addArg(arg: String): ProgramsRunner {
        args.add(arg)

        return this
    }

    fun addArgs(vararg newArgs: Pair<String, String>): ProgramsRunner {
        newArgs.forEach { args.add(it.first); args.add(it.second) }

        return this
    }

    fun addArgs(vararg newArgs: String): ProgramsRunner {
        args.addAll(newArgs)

        return this
    }

    open fun beforeRun() {}

    fun runAndWait(): Boolean {
        beforeRun()
        process = ProcessBuilder(args)
            .directory(workingDir)
            .inheritIO()
            .start()
        process.waitFor()

        return process.exitValue() == 0
    }
}