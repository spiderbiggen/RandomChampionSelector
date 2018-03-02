package com.spiderbiggen.randomchampionselector.util.internet;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.spiderbiggen.randomchampionselector.util.async.ProgressTask;

public abstract class DownloadTask<S, T> extends ProgressTask<S, T> {

    private static final String TAG = DownloadTask.class.getSimpleName();
    private DownloadCallback<T> mCallback;
    private NetworkInfo networkInfo;

    public DownloadTask(@NonNull NetworkInfo info, DownloadCallback<T> callback) {
        setCallback(callback);
        this.networkInfo = info;
    }

    public void setCallback(DownloadCallback<T> callback) {
        super.setCallback(callback);
        mCallback = callback;
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    @Override
    protected void onPreExecute() {
        if (networkInfo == null || !networkInfo.isConnected() || (networkInfo.getType() != ConnectivityManager.TYPE_WIFI && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
            this.onNoInternet();
        }
    }

    protected void onNoInternet() {
        // If no connectivity, cancel task and update Callback with null data.
        mCallback.updateFromDownload(null);
        cancel(true);
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    @Override
    protected void onPostExecute(T result) {
        if (mCallback != null) {
            if (exception != null) {
                mCallback.throwException(exception);
            }
            if (result != null) {
                mCallback.updateFromDownload(result);
            }
        }
        super.onPostExecute(result);
    }

}
