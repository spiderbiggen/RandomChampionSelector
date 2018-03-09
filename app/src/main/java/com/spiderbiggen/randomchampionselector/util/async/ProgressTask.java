package com.spiderbiggen.randomchampionselector.util.async;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created on 1-3-2018.
 *
 * @author Stefan Breetveld
 */
public abstract class ProgressTask<S, T> extends AsyncTask<S, Integer, T> {

    private static final String TAG = ProgressTask.class.getSimpleName();
    protected Exception exception;
    private int lastProgressId = -1;
    private ProgressCallback mCallback;

    protected <R extends ProgressCallback> void setCallback(R callback) {
        mCallback = callback;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        Log.d(TAG, "onProgressUpdate() called with: values = [" + values + "]");
        if (mCallback != null && values != null && values.length >= 3) {
            mCallback.onProgressUpdate(values[0], values[1], values[2]);
        }
    }

    protected void updateProgress(int progressId, int progress, int progressMax) {
        if (progressId == Progress.ERROR || progressId >= lastProgressId) {
            publishProgress(progressId, progress, progressMax);
            if (progressId != Progress.ERROR) {
                lastProgressId = progressId;
            }
        }
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(T result) {
        if (mCallback != null) {
            mCallback.finishExecution();
        }
    }
}
