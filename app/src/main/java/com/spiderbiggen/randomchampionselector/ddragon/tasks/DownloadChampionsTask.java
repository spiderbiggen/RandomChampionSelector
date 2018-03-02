package com.spiderbiggen.randomchampionselector.ddragon.tasks;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadTask;
import com.spiderbiggen.randomchampionselector.util.internet.HttpRequest;
import com.spiderbiggen.randomchampionselector.util.async.Progress;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadChampionsTask extends DownloadTask<String, List<Champion>> {

    private static final String TAG = DownloadChampionsTask.class.getSimpleName();

    public DownloadChampionsTask(@NonNull NetworkInfo info, DownloadCallback<List<Champion>> callback) {
        super(info, callback);
    }

    @Override
    protected List<Champion> doInBackground(String... strings) {
        List<Champion> result = null;
        Log.d(TAG, "doInBackground: " + Arrays.toString(strings));
        if (strings != null && strings.length > 0) {
            try {
                String string = strings[0];
                if (!isCancelled()) {
                    Log.d(TAG, "doInBackground: " + string);
                    String resultString = downloadUrl(string);
                    if (resultString != null) {
                        result = parseChampions(resultString);
                    } else {
                        throw new IOException("No response received.");
                    }
                }
            } catch (Exception e) {
                exception = e;
            }
        }
        if (result != null) {
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.addChampions(result);
            publishProgress(Progress.DOWNLOAD_SUCCESS, 100);
        }
        return result;
    }

    private List<Champion> parseChampions(String json) {
        JsonDeserializer<List<Champion>> deserializer = getChampionsDeserializer();
        GsonBuilder builder = new GsonBuilder();
        Type t = new TypeToken<Collection<Champion>>() {}.getType();
        builder.registerTypeAdapter(t, deserializer);
        Gson gson = builder.create();
        return gson.fromJson(json, t);
    }

    @NonNull
    private JsonDeserializer<List<Champion>> getChampionsDeserializer() {
        return new JsonDeserializer<List<Champion>>() {
            @Override
            public List<Champion> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                JsonObject object = json.getAsJsonObject();
                object = object.getAsJsonObject("data");
                List<Champion> list = new ArrayList<>();
                Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
                int total = entries.size();
                int count = 0;
                for (Map.Entry<String, JsonElement> entry : entries) {
                    Champion champion = context.deserialize(entry.getValue(), Champion.class);
                    updateProgress(Progress.DOWNLOAD_SUCCESS, ++count, total);
                    if (champion != null) {
                        list.add(champion);
                    }
                }
                return list;
            }
        };
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
}
