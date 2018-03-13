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
        ERROR(-1),
        IDLE(0),
        CONNECT_SUCCESS(1),
        GET_INPUT_STREAM_SUCCESS(2),
        PROCESS_INPUT_STREAM_IN_PROGRESS(3),
        PROCESS_INPUT_STREAM_SUCCESS(4),
        DOWNLOAD_SUCCESS(10);

        private final int priority;

        Progress(int priority) {
            this.priority = priority;
        }

        public int getPriority() {
            return priority;
        }
    }
}
