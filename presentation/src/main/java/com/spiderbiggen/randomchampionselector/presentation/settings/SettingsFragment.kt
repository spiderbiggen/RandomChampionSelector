package com.spiderbiggen.randomchampionselector.presentation.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.Preference.OnPreferenceChangeListener
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager.getDefaultSharedPreferences
import androidx.preference.SeekBarPreference
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.presentation.activities.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import java.io.IOException
import javax.inject.Inject
import com.spiderbiggen.randomchampionselector.domain.storage.preferences.Preference as AppPreference

/**
 * Shows the settings menu.
 *
 * @author Stefan Breetveld
 */
@FlowPreview
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    internal lateinit var fileRepository: FileRepository

    @Inject
    internal lateinit var preferenceRepository: PreferenceRepository

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        setPreferencesFromResource(R.xml.preference, s)

        val barPreference = findPreference(AppPreference.ImageQuality.key) as? SeekBarPreference
        bindIntSummary(barPreference, barPreference?.value ?: AppPreference.ImageQuality.default)
        bindStringSummary(findPreference(AppPreference.Language.key), AppPreference.Language.default)
        bindStringSummary(findPreference(AppPreference.ImageType.key), AppPreference.ImageType.default)
        bindLongSummary(findPreference(AppPreference.SyncFrequency.key), AppPreference.SyncFrequency.default)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        when (key) {
            "pref_language" -> setRefresh()
            "pref_image_type", "pref_image_quality" -> {
                setRefresh()
                try {
                    fileRepository.deleteChampionImages()
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
        getDefaultSharedPreferences(requireContext())
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        getDefaultSharedPreferences(requireContext())
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {

        private val TAG = this::class.java.simpleName
        const val FRAGMENT_TAG = "SETTINGS_FRAGMENT"
        
        private fun bindStringSummary(preference: Preference?, defaultValue: String? = null) {
            if (preference == null) {
                return
            }
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceListener
            preferenceListener.onPreferenceChange(preference, getDefaultSharedPreferences(preference.context).getString(preference.key, defaultValue))
        }

        private fun bindIntSummary(preference: Preference?, defaultValue: Int = 0) {
            if (preference == null) {
                return
            }
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceListener.apply {
                onPreferenceChange(preference, getDefaultSharedPreferences(preference.context).getInt(preference.key, defaultValue))
            }
        }

        private fun bindLongSummary(preference: Preference?, defaultValue: Long = 0L) {
            if (preference == null) {
                return
            }
            // Set the listener to watch for value changes.
            preference.onPreferenceChangeListener = preferenceListener.apply {
                onPreferenceChange(preference, getDefaultSharedPreferences(preference.context).getLong(preference.key, defaultValue))
            }
        }

        private val preferenceListener: OnPreferenceChangeListener
            get() = OnPreferenceChangeListener { preference, value ->
                val stringValue = value.toString()
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