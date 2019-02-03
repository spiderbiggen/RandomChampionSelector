package com.spiderbiggen.randomchampionselector.util.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap.CompressFormat
import android.preference.PreferenceManager
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.interfaces.Contextual
import java.util.*

/**
 * Helper methods for access and manipulation of preferences.
 *
 * @author Stefan Breetveld
 */
object PreferenceManager : Contextual {
    private const val MILLIS_IN_MINUTE = 60_000
    private val resources = ResourceManager
    private lateinit var preferences: SharedPreferences

    override fun useContext(context: Context) {
        if (!this::preferences.isInitialized) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
        }
    }

    /**
     * Minimum amount of time between each synchronization in minutes.
     */
    private val syncDelay: Long
        get() = getString(R.string.pref_sync_frequency_key, R.string.pref_sync_frequency_default, "720").toLong()

    /**
     * The desired image quality 0-100 %
     */
    val quality: Int
        get() = getInt(R.string.pref_image_quality_key, R.integer.pref_image_quality_default)

    /**
     * A string representation of the preferred locale
     */
    val locale: String
        get() = getString(R.string.pref_language_key, R.string.pref_language_default, "en_US")

    /**
     * The desired [CompressFormat]
     */
    val compressFormat: CompressFormat
        get() = CompressFormat.valueOf(getString(R.string.pref_image_type_key, R.string.pref_image_type_default, "WEBP"))

    /**
     * If the app is outdated as specified by the set [syncDelay] and [lastSync].
     */
    val isOutdated: Boolean
        get() = syncDelay != -1L && lastSync == -1L || Date().after(Date(lastSync + syncDelay * MILLIS_IN_MINUTE))

    /**
     * The last performed update of all resources in millis since epoch
     */
    var lastSync: Long
        get() = getLong(R.string.pref_last_sync_key, R.integer.pref_last_sync_default)
        set(value) = setLongPreference(R.string.pref_last_sync_key, value)

    private fun setIntPreference(@StringRes pref: Int, value: Int) =
        preferences.edit { putInt(resources.getString(pref), value) }

    private fun setLongPreference(@StringRes pref: Int, value: Long) =
        preferences.edit { putLong(resources.getString(pref), value) }

    private fun getString(@StringRes pref: Int, @StringRes resource: Int, default: String): String =
        preferences.getString(resources.getString(pref), resources.getString(resource))
            ?: default

    private fun getInt(@StringRes pref: Int, @IntegerRes resource: Int): Int =
        preferences.getInt(resources.getString(pref), resources.getInt(resource))

    private fun getLong(@StringRes pref: Int, @IntegerRes resource: Int): Long =
        preferences.getLong(resources.getString(pref), resources.getInt(resource).toLong())

}
