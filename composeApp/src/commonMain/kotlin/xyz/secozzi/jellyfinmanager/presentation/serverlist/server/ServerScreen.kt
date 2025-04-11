package xyz.secozzi.jellyfinmanager.presentation.serverlist.server

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import xyz.secozzi.jellyfinmanager.domain.database.models.Server
import xyz.secozzi.jellyfinmanager.presentation.serverlist.server.components.ServerTextField
import xyz.secozzi.jellyfinmanager.presentation.serverlist.server.components.SettingsSection
import xyz.secozzi.jellyfinmanager.presentation.utils.isLandscapeMode
import xyz.secozzi.jellyfinmanager.ui.theme.spacing

@Composable
fun ServerScreen(
    server: Server,
    serverNames: List<String>,
    onServerNameChange: (String) -> Unit,
    onJfAddressChange: (String) -> Unit,
    onJfUsernameChange: (String) -> Unit,
    onJfPasswordChange: (String) -> Unit,
    onSSHAddressChange: (String) -> Unit,
    onSSHPortChange: (String) -> Unit,
    onSSHHostnameChange: (String) -> Unit,
    onSSHPasswordChange: (String) -> Unit,
    onSSHPrivateKeyChange: (String) -> Unit,
    onSSHBaseDirChange: (String) -> Unit,
    onSSHBaseDirBlacklistChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (isLandscapeMode()) {
        ServerScreenLargeImpl(
            server = server,
            serverNames = serverNames,
            onServerNameChange = onServerNameChange,
            onJfAddressChange = onJfAddressChange,
            onJfUsernameChange = onJfUsernameChange,
            onJfPasswordChange = onJfPasswordChange,
            onSSHAddressChange = onSSHAddressChange,
            onSSHPortChange = onSSHPortChange,
            onSSHHostnameChange = onSSHHostnameChange,
            onSSHPasswordChange = onSSHPasswordChange,
            onSSHPrivateKeyChange = onSSHPrivateKeyChange,
            onSSHBaseDirChange = onSSHBaseDirChange,
            onSSHBaseDirBlacklistChange = onSSHBaseDirBlacklistChange,
            modifier = modifier,
        )
    } else {
        ServerScreenSmallImpl(
            server = server,
            serverNames = serverNames,
            onServerNameChange = onServerNameChange,
            onJfAddressChange = onJfAddressChange,
            onJfUsernameChange = onJfUsernameChange,
            onJfPasswordChange = onJfPasswordChange,
            onSSHAddressChange = onSSHAddressChange,
            onSSHPortChange = onSSHPortChange,
            onSSHHostnameChange = onSSHHostnameChange,
            onSSHPasswordChange = onSSHPasswordChange,
            onSSHPrivateKeyChange = onSSHPrivateKeyChange,
            onSSHBaseDirChange = onSSHBaseDirChange,
            onSSHBaseDirBlacklistChange = onSSHBaseDirBlacklistChange,
            modifier = modifier,
        )
    }
}

@Composable
private fun ServerScreenSmallImpl(
    server: Server,
    serverNames: List<String>,
    onServerNameChange: (String) -> Unit,
    onJfAddressChange: (String) -> Unit,
    onJfUsernameChange: (String) -> Unit,
    onJfPasswordChange: (String) -> Unit,
    onSSHAddressChange: (String) -> Unit,
    onSSHPortChange: (String) -> Unit,
    onSSHHostnameChange: (String) -> Unit,
    onSSHPasswordChange: (String) -> Unit,
    onSSHPrivateKeyChange: (String) -> Unit,
    onSSHBaseDirChange: (String) -> Unit,
    onSSHBaseDirBlacklistChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
            .padding(horizontal = MaterialTheme.spacing.medium)
            .verticalScroll(rememberScrollState()),
    ) {
        ServerSection(
            scrollable = false,
            server = server,
            serverNames = serverNames,
            onServerNameChange = onServerNameChange,
        )

        JellyfinSection(
            scrollable = false,
            server = server,
            onJfAddressChange = onJfAddressChange,
            onJfUsernameChange = onJfUsernameChange,
            onJfPasswordChange = onJfPasswordChange,
        )

        SSHSection(
            scrollable = false,
            server = server,
            onSSHAddressChange = onSSHAddressChange,
            onSSHPortChange = onSSHPortChange,
            onSSHHostnameChange = onSSHHostnameChange,
            onSSHPasswordChange = onSSHPasswordChange,
            onSSHPrivateKeyChange = onSSHPrivateKeyChange,
            onSSHBaseDirChange = onSSHBaseDirChange,
            onSSHBaseDirBlacklistChange = onSSHBaseDirBlacklistChange,
        )
    }
}

