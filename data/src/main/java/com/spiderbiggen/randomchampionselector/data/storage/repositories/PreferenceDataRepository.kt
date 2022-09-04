package com.spiderbiggen.randomchampionselector.data.storage.repositories

import android.content.SharedPreferences
import androidx.core.content.edit
import com.spiderbiggen.randomchampionselector.domain.storage.models.CompressionFormat
import com.spiderbiggen.randomchampionselector.domain.storage.preferences.Preference
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import java.util.*
import javax.inject.Inject

/**
 * Helper methods for access and manipulation of preferences.
 *
 * @author Stefan Breetveld
 */
class PreferenceDataRepository @Inject constructor(
    private val preferences: SharedPreferences,
) : PreferenceRepository {

    /**
     * Minimum amount of time between each synchronization in minutes.
     */
    override var syncDelay: Long
        get() = getLong(Preference.SyncFrequency.key, Preference.SyncFrequency.default)
        set(value) = setLong(Preference.SyncFrequency.key, value)

    override var quality: Int
        get() = getInt(Preference.ImageQuality.key, Preference.ImageQuality.default)
        set(value) = setInt(Preference.ImageQuality.key, value.coerceIn(0..100))

    override var locale: String
        get() = getString(Preference.Language.key, Preference.Language.default)
        set(value) = setString(Preference.Language.key, value)

    override var compressFormat: CompressionFormat
        get() = CompressionFormat.valueOf(getString(Preference.ImageType.key, Preference.ImageType.default))
        set(value) = setString(Preference.ImageType.key, value.name)

    override var lastSync: Long
        get() = getLong(Preference.SyncLast.key, Preference.SyncLast.default)
        set(value) = setLong(Preference.SyncLast.key, value.coerceAtLeast(0))

    override val isOutdated: Boolean
        get() = syncDelay != -1L && lastSync == -1L || Date().after(Date(lastSync + syncDelay * MILLIS_IN_MINUTE))

    override var version: String?
        get() = preferences.getString(Preference.RiotVersion.key, Preference.RiotVersion.default)
        set(value) {
            value?.let { setString(Preference.RiotVersion.key, it) }
        }

    private fun setInt(pref: String, value: Int) =
        preferences.edit { putInt(pref, value) }

    private fun setLong(pref: String, value: Long) =
        preferences.edit { putLong(pref, value) }

    private fun setString(pref: String, value: String) =
        preferences.edit { putString(pref, value) }

    private fun getString(pref: String, default: String): String =
        preferences.getString(pref, default) ?: default

    private fun getInt(pref: String, default: Int): Int = runCatching { preferences.getInt(pref, default) }
        .recoverCatching { preferences.getString(pref, null)?.toInt() ?: default }
        .getOrDefault(default)

    private fun getLong(pref: String, default: Long): Long = runCatching { preferences.getLong(pref, default) }
        .recoverCatching { preferences.getString(pref, null)?.toLong() ?: default }
        .getOrDefault(default)


    companion object {
        private const val MILLIS_IN_MINUTE = 60_000
    }
}
