package xyz.secozzi.jellyfinmanager.utils

fun getDirs(lines: String): List<String> {
    if (lines.startsWith("/usr/bin/ls: cannot access")) {
        throw IllegalArgumentException(lines)
    }

    return lines.lines().map {
        it.removePrefix("'").removeSuffix("'")
    }
}
