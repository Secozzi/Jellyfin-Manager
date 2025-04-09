package xyz.secozzi.jellyfinmanager.ui.preferences

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.alorma.compose.settings.ui.SettingsSwitch
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.koin.compose.koinInject
import xyz.secozzi.jellyfinmanager.preferences.BasePreferences
import xyz.secozzi.jellyfinmanager.preferences.preference.collectAsState
import xyz.secozzi.jellyfinmanager.preferences.preference.toggle
import xyz.secozzi.jellyfinmanager.presentation.preferences.MultiChoiceSegmentedButtonsPreference
import xyz.secozzi.jellyfinmanager.presentation.theme.DarkMode
import xyz.secozzi.jellyfinmanager.presentation.theme.isMaterialYouAvailable
import xyz.secozzi.jellyfinmanager.presentation.utils.Screen

object AppearancePreferencesScreen : Screen() {
    private fun readResolve(): Any = AppearancePreferencesScreen

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val preferences = koinInject<BasePreferences>()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text("Appearance")
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                val darkMode by preferences.darkMode.collectAsState()
                MultiChoiceSegmentedButtonsPreference(
                    DarkMode.entries.toImmutableList(),
                    valueToText = { it.name },
                    selectedIndices = persistentListOf(DarkMode.entries.indexOf(darkMode)),
                    onClick = { preferences.darkMode.set(it) },
                )

                val materialYou by preferences.materialYou.collectAsState()
                SettingsSwitch(
                    state = materialYou,
                    title = { Text("Material You") },
                    enabled = isMaterialYouAvailable,
                    onCheckedChange = { preferences.materialYou.toggle() },
                )
            }
        }
    }
}