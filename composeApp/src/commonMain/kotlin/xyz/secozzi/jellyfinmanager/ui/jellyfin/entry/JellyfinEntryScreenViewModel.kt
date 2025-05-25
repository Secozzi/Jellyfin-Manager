package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.NameGuidPair
import xyz.secozzi.jellyfinmanager.domain.anilist.AnilistRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.presentation.utils.UIState
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails.Companion.toEntryDetails

class JellyfinEntryScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val jellyfinRepository: JellyfinRepository,
    private val anilistRepository: AnilistRepository,
) : StateViewModel() {
    val entryRoute = savedStateHandle.toRoute<JellyfinEntryRoute>(
        typeMap = JellyfinEntryRoute.typeMap,
    )

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    private val _details = MutableStateFlow<JellyfinEntryDetails>(JellyfinEntryDetails.EMPTY)
    val details = _details.asStateFlow()

    private val itemFlow = MutableStateFlow<BaseItemDto?>(null)

    init {
        viewModelScope.launch {
            try {
                jellyfinRepository.getItem(entryRoute.data.itemId).let { item ->
                    _details.update { _ -> item.toEntryDetails() }
                    itemFlow.update { _ -> item }

                    if (entryRoute.data.itemType == JellyfinScreenViewModel.JellyfinItemType.Season) {
                        getSeasonNumber(item.path ?: "")?.let {
                            onSeasonNumberChange(it)
                        }
                    }

                    mutableState.update { _ -> UIState.Success }
                }
            } catch (e: Exception) {
                mutableState.update { _ -> UIState.Error(e) }
            }
        }
    }

    fun onSearch(id: String) {
        val remoteId = id.toLongOrNull() ?: return
        mutableState.update { _ -> UIState.Loading }
        viewModelScope.launch {
            try {
                anilistRepository.getDetails(remoteId)?.let {
                    _details.update { details ->
                        details.copy(
                            title = it.titles.first(),
                            titleList = it.titles,
                            studio = it.studio.joinToString(),
                            description = it.description ?: details.description,
                            genre = it.genre.joinToString(),
                        )
                    }
                }
                mutableState.update { _ -> UIState.Success }
            } catch (e: Exception) {
                mutableState.update { _ -> UIState.Error(e) }
            }
        }
    }

    fun save() {
        val id = entryRoute.data.itemId

        val details = details.value
        val itemData = itemFlow.value!!

        val item = itemData
            .copy(
                name = details.title,
                overview = details.description,
                genres = details.genre.split(", "),
                studios = details.studio.split(", ").map {
                    NameGuidPair(
                        name = it,
                        id = UUID.randomUUID(),
                    )
                },
                indexNumber = details.seasonNumber?.toIntOrNull() ?: itemData.indexNumber,

                // Don't touch these
                mediaSources = null,
                userData = null,
                mediaStreams = null,
                chapters = null,
            )

        _saveState.update { _ -> SaveState.Loading }
        try {
            viewModelScope.launch {
                jellyfinRepository.updateItem(
                    id = id,
                    type = item.type,
                    item = item,
                )
            }
            _saveState.update { _ -> SaveState.Success }
        } catch (_: IOException) {
            _saveState.update { _ -> SaveState.Error }
        }
    }

    fun onTitleChange(value: String) {
        _details.update { details ->
            details.copy(
                title = value,
            )
        }
    }

    fun onStudioChange(value: String) {
        _details.update { details ->
            details.copy(
                studio = value,
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _details.update { details ->
            details.copy(
                description = value,
            )
        }
    }

    fun onGenreChange(value: String) {
        _details.update { details ->
            details.copy(
                genre = value,
            )
        }
    }

    fun onSeasonNumberChange(value: String) {
        if (value.isEmpty() || value.toIntOrNull() != null) {
            _details.update { details ->
                details.copy(
                    seasonNumber = value,
                )
            }
        }
    }

    private fun getSeasonNumber(path: String): String? {
        val name = path.substringAfterLast("/")

        SEASON_REGEX_LIST.forEach { pattern ->
            pattern.find(name)?.let {
                return it.groupValues[1]
            }
        }

        return null
    }

    data class JellyfinEntryDetails(
        val title: String,
        val titleList: List<String>,
        val studio: String,
        val description: String,
        val genre: String,
        val path: String,
        val seasonNumber: String?,
    ) {
        companion object {
            fun BaseItemDto.toEntryDetails() = JellyfinEntryDetails(
                title = this.name ?: "",
                titleList = listOf(this.name ?: ""),
                studio = this.studios?.mapNotNull { it.name }?.joinToString() ?: "",
                description = this.overview ?: "",
                genre = this.genres.orEmpty().joinToString(),
                path = this.path ?: "",
                seasonNumber = (this.indexNumber ?: 0).takeIf { this.type == BaseItemKind.SEASON }?.toString(),
            )

            val EMPTY = JellyfinEntryDetails(
                title = "",
                titleList = emptyList(),
                studio = "",
                description = "",
                genre = "",
                path = "",
                seasonNumber = null,
            )
        }
    }

    enum class SaveState {
        Idle,
        Loading,
        Error,
        Success,
    }

    companion object {
        const val SEARCH_RESULT_KEY = "search_result_key"

        private val SEASON_REGEX_LIST = listOf(
            Regex("""Season (\d+)"""),
            Regex("""^(\d+)"""),
            Regex("""S(\d+)"""),
        )
    }
}
