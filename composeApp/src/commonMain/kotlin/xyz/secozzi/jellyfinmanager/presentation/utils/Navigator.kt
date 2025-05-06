package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.eygraber.uri.UriCodec
import kotlinx.serialization.json.Json

@Suppress("CompositionLocalAllowlist")
val LocalNavController = compositionLocalOf<NavHostController> {
    error("LocalNavController not initialized!")
}

inline fun <reified T> serializableType(
    json: Json = Json,
) = object : NavType<T?>(isNullableAllowed = true) {
    override fun get(bundle: SavedState, key: String): T? {
        return bundle.read {
            getStringOrNull(key)?.toDecodedUrl()?.let<String, T?>(::parseValue)
        }
    }

    override fun parseValue(value: String): T? {
        if (value == "null") return null
        return json.decodeFromString<T>(value.toDecodedUrl())
    }

    override fun serializeAsValue(value: T?): String {
        return value?.let {
            json.encodeToString(value).toEncodedUrl(allow = null)
        } ?: "null"
    }

    override fun put(bundle: SavedState, key: String, value: T?) {
        bundle.write { putString(key, serializeAsValue(value)) }
    }
}

fun String.toEncodedUrl(allow: String? = ":/-![].,%?&="): String {
    return UriCodec.encode(this, allow)
}

fun String.toDecodedUrl(): String {
    return UriCodec.decode(this)
}
