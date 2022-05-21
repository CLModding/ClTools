package io.github.clmodding.cltools.mappings

import io.github.clmodding.cltools.model.CraftLandiaVersionUtils
import io.github.clmodding.cltools.model.version.ClientVersion
import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.MappingVisitor
import net.fabricmc.mappingio.adapter.MappingNsCompleter
import net.fabricmc.mappingio.adapter.MappingNsRenamer
import net.fabricmc.mappingio.tree.MappingTree
import net.fabricmc.mappingio.tree.MemoryMappingTree
import java.io.File
import java.net.URL

object MappingsUtils {

    val mappingsCacheDir = File(".mappings").also { it.mkdirs() }

    private fun getMappingFileAsTree(file: MappingFile): MemoryMappingTree {
        val mappingFile = File(mappingsCacheDir, file.url.substringAfterLast("/"))
        val mappingFilePath = mappingFile.toPath()

        if (!mappingFile.exists()) {
            URL(file.url).readBytes().also {
                mappingFile.writeBytes(it)
            }
        }

        val resultingMemoryTree = MemoryMappingTree()
        var visitor: MappingVisitor = resultingMemoryTree
        if (file.renamedSrc != null) {
            visitor = MappingNsRenamer(visitor, mapOf("official" to file.renamedSrc))
        }
        MappingReader.read(mappingFilePath, visitor)

        return resultingMemoryTree
    }

    private fun getCraftLandiaMappingsUrls(
        versionHash: String,
        version: ClientVersion,
        hasCraftLandiaMappings: Boolean
    ): List<MappingFile> {
        val mappingsName = version.mappingsName
            ?: throw IllegalArgumentException("Version ${version.name} does not have mappings name")

        return buildList {
            add(MappingFile("https://raw.githubusercontent.com/CLModding/intermediary/main/intermediary/${versionHash}.tiny")) // Official CL -> intermediary
            add(MappingFile("https://raw.githubusercontent.com/CLModding/intermediary/main/intermediary-vanilla.tiny")) // intermediary -> mojang official

            add(
                MappingFile(
                    "https://raw.githubusercontent.com/NickAcPT/LightCraftMappings/main/1.5.2/mappings-official-srg-named.tiny2",
                    "mojang_official"
                )
            ) // mojang official -> named
            add(MappingFile("https://raw.githubusercontent.com/NickAcPT/LightCraftMappings/main/1.5.2/mappings-extra.tinyv2")) // named -> named


            if (hasCraftLandiaMappings)
                add(MappingFile("https://raw.githubusercontent.com/CLModding/mappings-new/master/$mappingsName/craftlandia.tiny")) // intermediary -> named
        }
    }

    fun getMergedCraftLandiaMappings(version: ClientVersion, hasCraftLandiaMappings: Boolean): MappingTree {
        val memoryTree = MemoryMappingTree()
        val finalTree = MemoryMappingTree()

        val hash = CraftLandiaVersionUtils.getMinecraftJarFile(version).hash
        val files = getCraftLandiaMappingsUrls(hash, version, hasCraftLandiaMappings)

        files.forEach {
            getMappingFileAsTree(it).apply {
                this.accept(memoryTree)
            }
        }

        val namespaces = memoryTree.dstNamespaces.reversed() + memoryTree.srcNamespace
        memoryTree.accept(
            MappingNsCompleter(finalTree,
                namespaces
                    .asSequence()
                    .mapIndexed { i, ns -> ns to (namespaces.getOrNull(i + 1) ?: namespaces.last()) }
                    .filter { it.first != it.second }
                    .toMap(),
                true)

        )

        return finalTree
    }

}