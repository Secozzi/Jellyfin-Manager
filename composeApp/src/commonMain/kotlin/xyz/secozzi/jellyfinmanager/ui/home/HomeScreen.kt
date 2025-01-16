package xyz.secozzi.jellyfinmanager.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import xyz.secozzi.jellyfinmanager.presentation.Screen

object HomeScreen : Screen() {
    private fun readResolve(): Any = HomeScreen

    @Composable
    override fun Content() {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text("Home Screen")
        }
    }
}
