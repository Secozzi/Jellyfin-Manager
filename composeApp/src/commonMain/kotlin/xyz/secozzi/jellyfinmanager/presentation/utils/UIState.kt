package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.time.Duration.Companion.seconds

sealed class UIState {
    data object Idle : UIState()
    data object Loading : UIState()
    data object Success : UIState()
    data class Error(val throwable: Throwable) : UIState()

    fun isWaiting() = isIdle() || isLoading()
    fun isIdle() = this is Idle
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isSuccess() = this is Success

    fun getError(): Throwable = (this as Error).throwable
}

abstract class StateViewModel : ViewModel() {
    val mutableState = MutableStateFlow<UIState>(UIState.Idle)
    val state = mutableState.asStateFlow()
}

context(viewModel: StateViewModel)
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun <T, R> combineRefreshable(
    flow: Flow<T>,
    flow2: Flow<Unit>,
    getResult: suspend (T) -> R,
): Flow<Result<R>> = combine(
    flow,
    flow2.onStart { emit(Unit) },
) { data, _ -> data }
    .onEach { _ -> viewModel.mutableState.update { _ -> UIState.Loading } }
    .mapLatest { data -> runCatching { getResult(data) } }
    .onEach { result ->
        viewModel.mutableState.update { _ ->
            if (result.isFailure) {
                UIState.Error(result.exceptionOrNull()!!)
            } else {
                UIState.Success
            }
        }
    }

context(viewModel: StateViewModel)
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
fun <T, R> combineRefreshableResult(
    flow: Flow<T>,
    flow2: Flow<Unit>,
    getResult: suspend (T) -> Result<R>,
): Flow<Result<R>> = combine(
    flow,
    flow2.onStart { emit(Unit) },
) { data, _ -> data }
    .onEach { _ -> viewModel.mutableState.update { _ -> UIState.Loading } }
    .mapLatest { data -> getResult(data) }
    .onEach { result ->
        viewModel.mutableState.update { _ ->
            if (result.isFailure) {
                UIState.Error(result.exceptionOrNull()!!)
            } else {
                UIState.Success
            }
        }
    }

context(viewModel: StateViewModel)
fun <T> Flow<Result<T>>.asStateFlow(
    scope: CoroutineScope = viewModel.viewModelScope,
    started: SharingStarted = SharingStarted.WhileSubscribed(5.seconds),
    initialValue: Result<T> = Result.failure(IllegalStateException("Not initialized")),
): StateFlow<Result<T>> = this.stateIn(
    scope = scope,
    started = started,
    initialValue = initialValue,
)