@Composable
private fun ServerScreenLargeImpl(
    server: Server,
    serverNames: List<String>,
    onServerNameChange: (String) -> Unit,
    onJfAddressChange: (String) -> Unit,
    onJfUsernameChange: (String) -> Unit,
    onJfPasswordChange: (String) -> Unit,
    onSSHAddressChange: (String) -> Unit,
    onSSHPortChange: (String) -> Unit,
    onSSHHostnameChange: (String) -> Unit,
    onSSHPasswordChange: (String) -> Unit,
    onSSHPrivateKeyChange: (String) -> Unit,
    onSSHBaseDirChange: (String) -> Unit,
    onSSHBaseDirBlacklistChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.medium),
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            ServerSection(
                scrollable = true,
                server = server,
                serverNames = serverNames,
                onServerNameChange = onServerNameChange,
            )

            JellyfinSection(
                scrollable = true,
                server = server,
                onJfAddressChange = onJfAddressChange,
                onJfUsernameChange = onJfUsernameChange,
                onJfPasswordChange = onJfPasswordChange,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            SSHSection(
                scrollable = true,
                server = server,
                onSSHAddressChange = onSSHAddressChange,
                onSSHPortChange = onSSHPortChange,
                onSSHHostnameChange = onSSHHostnameChange,
                onSSHPasswordChange = onSSHPasswordChange,
                onSSHPrivateKeyChange = onSSHPrivateKeyChange,
                onSSHBaseDirChange = onSSHBaseDirChange,
                onSSHBaseDirBlacklistChange = onSSHBaseDirBlacklistChange,
            )
        }
    }
}

@Composable
private fun ServerSection(
    scrollable: Boolean,
    server: Server,
    serverNames: List<String>,
    onServerNameChange: (String) -> Unit,
) {
    SettingsSection(
        title = "Server",
        scrollable = scrollable,
    ) {
        ServerTextField(
            value = server.name,
            label = "Server name",
            required = true,
            validate = { it.isNotBlank() && it !in serverNames },
            errorMessage = if (server.name.isBlank()) "*required" else "Server with name already exists",
            onValueChange = onServerNameChange,
        )
    }
}

@Composable
private fun JellyfinSection(
    scrollable: Boolean,
    server: Server,
    onJfAddressChange: (String) -> Unit,
    onJfUsernameChange: (String) -> Unit,
    onJfPasswordChange: (String) -> Unit,
) {
    SettingsSection(
        title = "Jellyfin",
        scrollable = scrollable,
    ) {
        ServerTextField(
            value = server.jfAddress,
            label = "Address",
            required = true,
            onValueChange = onJfAddressChange,
        )

        ServerTextField(
            value = server.jfUsername,
            label = "Username",
            required = true,
            onValueChange = onJfUsernameChange,
        )

        ServerTextField(
            value = server.jfPassword,
            label = "Password",
            required = false,
            onValueChange = onJfPasswordChange,
        )
    }
}

@Composable
private fun SSHSection(
    scrollable: Boolean,
    server: Server,
    onSSHAddressChange: (String) -> Unit,
    onSSHPortChange: (String) -> Unit,
    onSSHHostnameChange: (String) -> Unit,
    onSSHPasswordChange: (String) -> Unit,
    onSSHPrivateKeyChange: (String) -> Unit,
    onSSHBaseDirChange: (String) -> Unit,
    onSSHBaseDirBlacklistChange: (String) -> Unit,
) {
    SettingsSection(
        title = "SSH",
        scrollable = scrollable,
    ) {
        ServerTextField(
            value = server.sshAddress,
            label = "Address",
            required = true,
            onValueChange = onSSHAddressChange,
        )

        ServerTextField(
            value = server.sshPort.toString(),
            label = "Port",
            required = true,
            onValueChange = onSSHPortChange,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        ServerTextField(
            value = server.sshHostname,
            label = "Hostname",
            required = true,
            onValueChange = onSSHHostnameChange,
        )

        ServerTextField(
            value = server.sshPassword,
            label = "Password",
            required = false,
            onValueChange = onSSHPasswordChange,
        )

        ServerTextField(
            value = server.sshPrivateKey,
            label = "Private key",
            required = false,
            onValueChange = onSSHPrivateKeyChange,
        )

        ServerTextField(
            value = server.sshBaseDir,
            label = "Base directory",
            required = true,
            onValueChange = onSSHBaseDirChange,
        )

        ServerTextField(
            value = server.sshBaseDirBlacklist,
            label = "Base directory blacklist",
            required = false,
            onValueChange = onSSHBaseDirBlacklistChange,
        )
    }
}
