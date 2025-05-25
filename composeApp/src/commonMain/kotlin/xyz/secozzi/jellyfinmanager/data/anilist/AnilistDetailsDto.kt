package xyz.secozzi.jellyfinmanager.data.anilist

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnilistDetailsPostData(
    val query: String,
    val variables: AnilistDetailsVariablesData,
)

@Serializable
data class AnilistDetailsVariablesData(
    val id: Long,
)

@Serializable
data class AnilistDetailsDto(
    val data: DataDto,
) {
    @Serializable
    data class DataDto(
        @SerialName("Media")
        val media: MediaDto? = null,
    ) {
        @Serializable
        data class MediaDto(
            val title: TitleDto,
            val description: String? = null,
            val genres: List<String>,
            val studios: StudiosDto,
        ) {
            @Serializable
            data class TitleDto(
                val romaji: String? = null,
                val english: String? = null,
                val native: String? = null,
            )

            @Serializable
            data class StudiosDto(
                val edges: List<StudioEdge>,
            ) {
                @Serializable
                data class StudioEdge(
                    val isMain: Boolean,
                    val node: StudioNode,
                ) {
                    @Serializable
                    data class StudioNode(
                        val name: String,
                    )
                }
            }
        }
    }
}
