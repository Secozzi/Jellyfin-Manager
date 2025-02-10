package xyz.secozzi.jellyfinmanager.ui.home.tabs

import androidx.compose.foundation.Image
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import jellyfinmanager.composeapp.generated.resources.Res
import jellyfinmanager.composeapp.generated.resources.jellyfin
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
        Text("UwU")
    }

    // ImageVector.vectorResource(R.drawable.anidb_icon)
}