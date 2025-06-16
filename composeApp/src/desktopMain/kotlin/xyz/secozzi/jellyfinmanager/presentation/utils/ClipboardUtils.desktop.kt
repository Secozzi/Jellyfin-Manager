package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual fun clipEntryOf(string: String): ClipEntry {
    return ClipEntry(StringSelection(string))
}
