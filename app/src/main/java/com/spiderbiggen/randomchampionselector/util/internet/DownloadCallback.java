package com.spiderbiggen.randomchampionselector.util.internet;

import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

public interface DownloadCallback<T> extends ProgressCallback {

    /**
     * Indicates that the caller has encountered an exception.
     *
     * @param exception thrown exception.
     */
    void throwException(Exception exception);

    /**
     * Indicates that the callback handler needs to update its appearance or information based on
     * the result of the task. Expected to be called from the main thread.
     */
    void updateFromDownload(T result);

    /**
     * Indicates that the download operation has finished. This method is called even if the
     * download hasn't completed successfully.
     */
    void finishExecution();

}
