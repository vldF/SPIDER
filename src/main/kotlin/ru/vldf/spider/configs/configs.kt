package ru.vldf.spider.configs

import ru.vldf.spider.configs.reader.BooleanConfig
import ru.vldf.spider.configs.reader.FileConfig
import ru.vldf.spider.configs.reader.StringConfig

val targetDir by FileConfig()
val tmpDir by FileConfig()
val javaPath by StringConfig()
val kexIntrinsicsJarPath by StringConfig()
val kexJarPath by StringConfig()
val kexBaseDir by StringConfig()
val checkFinishstates by BooleanConfig()