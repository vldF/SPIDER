data class Defect (
    val callStack: Array<String>,
    val id: String,
    val testCaseName: String?,
    val testFile: String?,
    val type: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Defect

        if (!callStack.contentEquals(other.callStack)) return false
        if (id != other.id) return false
        if (testCaseName != other.testCaseName) return false
        if (testFile != other.testFile) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = callStack.contentHashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + (testCaseName?.hashCode() ?: 0)
        result = 31 * result + (testFile?.hashCode() ?: 0)
        result = 31 * result + type.hashCode()
        return result
    }
}