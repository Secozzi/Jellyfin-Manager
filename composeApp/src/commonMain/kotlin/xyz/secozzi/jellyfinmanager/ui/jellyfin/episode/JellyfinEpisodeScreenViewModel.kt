package xyz.secozzi.jellyfinmanager.ui.jellyfin.episode

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dokar.sonner.Toast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.schmizz.sshj.SSHClient
import nl.adaptivity.xmlutil.serialization.XML
import org.jellyfin.sdk.model.api.BaseItemDto
import xyz.secozzi.jellyfinmanager.data.ssh.GetSSHClient
import xyz.secozzi.jellyfinmanager.domain.anidb.AniDBRepository
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisode
import xyz.secozzi.jellyfinmanager.domain.anidb.models.AniDBEpisodeType
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.EpisodeNfo
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.MovieNfo
import xyz.secozzi.jellyfinmanager.domain.server.ServerStateHolder
import xyz.secozzi.jellyfinmanager.domain.ssh.GetDirectories
import xyz.secozzi.jellyfinmanager.domain.ssh.model.Directory
import xyz.secozzi.jellyfinmanager.presentation.utils.UiState
import xyz.secozzi.jellyfinmanager.utils.Constants
import xyz.secozzi.jellyfinmanager.utils.useSFTPClient
import xyz.secozzi.jellyfinmanager.utils.writeToFile

class JellyfinEpisodeScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val xml: XML,
    private val jellyfinRepository: JellyfinRepository,
    private val aniDBRepository: AniDBRepository,
    private val getDirectories: GetDirectories,
    private val serverStateHolder: ServerStateHolder,
    private val getSSHClient: GetSSHClient,
) : ViewModel() {
    val episodeRoute = savedStateHandle.toRoute<JellyfinEpisodeRoute>(
        typeMap = JellyfinEpisodeRoute.typeMap,
    )

    private val sshClient = MutableStateFlow<SSHClient?>(null)
    private val episodeList = MutableStateFlow<Map<Int, List<AniDBEpisode>>>(emptyMap())

    private val _state = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val state = _state.asStateFlow()

    private val _uploadState = MutableStateFlow<UploadState>(UploadState.Idle)
    val uploadState = _uploadState.asStateFlow()

    private val _item = MutableStateFlow<BaseItemDto?>(null)
    val item = _item.asStateFlow()

    private val _aniDBId = MutableStateFlow<Long?>(null)
    val aniDBId = _aniDBId.asStateFlow()

    private val _availableTypes = MutableStateFlow<List<AniDBEpisodeType>>(emptyList())
    val availableTypes = _availableTypes.asStateFlow()

    private val _selectedType = MutableStateFlow<AniDBEpisodeType>(AniDBEpisodeType.Regular(extraData = 1))
    val selectedType = _selectedType.asStateFlow()

    private val _remoteFileList = MutableStateFlow<List<Directory>>(emptyList())
    val remoteFileList = _remoteFileList.asStateFlow()

    private val _toasterEvent = MutableSharedFlow<Toast>()
    val toasterEvent = _toasterEvent.asSharedFlow()

    private val _episodeInfo = MutableStateFlow<EpisodeInfo>(
        EpisodeInfo(
            start = 1,
            end = 1,
            offset = 0,
            isValid = true,
            startPreview = null,
            endPreview = null,
        ),
    )
    val episodeInfo = _episodeInfo.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                jellyfinRepository.getItem(episodeRoute.data.itemId).let { item ->
                    val aniDBId = item.providerIds?.get("AniDB")?.toLongOrNull()

                    val server = serverStateHolder.selectedServer.value!!
                    val files = getDirectories(
                        sshClient = null,
                        server = server,
                        path = server.sshBaseDir + item.path,
                    ).sortedBy { it.name }

                    _remoteFileList.update { _ ->
                        files.filter { f ->
                            !f.isDirectory && Constants.videoFileExtensions.any { f.name.endsWith(it, true) }
                        }
                    }

                    _item.update { _ -> item }
                    _aniDBId.update { _ -> aniDBId }

                    aniDBId?.let {
                        episodeList.update { _ ->
                            val episodes = aniDBRepository.getEpisodes(it)
                                .groupBy { it.aniDBEpisodeType.id }
                                .toSortedMap()
                                .mapValues { (_, episodes) -> episodes.sortedBy { it.episodeNumber } }

                            _availableTypes.update {
                                episodes.map { (type, items) ->
                                    when (type) {
                                        1 -> AniDBEpisodeType.Regular(extraData = items.size)
                                        2 -> AniDBEpisodeType.Special(extraData = items.size)
                                        3 -> AniDBEpisodeType.Credit(extraData = items.size)
                                        4 -> AniDBEpisodeType.Trailer(extraData = items.size)
                                        5 -> AniDBEpisodeType.Parody(extraData = items.size)
                                        else -> AniDBEpisodeType.Other(extraData = items.size)
                                    }
                                }
                            }

                            _selectedType.update { _ ->
                                availableTypes.value.first()
                            }

                            val endValue = selectedType.value.extraData!!
                            _episodeInfo.update { info ->
                                info.copy(
                                    start = 1,
                                    end = endValue,
                                    startPreview = episodes[selectedType.value.id]!![0].copy(
                                        episodeNumber = info.offset + 1,
                                    ),
                                    endPreview = episodes[selectedType.value.id]!![endValue - 1].copy(
                                        episodeNumber = info.offset + endValue,
                                    ),
                                )
                            }

                            episodes
                        }
                    }

                    _state.update { _ -> UiState.Success(Unit) }
                }
            } catch (e: Exception) {
                _state.update { _ -> UiState.Error(e) }
            }
        }
    }

    fun onSelectType(input: AniDBEpisodeType?) {
        input?.let {
            _selectedType.update { _ -> it }

            val endValue = it.extraData!!
            _episodeInfo.update { info ->
                info.copy(
                    start = 1,
                    end = endValue,
                    startPreview = episodeList.value[it.id]!![0].copy(
                        episodeNumber = info.offset + 1,
                    ),
                    endPreview = episodeList.value[it.id]!![endValue - 1].copy(
                        episodeNumber = info.offset + endValue,
                    ),
                )
            }
        }
    }

    fun updateOffset(input: Int) {
        _episodeInfo.update { info ->
            info.copy(
                offset = input,
            )
        }
        updatePreviewCards()
    }

    fun updateStart(input: Int) {
        _episodeInfo.update { info ->
            info.copy(
                start = input,
            )
        }
        updateIsValid(episodeInfo.value.end >= input && input >= 1 && input <= selectedType.value.extraData!!)
    }

    fun updateEnd(input: Int) {
        _episodeInfo.update { info ->
            info.copy(
                end = input,
            )
        }
        updateIsValid(episodeInfo.value.end >= input && input >= 1 && input <= selectedType.value.extraData!!)
    }

    private fun updateIsValid(input: Boolean) {
        _episodeInfo.update { info ->
            info.copy(
                isValid = input,
            )
        }
        if (input) {
            updatePreviewCards()
        }
    }

    private fun updatePreviewCards() {
        val selectedList = episodeList.value[selectedType.value.id]!!
        _episodeInfo.update { info ->
            info.copy(
                startPreview = selectedList[info.start - 1].copy(
                    episodeNumber = info.offset + info.start,
                ),
                endPreview = selectedList[info.end - 1].copy(
                    episodeNumber = info.offset + info.end,
                ),
            )
        }
    }

    fun uploadFiles() {
        if (uploadState.value is UploadState.Loading) return
        if (item.value == null) return

        _uploadState.update { _ -> UploadState.Loading(0) }

        if (episodeRoute.data.isSeason) {
            uploadSeason()
        } else {
            uploadMovie()
        }
    }

    private fun uploadSeason() {
        val info = episodeInfo.value
        val selectedList = episodeList.value[selectedType.value.id]!!.subList(info.start - 1, info.end).map {
            EpisodeNfo(
                title = EpisodeNfo.Title(it.englishTitle ?: it.romajiTitle ?: it.nativeTitle ?: ""),
                episode = EpisodeNfo.Episode((it.episodeNumber + info.offset).toString()),
                aired = it.airingDate?.let { a -> EpisodeNfo.Aired(a) },
                season = EpisodeNfo.Season(item.value!!.indexNumber!!.toString()),
                plot = it.summary?.let { s -> EpisodeNfo.Plot(s) },
            )
        }

        val server = serverStateHolder.selectedServer.value!!
        val dirPath = server.sshBaseDir + item.value!!.path
        viewModelScope.launch {
            if (sshClient.value == null || sshClient.value?.isConnected == false) {
                sshClient.update { _ -> getSSHClient(server) }
            }

            try {
                useSFTPClient(sshClient.value) { client ->
                    remoteFileList.value.take(selectedList.size).forEachIndexed { idx, file ->
                        val itemName = file.name.substringAfterLast("/")
                            .substringBeforeLast(".")

                        client.writeToFile(
                            path = "$dirPath/$itemName.nfo",
                            content = xml.encodeToString(EpisodeNfo.serializer(), selectedList[idx]),
                        )

                        _uploadState.update { _ ->
                            UploadState.Loading((idx.toDouble() / remoteFileList.value.size).times(100).toInt())
                        }
                    }
                }

                _uploadState.update { _ -> UploadState.Success }
            } catch (e: Exception) {
                _toasterEvent.emit(Toast(e.message ?: "Unknown error occurred"))
                _uploadState.update { _ -> UploadState.Error(e) }
            }
        }
    }

    private fun uploadMovie() {
        val movieData = episodeList.value[selectedType.value.id]!!.first()
        val movieNfo = MovieNfo(
            title = MovieNfo.Title(movieData.englishTitle ?: movieData.romajiTitle ?: movieData.nativeTitle ?: ""),
            premiered = movieData.airingDate?.let { MovieNfo.Premiered(it) },
            plot = movieData.summary?.let { MovieNfo.Plot(it) },
        )

        val server = serverStateHolder.selectedServer.value!!
        val path = server.sshBaseDir + item.value!!.path
        viewModelScope.launch {
            if (sshClient.value == null || sshClient.value?.isConnected == false) {
                sshClient.update { _ -> getSSHClient(server) }
            }

            try {
                useSFTPClient(sshClient.value) { client ->
                    val itemName = path.substringBeforeLast(".")
                    client.writeToFile(
                        path = "$itemName.nfo",
                        content = xml.encodeToString(MovieNfo.serializer(), movieNfo),
                    )
                }

                _uploadState.update { _ -> UploadState.Success }
            } catch (e: Exception) {
                _uploadState.update { _ -> UploadState.Error(e) }
            }
        }
    }

    sealed class UploadState {
        data object Idle : UploadState()
        data class Loading(val progress: Int) : UploadState()
        data class Error(val throwable: Throwable) : UploadState()
        data object Success : UploadState()
    }

    @Immutable
    data class EpisodeInfo(
        val start: Int,
        val end: Int,
        val offset: Int,
        val isValid: Boolean,
        val startPreview: AniDBEpisode?,
        val endPreview: AniDBEpisode?,
    )
}
