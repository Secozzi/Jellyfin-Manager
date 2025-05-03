package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import coil3.compose.AsyncImage
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.jellyfin
import org.jellyfin.sdk.model.api.BaseItemKind
import org.jetbrains.compose.resources.vectorResource
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.JellyfinScreen
import xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry.JellyfinEntryScreen
import xyz.secozzi.jellyfinmanager.presentation.screen.ErrorScreenContent
import xyz.secozzi.jellyfinmanager.presentation.screen.LoadingScreenContent
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenScreenModel
import xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh.SSHTabScreenModel

object JellyfinTab: Tab {
    private fun readResolve(): Any = JellyfinTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(vectorResource(Res.drawable.jellyfin))
            return TabOptions(
                index = 0u,
                title = "Jellyfin",
                icon = image,
            )
        }

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        val homeScreenModel = koinScreenModel<HomeScreenScreenModel>()
        val screenModel = koinScreenModel<JellyfinTabScreenModel>()

        LaunchedEffect(Unit) {
            homeScreenModel.state.collect { selected ->
                selected.getSuccessDataOrNull()
                    ?.let(screenModel::changeServer)
            }
        }

        val state by screenModel.state.collectAsState()
        val itemList by screenModel.itemList.collectAsState()

        BackHandler(itemList.size > 1) {
            screenModel.onNavigateTo(itemList.size - 2)
        }

        JellyfinScreen(
            state = state,
            itemList = itemList,
            onNavigateTo = screenModel::onNavigateTo,
            onClickItem = {
                when (it.type) {
                    BaseItemKind.MOVIE -> navigator.push(JellyfinEntryScreen(it))
                    BaseItemKind.SEASON -> navigator.push(JellyfinEntryScreen(it))
                    else -> screenModel.onClickItem(it)
                }
            },
        )
    }
}
