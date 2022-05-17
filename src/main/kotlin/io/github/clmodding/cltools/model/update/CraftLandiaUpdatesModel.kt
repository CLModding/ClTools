package io.github.clmodding.cltools.model.update

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.net.URL

internal data class CraftLandiaUpdatesModel(
    val base: String,
    val updater: UpdaterInfo,
    val files: Map<String, List<UpdateFile>>
) {

    companion object {
        private const val updatesUrl = "https://updater.craftlandia.com.br/v3/updates.json"

        val INSTANCE by lazy {
            fromJson(URL(updatesUrl).readText())
        }

        private val craftLandiaMapper = jacksonObjectMapper().apply {
            propertyNamingStrategy = PropertyNamingStrategies.LOWER_CAMEL_CASE
            setSerializationInclusion(JsonInclude.Include.NON_NULL)
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }

        private fun fromJson(json: String) = craftLandiaMapper.readValue<CraftLandiaUpdatesModel>(json)
    }
}

