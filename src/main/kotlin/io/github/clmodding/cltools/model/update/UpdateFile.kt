package io.github.clmodding.cltools.model.update

internal data class UpdateFile(
    val file: String,
    val hash: String,
    val force: Boolean
) {
    val url: String
        get() {
            return (CraftLandiaUpdatesModel.INSTANCE.base.removeSuffix("/") + "/" + file)
        }
}