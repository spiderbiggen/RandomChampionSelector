package com.spiderbiggen.randomchampionselector.interfaces

import androidx.annotation.StringRes
import com.spiderbiggen.randomchampionselector.R

interface IProgressCallback {

    fun update(progress: Int = 0, progressMax: Int = 0) =
        update(Progress.DOWNLOAD_SUCCESS, progress, progressMax)

    /**
     * Indicate to callback handler any progress update.
     *
     * @param type must be one of the defined constants.
     * @param progress     the current progress.
     * @param progressMax  the maximum progress.
     */
    fun update(type: Progress = Progress.DOWNLOAD_SUCCESS, progress: Int = 0, progressMax: Int = 0)

    enum class Progress constructor(val indeterminate: Boolean, @StringRes val stringResource: Int) {
        ERROR(true, R.string.progress_error),
        IDLE(true, R.string.progress_idle),
        CHECKING_VERSION(true, R.string.checking_version),
        PARSING_DATA(true, R.string.parsing_data),
        VERIFY_SUCCESS(false, R.string.progress_verified),
        DOWNLOAD_SUCCESS(false, R.string.progress_downloads)
    }
}
