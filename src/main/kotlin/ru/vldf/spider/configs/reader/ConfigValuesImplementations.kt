package ru.vldf.spider.configs.reader

import java.io.File

class LongConfig : AbstractConfigValue<Long>() {
    override fun parseValue(value: String): Long {
        return value.toLong()
    }
}

class IntConfig : AbstractConfigValue<Int>() {
    override fun parseValue(value: String): Int {
        return value.toInt()
    }
}

class StringConfig : AbstractConfigValue<String>() {
    override fun parseValue(value: String): String {
        return value
    }
}

class BooleanConfig : AbstractConfigValue<Boolean>() {
    override fun parseValue(value: String): Boolean {
        return value.toBoolean()
    }
}

class FileConfig : AbstractConfigValue<File>() {
    override fun parseValue(value: String): File {
        return File(value)
    }
}
