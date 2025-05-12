package xyz.secozzi.jellyfinmanager.presentation.utils

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Error
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Loading
import xyz.secozzi.jellyfinmanager.presentation.utils.RequestState.Success
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

// https://gist.github.com/stevdza-san/cca20eff9f2c4c7d783ffd0a0061b352
sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()
    data object Loading : RequestState<Nothing>()
    data class Success<T>(val data: T) : RequestState<T>()
    data class Error(val throwable: Throwable) : RequestState<Nothing>()

    fun isIdle() = this is Idle
    fun isLoading() = this is Loading
    fun isSuccess() = this is Success
    fun isError() = this is Error

    /**
     * Returns data from a [Success].
     * @throws ClassCastException If the current state is not [Success]
     *  */
    fun getSuccessData() = (this as Success).data
    fun getSuccessDataOrNull() = (this as? Success)?.data

    /**
     * Returns an error message from an [Error]
     * @throws ClassCastException If the current state is not [Error]
     *  */
    fun getError() = (this as Error).throwable
    fun getErrorOrNull() = (this as? Error)?.throwable

    @Composable
    fun DisplayResult(
        onLoading: @Composable () -> Unit,
        onSuccess: @Composable (T) -> Unit,
        onError: @Composable (Throwable) -> Unit,
        modifier: Modifier = Modifier,
        onIdle: (@Composable () -> Unit)? = null,
    ) {
        AnimatedContent(
            targetState = this@RequestState,
            transitionSpec = {
                fadeIn(tween(durationMillis = 300)) togetherWith fadeOut(tween(durationMillis = 300))
            },
            label = "Content Animation",
            modifier = modifier,
        ) { state ->
            when (state) {
                is Idle -> {
                    onIdle?.invoke() ?: onLoading()
                }

                is Loading -> {
                    onLoading()
                }

                is Success -> {
                    onSuccess(state.getSuccessData())
                }

                is Error -> {
                    onError(state.throwable)
                }
            }
        }
    }

    companion object {
        context(viewModel: ViewModel)
        fun <T> Flow<RequestState<T>>.asStateFlow(
            scope: CoroutineScope = viewModel.viewModelScope,
            started: SharingStarted = SharingStarted.WhileSubscribed(5.seconds),
            initialValue: RequestState<T> = Idle,
        ): StateFlow<RequestState<T>> = this.stateIn(
            scope = scope,
            started = started,
            initialValue = initialValue,
        )

        fun <T> Result<T>.toRequestState(): RequestState<T> {
            return if (this.isSuccess) {
                Success(this.getOrThrow())
            } else {
                Error(this.exceptionOrNull() ?: Exception("error"))
            }
        }
    }
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@Suppress("UNCHECKED_CAST")
fun <T, R> combineRefreshable(
    flow: Flow<T>,
    flow2: Flow<Unit>,
    getResult: suspend (T) -> RequestState<R>,
): Flow<RequestState<R>> = combine(
    flow,
    flow2.onStart { emit(Unit) },
) { args: Array<*> ->
    args[0] as T
}
    .flatMapLatest { data ->
        flow {
            emit(Loading)
            try {
                val result = getResult(data)
                emit(result)
            } catch (e: Exception) {
                emit(Error(e))
            }
        }
    }
