package io.github.clmodding.cltools.utils

import net.fabricmc.mappingio.MappingReader
import net.fabricmc.mappingio.tree.MappingTree
import net.fabricmc.mappingio.tree.MemoryMappingTree
import net.fabricmc.tinyremapper.NonClassCopyMode
import net.fabricmc.tinyremapper.OutputConsumerPath
import net.fabricmc.tinyremapper.TinyRemapper
import java.io.File
import java.io.IOException


object RemapUtils {

    fun remapJar(
        inputFile: File,
        output: File,
        mappings: RemapMappingFile?,
        classpath: List<File>,
    ) {
        val remapper = TinyRemapper.newRemapper()
            .also { builder ->
                if (mappings == null) return@also

                builder.withMappings(
                    TinyRemapperMappingsHelper.create(
                        mappings.tree,
                        mappings.from,
                        mappings.to,
                        true
                    )
                )
                    .renameInvalidLocals(true)
                    .rebuildSourceFilenames(true)
                    .fixPackageAccess(true)
                    .ignoreConflicts(true)
            }.build()

        try {
            val input = inputFile.toPath()

            OutputConsumerPath.Builder(output.toPath()).build().use { outputConsumer ->
                outputConsumer.addNonClassFiles(input, NonClassCopyMode.SKIP_META_INF, remapper)
                remapper.readInputs(input)
                remapper.readClassPath(*classpath.map { it.toPath() }.toTypedArray())
                remapper.apply(outputConsumer)
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        } finally {
            remapper.finish()
        }
    }
}