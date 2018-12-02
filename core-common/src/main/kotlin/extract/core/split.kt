package extract.core

fun String.removeCamelCase(): String {
    return toWords().joinToString(separator = " ")
}

fun String.toWords(): List<String> {
    if (!needSplit()) return listOf(this)

    val words = ArrayList<String>()

    var from = 0
    var lastUpperCase = if (this[0].isUpperCase()) 0 else -1

    for (index in 1 until length) {
        if (this[index].isUpperCase()) {
            if (lastUpperCase != index - 1) {
                words.add(substring(from, index))

                from = index
            }

            lastUpperCase = index
        }
    }

    if (from != -1) {
        // Finish last word
        words.add(substring(from, length))
    }

    return words
}

private fun String.needSplit(): Boolean {
    for (index in 1 until length) {
        if (this[index].isUpperCase()) {
            return true
        }
    }
    return false
}

fun Char.isUpperCase() = this.toLowerCase() != this