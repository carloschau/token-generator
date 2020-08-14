package carloschau.tokengenerator.util

object CommonUtil {
    fun tokenizeString(text: String) : Set<String> {
        return "\\{.*?}".toRegex().findAll(text).map {
            it.value.trimStart('{').trimEnd('}')
        }.toSet()
    }
}