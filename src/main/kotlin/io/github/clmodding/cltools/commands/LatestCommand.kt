package io.github.clmodding.cltools.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.multiple
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.enum
import io.github.clmodding.cltools.mappings.MappingsUtils
import io.github.clmodding.cltools.model.CraftLandiaVersionUtils
import io.github.clmodding.cltools.model.remap.RemapLevel
import io.github.clmodding.cltools.model.update.UpdateFile
import io.github.clmodding.cltools.model.version.ClientVersion
import io.github.clmodding.cltools.utils.RemapMappingFile
import io.github.clmodding.cltools.utils.RemapUtils
import java.io.File
import java.net.URL

class LatestCommand : CliktCommand("Download the latest version of CraftLandia's client") {

    private val version: ClientVersion by argument(help = "The version to download").enum<ClientVersion>()
        .default(ClientVersion.FAREWELL)

    private val includeMacrosMod: Boolean by option(
        "--macros", help = "Whether or not to also download the macros mod jar"
    ).flag(default = false)

    private val remapLevel: List<RemapLevel> by option(
        "--remap", help = "Remap level downloaded jars"
    ).enum<RemapLevel>().multiple()

    override fun run() {

        val downloadedFiles = mutableListOf<File>()

        downloadedFiles += downloadFileHash(CraftLandiaVersionUtils.getMinecraftJarFile(version), false)

        if (includeMacrosMod) {
            val macrosModFile = CraftLandiaVersionUtils.getMacrosModFile(version)

            if (macrosModFile != null) {
                downloadedFiles += downloadFileHash(macrosModFile, true)
            } else {
                println("No macros mod file found for version $version")
            }
        }

        if (remapLevel.any { it > RemapLevel.NONE }) {
            val srcNamespace = "official"
            val destNamespace = "named"

            remapLevel.flatMap { it.mappingsContainer }
                .forEach { hasLandiaMappings ->
                    val mappingFile = MappingsUtils.getMergedCraftLandiaMappings(version, hasLandiaMappings)
                    var finalSuffix = destNamespace

                    // If we don't apply landia mappings, it means we are wanting to modify them on top of this file
                    // Thus we give it a different suffix (clientnamed vs named)
                    if (!hasLandiaMappings) finalSuffix = "client$finalSuffix"

                    downloadedFiles.forEachIndexed { i, file ->

                        RemapUtils.remapJar(
                            file,
                            File(file.parentFile, file.nameWithoutExtension + "-$finalSuffix.jar"),
                            RemapMappingFile(mappingFile, srcNamespace, destNamespace),
                            downloadedFiles.take(i)
                        )
                    }
                }

        }
    }

    private fun downloadFileHash(file: UpdateFile, useName: Boolean): File {
        val name = if (useName) file.file.substringAfterLast('/').replace(".litemod", ".jar") else (file.hash + ".jar")
        val output = File(name)

        output.writeBytes(URL(file.url).readBytes())

        println("Downloaded $name successfully")
        return output
    }
}