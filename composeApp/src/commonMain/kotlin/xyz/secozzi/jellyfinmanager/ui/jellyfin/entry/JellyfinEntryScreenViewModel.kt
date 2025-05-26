package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dokar.sonner.Toast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okio.IOException
import org.jellyfin.sdk.model.DateTime
import org.jellyfin.sdk.model.UUID
import org.jellyfin.sdk.model.api.BaseItemDto
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jellyfin.sdk.model.api.NameGuidPair
import xyz.secozzi.jellyfinmanager.domain.anilist.AnilistRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSearchProvider
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.presentation.utils.executeCatching
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails.Companion.toEntryDetails
import xyz.secozzi.jellyfinmanager.ui.jellyfin.search.JellyfinSearchScreenViewModel

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

    private val _toasterEvent = MutableSharedFlow<Toast>()
    val toasterEvent = _toasterEvent.asSharedFlow()

    private val itemFlow = MutableStateFlow<BaseItemDto?>(null)

    init {
        executeCatching {
            jellyfinRepository.getItem(entryRoute.data.itemId).let { item ->
                _details.update { _ -> item.toEntryDetails() }
                itemFlow.update { _ -> item }

                if (entryRoute.data.itemType == JellyfinScreenViewModel.JellyfinItemType.Season) {
                    getSeasonNumber(item.path ?: "")?.let {
                        onSeasonNumberChange(it)
                    }
                }
            }
        }
    }

    fun onSearch(data: JellyfinSearchScreenViewModel.Companion.SEARCH_RESULT_TYPE) {
        when (data!!.first) {
            JellyfinSearchProvider.Anilist -> searchAnilist(data.second)
            JellyfinSearchProvider.AniDB -> updateAniDBId(data.second)
        }
    }

    private fun searchAnilist(id: String) {
        val remoteId = id.toLongOrNull() ?: return
        executeCatching {
            anilistRepository.getDetails(remoteId)?.let {
                _details.update { details ->
                    details.copy(
                        title = it.titles.first(),
                        titleList = it.titles,
                        studio = it.studio.joinToString(),
                        description = it.description ?: details.description,
                        genre = it.genre.joinToString(),
                        startDate = it.startDate,
                        endDate = it.endDate,
                        status = it.status.jellyfinName,
                    )
                }
            }
        }
    }

    private fun updateAniDBId(id: String) {
        val itemId = entryRoute.data.itemId
        val itemData = itemFlow.value!!

        val providers = (itemData.providerIds?.toMutableMap() ?: mutableMapOf()).also {
            it[JellyfinSearchProvider.AniDB.providerName] = id
        }

        val item = itemData.copy(
            providerIds = providers,

            // Don't touch these
            mediaSources = null,
            userData = null,
            mediaStreams = null,
            chapters = null,
        )

        viewModelScope.launch {
            try {
                jellyfinRepository.updateItem(
                    id = itemId,
                    type = item.type,
                    item = item,
                )
                _toasterEvent.emit(Toast("Successfully updated anidb id"))
            } catch (_: IOException) {
                _toasterEvent.emit(Toast("Failed to update anidb id"))
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
                premiereDate = details.startDate,
                productionYear = details.startDate?.year,
                startDate = details.startDate,
                endDate = details.endDate,

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

        // For season
        val seasonNumber: String?,

        // For series and movies
        val startDate: DateTime?,
        val endDate: DateTime?,
        val status: String?,
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
                startDate = this.startDate,
                endDate = this.endDate,
                status = this.status,
            )

            val EMPTY = JellyfinEntryDetails(
                title = "",
                titleList = emptyList(),
                studio = "",
                description = "",
                genre = "",
                path = "",
                seasonNumber = null,
                startDate = null,
                endDate = null,
                status = null,
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
        private val SEASON_REGEX_LIST = listOf(
            Regex("""Season (\d+)"""),
            Regex("""^(\d+)"""),
            Regex("""S(\d+)"""),
        )
    }
}
