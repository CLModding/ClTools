package io.github.clmodding.cltools.utils

import net.fabricmc.mappingio.tree.MappingTree

data class RemapMappingFile(val tree: MappingTree, val from: String, val to: String) {
    fun reverse(): RemapMappingFile {
        return this.copy(from = to, to = from)
    }
}