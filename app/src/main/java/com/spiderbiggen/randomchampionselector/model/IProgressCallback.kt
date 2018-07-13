package com.spiderbiggen.randomchampionselector.model

import android.support.annotation.StringRes
import com.spiderbiggen.randomchampionselector.R

interface IProgressCallback {

    fun onDownloadSuccess(count: Int, total: Int) =
            onProgressUpdate(Progress.DOWNLOAD_SUCCESS, count, total)

    fun onProgressUpdate(progress: Progress) = onProgressUpdate(progress, 0, 0)

    /**
     * Indicate to callback handler any progress update.
     *
     * @param type must be one of the defined constants.
     * @param progress     the current progress.
     * @param progressMax  the maximum progress.
     */
    fun onProgressUpdate(type: Progress, progress: Int, progressMax: Int)

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    fun finishExecution()

    enum class Progress constructor(val indeterminate: Boolean, @StringRes val stringResource: Int) {
        ERROR(true, R.string.progress_error),
        IDLE(true, R.string.progress_idle),
        CHECKING_VERSION(true, R.string.checking_version),
        PARSING_DATA(true, R.string.parsing_data),
        VERIFY_SUCCESS(false, R.string.progress_verified),
        DOWNLOAD_SUCCESS(false, R.string.progress_downloads)
    }
}
