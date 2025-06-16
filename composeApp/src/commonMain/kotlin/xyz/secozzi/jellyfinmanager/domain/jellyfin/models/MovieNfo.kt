package xyz.secozzi.jellyfinmanager.domain.jellyfin.models

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("movie", "", "")
data class MovieNfo(
    val title: Title,
    val premiered: Premiered?,
    val plot: Plot?,
) {
    @Serializable
    @XmlSerialName("title", "", "")
    data class Title(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("premiered", "", "")
    data class Premiered(@XmlValue(true) val value: String = "")

    @Serializable
    @XmlSerialName("plot", "", "")
    data class Plot(@XmlValue(true) val value: String = "")
}
