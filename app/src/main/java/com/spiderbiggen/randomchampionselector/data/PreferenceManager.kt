package com.spiderbiggen.randomchampionselector.data

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap.CompressFormat
import android.preference.PreferenceManager
import androidx.annotation.IntegerRes
import androidx.annotation.StringRes
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.model.IRequiresContext
import com.spiderbiggen.randomchampionselector.presenters.LoaderPresenter
import java.util.*

object PreferenceManager : IRequiresContext {

    private val resources = ResourceManager
    private lateinit var preferences: SharedPreferences

    override fun useContext(context: Context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context.applicationContext)
    }

    override fun hasContext(): Boolean = this::preferences.isInitialized

    val syncTime: Long
        get() = getString(R.string.pref_title_sync_frequency, R.string.pref_sync_frequency_default).toLong()

    val quality: Int
        get() = getInt("pref_image_quality", R.integer.pref_image_quality_default, 85)

    val locale: String
        get() = getString("pref_language", R.string.pref_language_default, "en_US")

    val compressFormat: CompressFormat
        get() = CompressFormat.valueOf(getString("pref_image_type", R.string.pref_image_type_default, "WEBP"))

    val isOutdated: Boolean
        get() = syncTime != -1L && lastSync == -1L || Date().after(Date(lastSync + syncTime * LoaderPresenter.MILLIS_IN_MINUTE))

    var lastSync: Long
        get() = getLong(R.string.pref_last_sync_key, R.integer.pref_last_sync_default)
        set(value) = setLongPreference(R.string.pref_last_sync_key, value)

    private fun setIntPreference(@StringRes pref: Int, value: Int) = setIntPreference(resources.getString(pref), value)

    private fun setIntPreference(pref: String, value: Int) = preferences.edit().putInt(pref, value).apply()

    private fun setLongPreference(@StringRes pref: Int, value: Long) = setLongPreference(resources.getString(pref), value)

    private fun setLongPreference(pref: String, value: Long) = preferences.edit().putLong(pref, value).apply()


    // Get string from preferences
    private fun getString(pref: String, default: String): String =
            preferences.getString(pref, default) ?: default

    private fun getString(@StringRes pref: Int, @StringRes resource: Int): String =
            getString(resources.getString(pref), resources.getString(resource))

    private fun getString(pref: String, @StringRes resource: Int, default: String): String =
            getString(pref, resources.getString(resource, default))

    private fun getString(@StringRes pref: Int, @StringRes resource: Int, default: String): String =
            getString(resources.getString(pref), resource, default)

    // Get int from preferences
    private fun getInt(pref: String, default: Int): Int =
            preferences.getInt(pref, default)

    private fun getInt(@StringRes pref: Int, @IntegerRes resource: Int): Int =
            getInt(resources.getString(pref), resources.getInt(resource))

    private fun getInt(pref: String, @IntegerRes resource: Int, default: Int): Int =
            getInt(pref, resources.getInt(resource, default))

    private fun getInt(@StringRes pref: Int, @IntegerRes resource: Int, default: Int): Int =
            getInt(resources.getString(pref), resource, default)

    // Get long from preferences
    private fun getLong(pref: String, default: Long): Long =
            preferences.getLong(pref, default)

    private fun getLong(@StringRes pref: Int, @IntegerRes resource: Int): Long =
            getLong(resources.getString(pref), resources.getLong(resource))

    private fun getLong(pref: String, @IntegerRes resource: Int, default: Long): Long =
            getLong(pref, resources.getLong(resource, default))

    private fun getLong(@StringRes pref: Int, @IntegerRes resource: Int, default: Long): Long =
            getLong(resources.getString(pref), resource, default)

}
