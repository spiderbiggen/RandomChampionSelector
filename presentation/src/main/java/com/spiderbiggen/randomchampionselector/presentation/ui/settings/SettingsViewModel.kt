package com.spiderbiggen.randomchampionselector.presentation.ui.settings

import androidx.lifecycle.ViewModel

/**
 * Tracks which cached data was invalidated while the user was in the settings screen, so a single
 * sync can be triggered when they leave instead of one per changed preference.
 */
class SettingsViewModel : ViewModel() {

    /** The cached images no longer match the configured format or quality. */
    var clearImages = false
        private set

    /** The champion data itself is stale, for example after switching language. */
    var refreshChampionData = false
        private set

    val syncRequired: Boolean
        get() = clearImages || refreshChampionData

    /** Returns true the first time, so the user is only told about the pending sync once. */
    fun onImagePreferenceChanged(): Boolean = !clearImages.also { clearImages = true }

    /**
     * A different locale means the champion texts have to be fetched again, the images are locale
     * independent and can stay.
     *
     * @see onImagePreferenceChanged for the return value.
     */
    fun onLanguageChanged(): Boolean = !refreshChampionData.also { refreshChampionData = true }
}
