package xyz.secozzi.jellyfinmanager.ui.jellyfin.cover

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.dokar.sonner.Toast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import xyz.secozzi.jellyfinmanager.domain.jellyfin.JellyfinRepository
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinImageType
import xyz.secozzi.jellyfinmanager.presentation.utils.asResultFlow

class JellyfinCoverScreenViewModel(
    savedStateHandle: SavedStateHandle,
    private val jellyfinRepository: JellyfinRepository,
) : ViewModel() {
    val coverRoute = savedStateHandle.toRoute<JellyfinCoverRoute>(
        typeMap = JellyfinCoverRoute.typeMap,
    ).data

    private val _selectedType = MutableStateFlow(JellyfinImageType.Primary)
    val selectedType = _selectedType.asStateFlow()

    private val _toasterEvent = MutableSharedFlow<Toast>()
    val toasterEvent = _toasterEvent.asSharedFlow()

    val state = selectedType.asResultFlow {
        jellyfinRepository.getRemoteImages(id = coverRoute.itemId, imageType = it)
    }

    private val currentImagesFlow = flow { emit(coverRoute.itemId) }
    private val refreshFlow = MutableSharedFlow<Unit>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val current = combine(
        currentImagesFlow,
        refreshFlow.onStart { emit(Unit) },
    ) { data, _ -> data }
        .asResultFlow {
            jellyfinRepository.getImages(id = it)
        }

    fun onSelectType(type: JellyfinImageType) {
        _selectedType.update { _ -> type }
    }

    fun uploadImage(imageUrl: String) {
        viewModelScope.launch {
            val result = try {
                jellyfinRepository.uploadImage(
                    id = coverRoute.itemId,
                    imageType = selectedType.value,
                    imageUrl = imageUrl,
                )
            } catch (_: Exception) {
                false
            }

            if (result) {
                _toasterEvent.emit(Toast("Successfully uploaded image"))
                refreshFlow.emit(Unit)
            } else {
                _toasterEvent.emit(Toast("Failed to upload image"))
            }
        }
    }

    fun removeImage() {
        viewModelScope.launch {
            val result = try {
                jellyfinRepository.deleteImage(coverRoute.itemId, selectedType.value)
            } catch (_: Exception) {
                false
            }

            if (result) {
                _toasterEvent.emit(Toast("Successfully deleted image"))
                refreshFlow.emit(Unit)
            } else {
                _toasterEvent.emit(Toast("Failed to delete image"))
            }
        }
    }
}
