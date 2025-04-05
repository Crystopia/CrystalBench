package net.crystopia.crystalbench.utils

object ResourcePackVersion {

    fun getPackFormat(version: String): Int {
        val versionMap = mapOf(
            "1.21.5" to 71,
            "1.21.4" to 61,
            "1.21.3" to 48,
            "1.21.2" to 48,
            "1.20.6" to 41,
            "1.20.5" to 41,
            "1.20.3" to 26,
            "1.20.2" to 18,
            "1.20" to 15,
        )

        return when {
            versionMap.containsKey(version) -> versionMap[version]!!
            else -> -1
        }
    }
}