package com.spiderbiggen.randomchampionselector.ddragon.tasks;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadTask;
import com.spiderbiggen.randomchampionselector.util.internet.HttpRequest;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;

/**
 * Created on 3-3-2018.
 *
 * @author Stefan Breetveld
 */

public class DownloadJsonTask<T> extends DownloadTask<String, T> {

    private static final String TAG = DownloadJsonTask.class.getSimpleName();
    protected final Type type;

    public DownloadJsonTask(@NonNull NetworkInfo info, DownloadCallback<T> callback, Class<? super T> type) {
        super(info, callback);
        this.type = type;
    }

    @Override
    protected T doInBackground(String... strings) {
        T result = null;
        if (strings != null && strings.length > 0) {
            try {
                String string = strings[0];
                if (!isCancelled()) {
                    String resultString = downloadUrl(string);
                    if (resultString != null) {
                        result = this.getGson().fromJson(resultString, type);
                    } else {
                        throw new IOException("No response received.");
                    }
                }
            } catch (Exception e) {
                exception = e;
            }
        }
        return result;
    }

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    private String downloadUrl(String url) throws IOException {
        try (HttpRequest request = new HttpRequest(url).connect()) {
            updateProgress(Progress.CONNECT_SUCCESS, 0, 0);
            int responseCode = request.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            // Retrieve the response body as an InputStream.
            updateProgress(Progress.GET_INPUT_STREAM_SUCCESS, 0, 0);
            return request.getResponse();
        }
    }

    protected Gson getGson() {
        return new Gson();
    }
}
