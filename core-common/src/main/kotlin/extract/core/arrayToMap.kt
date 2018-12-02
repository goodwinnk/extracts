package extract.core

fun arrayToMap(values: Array<String>): Map<String, String> {
    require(values.size % 2 == 0)

    val result = HashMap<String, String>()
    for (i in 0 until (values.size / 2)) {
        val from = values[i * 2]
        val to = values[i * 2 + 1]
        result[from] = to
    }

    return result
}