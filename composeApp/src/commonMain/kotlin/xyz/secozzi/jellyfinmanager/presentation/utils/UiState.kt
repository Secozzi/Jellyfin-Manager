package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlin.time.Duration.Companion.seconds

sealed class UiState<out T> {
    data object Idle : UiState<Nothing>()
    data object Loading : UiState<Nothing>()
    data class Error(val throwable: Throwable) : UiState<Nothing>()
    data class Success<T>(val successData: T) : UiState<T>()

    fun isWaiting() = isIdle() || isLoading()
    fun isIdle() = this is Idle
    fun isLoading() = this is Loading
    fun isError() = this is Error
    fun isSuccess() = this is Success

    fun getError(): Throwable = (this as Error).throwable
    fun getData(): T = (this as Success).successData
    fun getDataOrNull(): T? = (this as? Success)?.successData
}

context(viewModel: ViewModel)
fun <T, R> combineRefreshable(
    flow: Flow<T>,
    refreshFlow: Flow<Unit>,
    getResult: suspend (T) -> R,
): StateFlow<UiState<R>> = combine(
    flow,
    refreshFlow.onStart { emit(Unit) },
) { data, _ ->
    data
}.asResultFlow(getResult = getResult)

context(viewModel: ViewModel)
fun <T, R> Flow<T>.asResultFlow(
    getResult: suspend (T) -> R,
): StateFlow<UiState<R>> = this.flatMapLatest { data ->
    flow {
        emit(UiState.Loading)
        try {
            val result = getResult(data)
            emit(UiState.Success(result))
        } catch (e: Exception) {
            emit(UiState.Error(e))
        }
    }
}
    .stateIn(
        scope = viewModel.viewModelScope,
        started = SharingStarted.WhileSubscribed(5.seconds),
        initialValue = UiState.Idle,
    )
