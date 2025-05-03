package xyz.secozzi.jellyfinmanager.presentation.jellyfin.entry

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import xyz.secozzi.jellyfinmanager.domain.jellyfin.models.JellyfinItem
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen

class JellyfinEntryScreen(private val item: JellyfinItem) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = item.name)
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            }
        ) {
            Text("UwU")
        }
    }
}
