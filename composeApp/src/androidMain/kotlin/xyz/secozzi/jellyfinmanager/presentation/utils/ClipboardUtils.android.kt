package xyz.secozzi.jellyfinmanager.presentation.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(string: String): ClipEntry {
    return ClipEntry(ClipData.newPlainText("path", string))
}
