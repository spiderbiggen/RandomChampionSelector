package com.spiderbiggen.randomchampionselector.util.async;

import android.content.Context;

import com.spiderbiggen.randomchampionselector.R;

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */

public interface ProgressCallback {

    default void onError() {
        onProgressUpdate(Progress.ERROR);
    }

    default void onProgressUpdate(Progress progress) {
        onProgressUpdate(progress, 0, 0);
    }

    default void onDownloadSuccess(int count, int total) {
        onProgressUpdate(Progress.DOWNLOAD_SUCCESS, count, total);
    }

    /**
     * Indicate to callback handler any progress update.
     *
     * @param progressCode must be one of the defined constants.
     * @param progress     the current progress.
     * @param progressMax  the maximum progress.
     */
    void onProgressUpdate(Progress progressCode, int progress, int progressMax);

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishExecution();

    enum Progress {
        ERROR(true, R.string.progress_error),
        IDLE(true, R.string.progress_idle),
        DOWNLOAD_SUCCESS(false, R.string.progress_downloads),
        VERIFY_SUCCESS(false, R.string.progress_verified);

        private final boolean indeterminate;
        private final int stringResource;

        Progress(boolean indeterminate, int stringResource) {
            this.indeterminate = indeterminate;
            this.stringResource = stringResource;
        }

        /**
         * Gets indeterminate
         *
         * @return value of indeterminate
         */
        public boolean isIndeterminate() {
            return indeterminate;
        }

        /**
         * Gets stringResource
         *
         * @return value of stringResource
         */
        public int getStringResource() {
            return stringResource;
        }
    }
}
