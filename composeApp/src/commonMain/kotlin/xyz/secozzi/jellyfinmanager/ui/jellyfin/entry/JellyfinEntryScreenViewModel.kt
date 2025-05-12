package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import okio.IOException
import xyz.secozzi.jellyfinmanager.data.ssh.SftpWriteFile
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Genre
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinMovieInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinSeasonInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinSeriesInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Plot
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Studio
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Title
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Companion.asStateFlow
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails.Companion.toEntryDetails

class JellyfinEntryScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val xml: XML,
    private val jellyfinRepository: JellyfinRepository,
    private val sftpWriteFile: SftpWriteFile,
    private val serverStateHolder: ServerStateHolder,
) : ViewModel() {
    val entryRoute = savedStateHandle.toRoute<JellyfinEntryRoute>(
        typeMap = JellyfinEntryRoute.typeMap,
    )

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    private val _details = MutableStateFlow<JellyfinEntryDetails>(JellyfinEntryDetails.EMPTY)
    val details = _details.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val state = flow {
        val item = try {
            jellyfinRepository.getItem(entryRoute.data.itemId)
        } catch (_: Exception) {
            null
        }

        emit(item)
    }
        .onEach { item ->
            item?.let { _details.update { _ -> it.toEntryDetails() } }
        }
        .mapLatest { item ->
            item?.let { RequestState.Success(it) }
                ?: RequestState.Error(Exception("Unable to retrieve item"))
        }
        .asStateFlow()

    private fun generateXmlString(data: JellyfinEntryDetails, type: JellyfinItemType): String {
        val title = Title(data.title)
        val description = Plot(data.description)
        val genre = Genre(data.genre)
        val studios = data.studio.split(", ").map { Studio(it) }

        val info = when (type) {
            JellyfinItemType.Season -> JellyfinSeasonInfo(title, description, genre, studios)
            JellyfinItemType.Movie -> JellyfinMovieInfo(title, description, genre, studios)
            JellyfinItemType.Series -> JellyfinSeriesInfo(title, description, genre, studios)
        }

        return xml.encodeToString(info)
    }

    fun save() {
        val server = serverStateHolder.selectedServer.value ?: return
        val data = details.value
        val itemPath = data.path

        val nfoPath = when (entryRoute.data.itemType) {
            JellyfinItemType.Season -> "$itemPath/season.nfo"
            JellyfinItemType.Movie -> itemPath.replace(".mkv", ".nfo")
            JellyfinItemType.Series -> "$itemPath/tvshow.nfo"
        }
        val nfoContent = generateXmlString(data, entryRoute.data.itemType)

        _saveState.update { _ -> SaveState.Loading }
        try {
            viewModelScope.launch {
                sftpWriteFile(
                    server = server,
                    filePath = server.sshBaseDir + nfoPath,
                    fileContents = nfoContent,
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
                title = value
            )
        }
    }

    fun onStudioChange(value: String) {
        _details.update { details ->
            details.copy(
                studio = value
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _details.update { details ->
            details.copy(
                description = value
            )
        }
    }

    fun onGenreChange(value: String) {
        _details.update { details ->
            details.copy(
                genre = value
            )
        }
    }

    data class JellyfinEntryDetails(
        val title: String,
        val titleList: List<String>,
        val studio: String,
        val description: String,
        val genre: String,
        val path: String,
    ) {
        companion object {
            fun JellyfinItem.toEntryDetails() = JellyfinEntryDetails(
                title = this.name,
                titleList = listOf(this.name),
                studio = this.studios.joinToString(),
                description = this.overview,
                genre = this.genres.joinToString(),
                path = this.path,
            )

            val EMPTY = JellyfinEntryDetails(
                title = "",
                titleList = emptyList(),
                studio = "",
                description = "",
                genre = "",
                path = "",
            )
        }
    }

    enum class SaveState {
        Idle,
        Loading,
        Error,
        Success,
    }
}
