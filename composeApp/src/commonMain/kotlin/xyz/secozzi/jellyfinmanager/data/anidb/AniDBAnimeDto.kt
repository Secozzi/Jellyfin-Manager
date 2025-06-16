package xyz.secozzi.jellyfinmanager.data.anidb

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisode
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisodeType

@Serializable
@XmlSerialName("anime", "", "")
data class AniDBAnimeDto(
    val episodes: Episodes,
) {
    @Serializable
    @XmlSerialName("episodes", "", "")
    data class Episodes(
        @XmlElement(true)
        val episode: List<EpisodeDto>,
    )
}

@Serializable
@XmlSerialName("episode", "", "")
data class EpisodeDto(
    val epno: EpNoDto,
    val length: Length?,
    val airdate: AirDate?,
    val rating: Rating?,
    val title: List<Title>,
    val summary: Summary?,
) {
    @Serializable
    @XmlSerialName("epno", "", "")
    data class EpNoDto(
        @XmlSerialName("type", "", "")
        val type: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("length", "", "")
    data class Length(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("airdate", "", "")
    data class AirDate(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("rating", "", "")
    data class Rating(
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("title", "", "")
    data class Title(
        @XmlSerialName("lang", "http://www.w3.org/XML/1998/namespace", "")
        val language: String,
        @XmlValue(true)
        val value: String,
    )

    @Serializable
    @XmlSerialName("summary", "", "")
    data class Summary(
        @XmlValue(true)
        val value: String,
    )
}

fun EpisodeDto.toEpisodeModel(): AniDBEpisode {
    return AniDBEpisode(
        englishTitle = this.title.firstOrNull { it.language == "en" }?.value,
        romajiTitle = this.title.firstOrNull { it.language == "x-jat" }?.value,
        nativeTitle = this.title.firstOrNull { it.language == "ja" }?.value,
        episodeNumber = if (this.epno.type == "1") {
            this.epno.value.toInt()
        } else {
            this.epno.value.drop(1).toInt()
        },
        duration = this.length?.value?.toInt(),
        airingDate = this.airdate?.value,
        rating = this.rating?.value,
        summary = this.summary?.value,
        aniDBEpisodeType = when (this.epno.type) {
            "1" -> AniDBEpisodeType.Regular()
            "2" -> AniDBEpisodeType.Special()
            "3" -> AniDBEpisodeType.Credit()
            "4" -> AniDBEpisodeType.Trailer()
            "5" -> AniDBEpisodeType.Parody()
            else -> AniDBEpisodeType.Other()
        },
    )
}
