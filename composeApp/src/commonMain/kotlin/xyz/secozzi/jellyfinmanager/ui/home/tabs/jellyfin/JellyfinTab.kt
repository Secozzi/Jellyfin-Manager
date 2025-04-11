package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.jellyfin
import org.jetbrains.compose.resources.vectorResource
import org.koin.core.parameter.parametersOf
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab

data class JellyfinTab(
    private val server: Server,
) : Tab {
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
        val screenModel = koinScreenModel<JellyfinTabScreenModel>(
            parameters = { parametersOf(server) }
        )

        Text("Jellyfin")
    }
}
