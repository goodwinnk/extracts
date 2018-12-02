package extract.core

internal class DirsForEngine(dirs: Dirs) {
    val skip: Set<String> = dirs.skip.toSet()
    val drop: Set<String> = dirs.drop.toSet()
    val terminate: Set<String> = dirs.terminate.filter { !it.endsWith('/') }.toSet()
    val prefixTerminate: List<String> =
            dirs.terminate.map {
                if (it.endsWith('/')) {
                    it
                } else {
                    "$it/"
                }
            }

    val upperCase: Set<String> = dirs.upperCase.toSet()

    val rename: Map<String, String> = arrayToMap(dirs.rename)

    val dirsToLabelCache = HashMap<String, String>()
}

internal fun dynamicFileLabels(commitInfo: CommitInfo, dirs: DirsForEngine): List<ExtractLabel> {
    val result = ArrayList<ExtractLabel>()

    for (fileAction in commitInfo.fileActions) {
        val labelNamesWithIndex = assignLabels(fileAction.path, dirs)

        val labels = labelNamesWithIndex.map { (labelName, index) ->
            ExtractLabel(
                    labelName,
                    text = null, icon = null, hint = null, url = null, style = null,
                    badges = arrayOf(index.toString()),
                    labelName = null
            )
        }

        result.addAll(labels)
    }

    return result
            .sortedBy { it.badges.getOrNull(0)?.toInt() ?: 0 }
            .distinctBy { it.name }
}

internal fun assignLabels(path: String, dirs: DirsForEngine): ArrayList<Pair<String, Int>> {
    val terminatedWithPrefix = dirs.prefixTerminate.find { prefix ->
        path.startsWith(prefix)
    }

    val withoutLastPart = (terminatedWithPrefix?.removeSuffix("/") ?: path)
            .split('/')
            .dropLast(1) // Last terminated or file

    val dirsPathIndexed: List<Pair<String, Int>> =
            withoutLastPart
            .takeWhile { it !in dirs.terminate }
            .mapIndexed { index, dirName -> dirName to index }
            .filter { (dirName, _) -> dirName !in dirs.skip }
            .toList()

    val labelsIndexed: List<Pair<String, Int>> = dirsPathIndexed.mapTo(ArrayList()) { (dirName, index) ->
        toLabelName(dirName, dirs) to index
    }

    val words = HashSet<String>()
    val sameWordsFilteredIndexed = ArrayList<Pair<String, Int>>()

    for (indexLabel in labelsIndexed) {
        val (labelName, _) = indexLabel
        val labelWords = labelName.split(" ")
        if (labelWords.all { word -> word in words }) {
            continue
        }

        sameWordsFilteredIndexed.add(indexLabel)

        words.addAll(labelWords)
    }

    return sameWordsFilteredIndexed
}

private val DELIMITER_CHARS = charArrayOf('.', '_', '-')

internal fun toLabelName(dirName: String, dirs: DirsForEngine): String {
    val labelFromCache = dirs.dirsToLabelCache[dirName]
    if (labelFromCache != null) {
        return labelFromCache
    }

    val remapped = dirs.rename[dirName]
    if (remapped != null) {
        dirs.dirsToLabelCache[dirName] = remapped
        return remapped
    }

    val dirWords = dirName
            .replace(DELIMITER_CHARS, ' ', 1)
            .splitToSequence(' ')
            .flatMap {
                it.toWords().asSequence()
            }

    return dirWords
            .map {
                if (it in dirs.upperCase) {
                    it.toUpperCase()
                } else {
                    it.capitalize()
                }
            }
            .filter {
                it !in dirs.drop
            }
            .joinToString(separator = " ")
            .also { label ->
                dirs.dirsToLabelCache[dirName] = label
            }
}

fun String.replace(oldChars: CharArray, newChar: Char, from: Int): String {
    if (length <= from) return this

    val replaceAt = ArrayList<Int>(0)
    for (i in from until length) {
        val ch = get(i)
        val contains = ch in oldChars
        if (contains) {
            replaceAt.add(i)
        }
    }

    if (replaceAt.isEmpty()) return this

    val replaceWith = newChar.toString()
    var newStr = this
    for (i in replaceAt) {
        newStr = newStr.replaceRange(i, i + 1, replaceWith)
    }

    return newStr
}