package com.spiderbiggen.randomchampionselector.presentation.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.google.android.material.snackbar.Snackbar
import com.spiderbiggen.randomchampionselector.presentation.R
import com.spiderbiggen.randomchampionselector.domain.storage.preferences.Preference as AppPreference

/**
 * Shows the settings menu.
 *
 * Changing a preference only marks the cached data as invalidated, the actual sync is triggered by
 * [SettingsFragment] once the user leaves the screen.
 *
 * @author Stefan Breetveld
 */
class SettingsPreferenceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    private val viewModel by viewModels<SettingsViewModel>(ownerProducer = { requireParentFragment() })

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preference, rootKey)

        bindListSummary(AppPreference.Language.key)
        bindListSummary(AppPreference.ImageType.key)
        bindListSummary(AppPreference.SyncFrequency.key)
        findPreference<SeekBarPreference>(AppPreference.ImageQuality.key)?.showSeekBarValue = true
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val firstChange = when (key) {
            AppPreference.Language.key -> viewModel.onLanguageChanged()
            AppPreference.ImageType.key, AppPreference.ImageQuality.key -> viewModel.onImagePreferenceChanged()
            else -> return
        }
        if (!firstChange) return
        view?.let { Snackbar.make(it, R.string.settings_sync_pending, Snackbar.LENGTH_SHORT).show() }
    }

    /** Show the selected entry as the preference summary. */
    private fun bindListSummary(key: String) {
        findPreference<ListPreference>(key)?.summaryProvider =
            ListPreference.SimpleSummaryProvider.getInstance()
    }
}
