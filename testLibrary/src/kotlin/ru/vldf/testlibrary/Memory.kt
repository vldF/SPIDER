package ru.vldf.testlibrary

class Memory {
    private var currentOS: OS = OS.NONE
    private var isOpen = false

    fun open() {
        isOpen = true
    }

    fun setOS(os: OS) {
        currentOS = os
    }

    fun close() {
        isOpen = false
    }

}