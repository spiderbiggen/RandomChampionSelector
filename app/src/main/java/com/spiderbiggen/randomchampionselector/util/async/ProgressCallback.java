package com.spiderbiggen.randomchampionselector.util.async;

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
        ERROR(true),
        IDLE(true),
        CONNECT_SUCCESS(true),
        GET_INPUT_STREAM_SUCCESS(true),
        PROCESS_INPUT_STREAM_IN_PROGRESS(true),
        PROCESS_INPUT_STREAM_SUCCESS(true),
        DOWNLOAD_SUCCESS(false),
        VERIFY_SUCCESS(false);

        private final boolean indeterminate;

        Progress(boolean indeterminate) {
            this.indeterminate = indeterminate;
        }

        public boolean isIndeterminate() {
            return indeterminate;
        }
    }
}
