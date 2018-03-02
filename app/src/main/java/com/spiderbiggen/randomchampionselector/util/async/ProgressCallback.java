package com.spiderbiggen.randomchampionselector.util.async;

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */

public interface ProgressCallback {

    /**
     * Indicate to callback handler any progress update.
     *
     * @param progressCode must be one of the constants defined in DownloadCallback.Progress.
     * @param progress     the current progress.
     * @param progressMax  the maximum progress.
     */
    void onProgressUpdate(int progressCode, int progress, int progressMax);

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishExecution();
}
