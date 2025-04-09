package xyz.secozzi.jellyfinmanager.ui.home.tabs.jellyfin

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import jellyfin_manager.composeapp.generated.resources.Res
import jellyfin_manager.composeapp.generated.resources.jellyfin
import org.jetbrains.compose.resources.vectorResource
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab

object JellyfinTab : Tab {
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
        Scaffold {
            Text("JELLYFIN!")
        }
    }
}
