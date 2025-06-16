package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("episodedetails", "", "")
data class EpisodeNfo(
    val title: Title,
    val episode: Episode,
    val aired: Aired?,
    val season: Season,
    val plot: Plot?,
) {
    @Serializable
    @XmlSerialName("title", "", "")
    data class Title(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("episode", "", "")
    data class Episode(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("aired", "", "")
    data class Aired(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("season", "", "")
    data class Season(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("plot", "", "")
    data class Plot(@XmlValue(true) val value: String = "")
}
