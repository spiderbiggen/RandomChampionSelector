package com.spiderbiggen.randomchampionselector.domain.storage.repositories

import com.spiderbiggen.randomchampionselector.domain.storage.models.CompressionFormat

interface PreferenceRepository {
    /**
     * Minimum amount of time between each synchronization in minutes.
     */
    var syncDelay: Long

    /**
     * The desired image quality 0-100 %
     */
    var quality: Int

    /**
     * A string representation of the preferred locale
     */
    var locale: String

    /**
     * The desired [CompressionFormat]
     */
    var compressFormat: CompressionFormat

    /**
     * If the app is outdated as specified by the set [syncDelay] and [lastSync].
     */
    val isOutdated: Boolean

    /**
     * The last performed update of all resources in millis since epoch
     */
    var lastSync: Long

    /**
     * The last known League of Legends version
     */
    var version: String?
}