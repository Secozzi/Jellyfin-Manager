package xyz.secozzi.jellyfinmanager.ui.home.tabs.ssh

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.TabOptions
import xyz.secozzi.jellyfinmanager.presentation.utils.Tab

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
        Scaffold {
            Text("SSH!")
        }
    }
}
