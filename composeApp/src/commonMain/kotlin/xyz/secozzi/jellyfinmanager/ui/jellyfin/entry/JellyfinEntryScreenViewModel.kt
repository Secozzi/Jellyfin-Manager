package xyz.secozzi.jellyfinmanager.ui.jellyfin.entry

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import nl.adaptivity.xmlutil.serialization.XML
import okio.IOException
import xyz.secozzi.jellyfinmanager.data.ssh.SftpWriteFile
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinMovie
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinSeries
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Genre
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinMovieInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinSeasonInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.JellyfinSeriesInfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Plot
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Studio
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.entry.Title
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState
import xyz.secozzi.jellyfinmanager.presentation.utils.StateViewModel
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenViewModel
import xyz.secozzi.jellyfinmanager.ui.jellyfin.JellyfinItemType
import xyz.secozzi.jellyfinmanager.ui.jellyfin.entry.JellyfinEntryScreenViewModel.JellyfinEntryDetails

class JellyfinEntryScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val xml: XML,
    private val jellyfinRepository: JellyfinRepository,
    private val sftpWriteFile: SftpWriteFile,
    private val homeViewModel: HomeScreenViewModel,
) : StateViewModel<RequestState<JellyfinEntryDetails>>(RequestState.Idle) {
    val entryRoute = savedStateHandle.toRoute<JellyfinEntryRoute>(
        typeMap = JellyfinEntryRoute.typeMap,
    )

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState = _saveState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val item = jellyfinRepository.getItem(entryRoute.data.itemId)

            mutableState.update { _ ->
                item?.let {
                    RequestState.Success(
                        JellyfinEntryDetails(
                            title = it.name,
                            titleList = listOf(it.name),
                            studio = it.studios.joinToString(),
                            description = it.overview,
                            genre = it.genres.joinToString(),
                            path = it.path,
                        )
                    )
                } ?: RequestState.Error(Exception("Unable to retrieve item"))
            }
        }
    }

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
        val server = homeViewModel.selectedServer.value ?: return
        val data = mutableState.value.getSuccessData()
        val itemPath = data.path

        val nfoPath = when (entryRoute.data.itemType) {
            JellyfinItemType.Season -> "$itemPath/season.nfo"
            JellyfinItemType.Movie -> itemPath.replace(".mkv", ".nfo")
            JellyfinItemType.Series -> "$itemPath/tvshow.nfo"
        }
        val nfoContent = generateXmlString(data, entryRoute.data.itemType)
        println(nfoContent)

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
        mutableState.update { state ->
            RequestState.Success(
                (state as RequestState.Success).data.copy(
                    title = value,
                )
            )
        }
    }

    fun onStudioChange(value: String) {
        mutableState.update { state ->
            RequestState.Success(
                (state as RequestState.Success).data.copy(
                    studio = value,
                )
            )
        }
    }

    fun onDescriptionChange(value: String) {
        mutableState.update { state ->
            RequestState.Success(
                (state as RequestState.Success).data.copy(
                    description = value,
                )
            )
        }
    }

    fun onGenreChange(value: String) {
        mutableState.update { state ->
            RequestState.Success(
                (state as RequestState.Success).data.copy(
                    genre = value,
                )
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
    )

    enum class SaveState {
        Idle,
        Loading,
        Error,
        Success,
    }
}
