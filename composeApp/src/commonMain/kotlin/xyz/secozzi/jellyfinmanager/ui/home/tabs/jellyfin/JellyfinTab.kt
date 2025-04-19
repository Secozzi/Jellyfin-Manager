package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.jellyfin
import org.jetbrains.compose.resources.vectorResource
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

    @Composable
    override fun Content() {
        val homeScreenModel = koinScreenModel<HomeScreenScreenModel>()
        val screenModel = koinScreenModel<JellyfinTabScreenModel>()

        LaunchedEffect(Unit) {
            homeScreenModel.state.collect { selected ->
                selected.getSuccessDataOrNull()
                    ?.let(screenModel::changeServer)
            }
        }

        val selectedServer by screenModel.selectedServer.collectAsState()

        Text("JELLYFIN: " + (selectedServer?.name ?: "Waiting"))
    }
}
