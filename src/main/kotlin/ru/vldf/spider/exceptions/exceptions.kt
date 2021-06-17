package ru.vldf.spider.exceptions

open class SemanticException(message: String) : Exception(message)

open class UnknownStateException(message: String) : SemanticException(message)

class UnknownAutomatonName(message: String) : SemanticException(message)

class UnknownTerm(message: String) : SemanticException(message)