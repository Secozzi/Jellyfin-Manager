package xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlSerialName

@Serializable
@XmlSerialName("season", "", "")
data class JellyfinSeasonInfo(
    override val title: Title,
    override val plot: Plot,
    override val genre: Genre,
    override val studio: List<Studio>,
) : JellyfinCommonInfo
