package exceptions

open class SemanticException(message: String) : Exception(message)

open class UnknownStateException(message: String) : SemanticException(message)