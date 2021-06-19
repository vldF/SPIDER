package ru.vldf.spider.configs.reader

import java.io.File
import java.util.*
import kotlin.reflect.KProperty

abstract class AbstractConfigValue <T> {
    private val configFilePath = "./config.properties"
    private val configFile = File(configFilePath)
    private val configProperties = Properties()

    init {
        if (!configFile.exists()) {
            val defaultConfig = javaClass.getResource("/$configFilePath")!!.readText()
            if (!configFile.createNewFile()) {
                throw IllegalStateException("Can not create config.properties")
            }
            configFile.writeText(defaultConfig)
        }
        configProperties.load(configFile.inputStream())
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        val name = property.name
        val value = configProperties[name]

        if (value == null) {
            configFile.writeText("$name = \n")
            throw IllegalArgumentException("config value with name $name not found; new one was created")
        }
        return parseValue(value.toString())
    }

    abstract fun parseValue(value: String): T
}