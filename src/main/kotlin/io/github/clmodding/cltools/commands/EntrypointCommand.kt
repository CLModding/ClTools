package io.github.clmodding.cltools.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands

class EntrypointCommand : CliktCommand() {
    init {
        subcommands(LatestCommand())
    }
    override fun run() = Unit
}
