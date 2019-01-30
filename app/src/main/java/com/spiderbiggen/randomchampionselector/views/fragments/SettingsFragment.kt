package com.spiderbiggen.randomchampionselector.views.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.data.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.views.activities.SettingsActivity
import java.io.IOException

/**
 * Shows the settings menu.
 */
class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preference, s)

        val prefLanguage = findPreference("pref_language") as ListPreference
        val prefImageType = findPreference("pref_image_type") as ListPreference
        val preference = findPreference("pref_image_quality") as SeekBarPreference

        bindPreferenceSummaryToValueString(prefLanguage)
        bindPreferenceSummaryToValueString(prefImageType)
        bindPreferenceSummaryToValueInteger(preference, preference.value)
        bindPreferenceSummaryToValueString(findPreference("sync_frequency"))
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
        PreferenceManager.getDefaultSharedPreferences(context)
                .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(context)
                .unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        private val TAG = SettingsFragment::class.java.simpleName
        const val FRAGMENT_TAG = "SETTINGS_FRAGMENT"

        /**
         * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary
         * (line of text below the preference title) is updated to reflect the value. The summary is also immediately
         * updated upon calling this method. The exact display format is dependent on the type of preference.
         *
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValueString(preference: Preference, defaultValue: String = "") {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    getDefaultSharedPreferences(preference.context).getString(preference.key, defaultValue))
        }

        /**
         * Binds a preference's summary to its value. More specifically, when the preference's value is changed, its summary
         * (line of text below the preference title) is updated to reflect the value. The summary is also immediately
         * updated upon calling this method. The exact display format is dependent on the type of preference.
         *
         * @param preference   The preference that needs to be bound
         * @param defaultValue The defaultValue of the preference
         * @see .sBindPreferenceSummaryToValueListener
         */
        private fun bindPreferenceSummaryToValueInteger(preference: Preference, defaultValue: Int = 0) {
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = sBindPreferenceSummaryToValueListener

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    getDefaultSharedPreferences(preference.context).getInt(preference.key, defaultValue))
        }

        private val sBindPreferenceSummaryToValueListener: Preference.OnPreferenceChangeListener
            get() = Preference.OnPreferenceChangeListener { preference, value ->
                    val stringValue = value.toString()

                    when (preference) {
                        is ListPreference -> {
                            val index = preference.findIndexOfValue(stringValue)
                            preference.setSummary(if (index >= 0) preference.entries[index] else null)
                        }
//                        is SeekBarPreference -> preference.setSummary((preference as SeekBarPreference).summary)
                        else -> preference.summary = stringValue
                    }
                    true
                }
    }
}