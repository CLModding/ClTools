package io.github.clmodding.cltools.model

import io.github.clmodding.cltools.model.update.CraftLandiaUpdatesModel
import io.github.clmodding.cltools.model.update.UpdateFile
import io.github.clmodding.cltools.model.version.ClientVersion

internal object CraftLandiaVersionUtils {

    fun getMinecraftJarFile(version: ClientVersion): UpdateFile {
        return getUpdateFile(version, "${version.downloadedFile}.jar")
    }

    fun getMacrosModFile(version: ClientVersion): UpdateFile? {
        return version.macrosModFile?.let { getUpdateFile(version, it) }
    }

    private fun getUpdateFile(version: ClientVersion, name: String): UpdateFile {
        val updates = CraftLandiaUpdatesModel.INSTANCE
        val list = updates.files[version.versionIdentifier]

        return list?.firstOrNull {
            it.file.endsWith(name)
        } ?: throw Exception("Unable to find launcher version jar with identifier \"${version.downloadedFile}\"")
    }

}