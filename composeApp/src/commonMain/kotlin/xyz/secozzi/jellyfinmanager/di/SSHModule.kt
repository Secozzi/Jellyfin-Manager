package xyz.secozzi.jellyfinmanager.di

import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import xyz.secozzi.jellyfinmanager.data.ssh.ExecuteSSH
import xyz.secozzi.jellyfinmanager.data.ssh.GetSSHClient
import xyz.secozzi.jellyfinmanager.domain.ssh.GetDirectories

val SSHModule = module {
    factoryOf(::ExecuteSSH)
    factoryOf(::GetDirectories)
    factoryOf(::GetSSHClient)
}
