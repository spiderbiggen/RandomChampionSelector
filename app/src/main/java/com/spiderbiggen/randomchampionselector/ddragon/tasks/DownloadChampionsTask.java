package com.spiderbiggen.randomchampionselector.ddragon.tasks;

import android.net.NetworkInfo;
import android.support.annotation.NonNull;

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
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DownloadChampionsTask extends DownloadJsonTask<List<Champion>> {

    private static final String TAG = DownloadChampionsTask.class.getSimpleName();

    public DownloadChampionsTask(@NonNull NetworkInfo info, DownloadCallback<List<Champion>> callback) {
        super(info, callback, new TypeToken<List<Champion>>() {}.getRawType());
    }

    @Override
    protected List<Champion> doInBackground(String... strings) {
        List<Champion> result = super.doInBackground(strings);
        if (result != null) {
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            databaseManager.addChampions(result);
        }
        return result;
    }

    @Override
    protected Gson getGson() {
        JsonDeserializer<List<Champion>> deserializer = getChampionsDeserializer();
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(type, deserializer);
        return builder.create();
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
                int count = 0, total = entries.size();
                for (Map.Entry<String, JsonElement> entry : entries) {
                    Champion champion = context.deserialize(entry.getValue(), Champion.class);
                    if (champion != null) {
                        list.add(champion);
                    }
                    updateProgress(Progress.DOWNLOAD_SUCCESS, ++count, total);
                }
                return list;
            }
        };
    }

}
