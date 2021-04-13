data class Defect (
    val location: Location,
    val testCaseName: String,
    val testFile: String,
    val type: String
)

data class Location(
    val file: String,
    val isKnown: String,
    val line: Int,
    val `package`: Package
)

data class Package(
    val isConcrete: Boolean,
    val name: String
)