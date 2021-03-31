package ru.vldf.testlibrary

class Computer {
    private var memory: Memory? = null
    private var isOsLoad = false

    fun boot() {
        memory = Memory()
    }

    fun selectOS(osName: String) {
        if (memory == null) {
            throw IllegalAccessException("Os wasn't loaded")
        }
        when (osName) {
            "win" -> memory!!.setOS(OS.WIN)
            "linux" -> memory!!.setOS(OS.LINUX)
            else -> throw IllegalArgumentException("Invalid OS name")
        }
    }

    fun loadOS() {
        isOsLoad = true
    }

    fun shutdown() {
        memory = null
        isOsLoad = false
    }
}