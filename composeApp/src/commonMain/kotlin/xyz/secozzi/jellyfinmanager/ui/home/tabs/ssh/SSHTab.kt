package xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab
import xyz.secozzi.jellyfinmanager.ui.home.HomeScreenScreenModel

object SSHTab : Tab {
    private fun readResolve(): Any = SSHTab

    override val options: TabOptions
        @Composable
        get() {
            val image = rememberVectorPainter(Icons.Default.Terminal)
            return TabOptions(
                index = 0u,
                title = "SSH",
                icon = image,
            )
        }

    @Composable
    override fun Content() {
        val homeScreenModel = koinScreenModel<HomeScreenScreenModel>()
        val screenModel = koinScreenModel<SSHTabScreenModel>()

        LaunchedEffect(Unit) {
            homeScreenModel.selectedServer.collect {
                screenModel.changeServer(it)
            }
        }

        val selectedServer by screenModel.selectedServer.collectAsState()

        Text("SSH: " + (selectedServer?.name ?: "Waiting"))
    }
}
