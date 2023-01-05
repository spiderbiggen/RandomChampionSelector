package com.spiderbiggen.randomchampionselector.domain.storage.preferences

import com.spiderbiggen.randomchampionselector.domain.storage.models.CompressionFormat

sealed class Preference<T>(val key: String, val default: T) {

    object ImageType : Preference<String>(
        key = "${PREFIX}_image_type",
        default = CompressionFormat.WEBP_LOSSY.name
    )

    object ImageQuality : Preference<Int>(
        key = "${PREFIX}_image_quality",
        default = 89
    )

    object RiotVersion : Preference<String?>(
        key = "${PREFIX}_riot_version",
        default = null
    )

    object SyncFrequency : Preference<Long>(
        key = "${PREFIX}_sync_frequency",
        default = 720L
    )

    object SyncLast : Preference<Long>(
        key = "${PREFIX}_last_sync",
        default = 0L
    )

    object Language : Preference<String>(
        key = "${PREFIX}_language",
        default = "en_US"
    )

    companion object {
        private const val PREFIX = "pref"
    }
}