package xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("title", "", "")
data class Title(@XmlValue(true) val value: String = "")

@Serializable
@XmlSerialName("plot", "", "")
data class Plot(@XmlValue(true) val value: String = "")

@Serializable
@XmlSerialName("genre", "", "")
data class Genre(@XmlValue(true) val value: String = "")

@Serializable
@XmlSerialName("studio", "", "")
data class Studio(@XmlValue(true) val value: String = "")

@Serializable
sealed interface JellyfinCommonInfo {
    val title: Title
    val plot: Plot
    val genre: Genre
    val studio: List<Studio>
}
