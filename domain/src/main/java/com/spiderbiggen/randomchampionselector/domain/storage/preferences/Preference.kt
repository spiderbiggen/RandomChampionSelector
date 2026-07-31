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
    ) {
        /**
         * The locales DDragon serves champion data for, anything else answers with a 403.
         *
         * Mirrored by the `pref_language_values` resource array, which adds the display names.
         *
         * @see <a href="https://ddragon.leagueoflegends.com/cdn/languages.json">languages.json</a>
         */
        val supported = setOf(
            "ar_AE", "cs_CZ", "de_DE", "el_GR", "en_AU", "en_GB", "en_PH", "en_SG", "en_US",
            "es_AR", "es_ES", "es_MX", "fr_FR", "hu_HU", "id_ID", "it_IT", "ja_JP", "ko_KR",
            "pl_PL", "pt_BR", "ro_RO", "ru_RU", "th_TH", "tr_TR", "vi_VN", "zh_CN", "zh_MY",
            "zh_TW",
        )
    }

    companion object {
        private const val PREFIX = "pref"
    }
}