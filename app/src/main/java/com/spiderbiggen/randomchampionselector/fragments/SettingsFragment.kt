package com.spiderbiggen.randomchampionselector.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.preference.SeekBarPreference
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.activities.SettingsActivity
import com.spiderbiggen.randomchampionselector.util.data.storage.file.FileStorage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.io.IOException

/**
 * Shows the settings menu.
 *
 * @author Stefan Breetveld
 */
class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preference, s)

        val barPreference =
            findPreference(getString(R.string.pref_image_quality_key)) as? SeekBarPreference
        bindIntSummary(barPreference, barPreference?.value ?: 0)

        bindStringSummary(findPreference(getString(R.string.pref_language_key)))
        bindStringSummary(findPreference(getString(R.string.pref_image_type_key)))
        bindStringSummary(findPreference(getString(R.string.pref_sync_frequency_key)))
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "pref_language" -> setRefresh()
            "pref_image_type", "pref_image_quality" -> {
                setRefresh()
                try {
                    FileStorage.deleteChampionImages()
                } catch (e: IOException) {
                    Log.e(TAG, "onSharedPreferenceChanged: ", e)
                }
            }
        }
    }

    private fun setRefresh() {
        (activity as? SettingsActivity)?.shouldRefresh()
    }

    override fun onResume() {
        super.onResume()
        getDefaultSharedPreferences(context)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        getDefaultSharedPreferences(context)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        private val TAG = this::class.java.simpleName
        const val FRAGMENT_TAG = "SETTINGS_FRAGMENT"

        /**
         * Binds a preference's summary to its value. When the preference's value is changed, its summary
         * is updated to reflect the value. The summary is also immediately updated upon calling this method.
         * The exact display format is dependent on the type of preference.
         *
         * @see Preference.OnPreferenceChangeListener
         */
        private fun bindStringSummary(preference: Preference?, defaultValue: String? = null) {
            if (preference == null) {
                return
            }
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceListener
            preferenceListener.onPreferenceChange(preference, getDefaultSharedPreferences(preference.context).getString(preference.key, defaultValue))
        }

        /**
         * Binds a preference's summary to its value. when the preference's value is changed, its summary
         * is updated to reflect the value. The summary is also immediately updated upon calling this method.
         * The exact display format is dependent on the type of preference.
         *
         * @param preference   The preference that needs to be bound
         * @param defaultValue The defaultValue of the preference
         * @see Preference.OnPreferenceChangeListener
         */
        private fun bindIntSummary(preference: Preference?, defaultValue: Int = 0) {
            if (preference == null) {
                return
            }
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceListener
            preferenceListener.onPreferenceChange(preference, getDefaultSharedPreferences(preference.context).getInt(preference.key, defaultValue))
        }

        private val preferenceListener: OnPreferenceChangeListener
            get() = OnPreferenceChangeListener { preference, value ->
                val stringValue = value.toString()
                preference.summary
                when (preference) {
                    is ListPreference -> {
                        val index = preference.findIndexOfValue(stringValue)
                        preference.setSummary(if (index >= 0) preference.entries[index] else null)
                    }
                    else -> preference.summary = stringValue
                }
                true
            }
    }
}