package io.github.clmodding.cltools.model.remap

enum class RemapLevel(val mappingsContainer: List<Boolean>) {
    NONE(emptyList()),
    CLIENTNAMED(listOf(false)),
    NAMED(listOf(true)),

}
