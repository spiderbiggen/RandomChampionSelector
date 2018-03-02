package com.spiderbiggen.randomchampionselector.ddragon;

import android.content.Context;
import android.net.ConnectivityManager;
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
import com.spiderbiggen.randomchampionselector.ddragon.callback.ImageCallback;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadImageTask;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.ImageType;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadChampionsTask;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.LoadImageTask;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DDragon {

    private static final String TAG = DDragon.class.getSimpleName();
    private static final int CAPACITY = 256;
    private static final String BASE_URL = "http://ddragon.leagueoflegends.com/cdn";
    private static final String VERSION = "8.4.1";
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 8, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(CAPACITY, true), new ThreadPoolExecutor.CallerRunsPolicy());

    private final String versionedBaseUrl;
    private final String version;
    private final Context context;
    private FileStorage storage;

    public DDragon(Context context, String version) {
        this.context = context;
        this.storage = new FileStorage(context);
        this.version = version;
        versionedBaseUrl = String.format("%s/%s", BASE_URL, version);
    }

    public DDragon(Context context) {
        this(context, VERSION);
    }

    public String getVersion() {
        return version;
    }

    public void getChampionList(DownloadCallback<List<Champion>> callback) {
        new DownloadChampionsTask(getActiveNetworkInfo(), callback).executeOnExecutor(executor, getChampionsUrl());
    }

    public void getChampionImage(Champion champion, @NonNull ImageType type, ImageCallback callback) {
        LoadImageTask.Entry entry;
        switch (type) {
            case SQUARE:
                entry = getChampionSquareEntry(champion, callback);
                break;
            case LOADING:
                entry = getChampionLoadingEntry(champion, callback);
                break;
            case SPLASH:
                entry = getChampionSplashEntry(champion, callback);
                break;
            default:
                return;
        }
        new LoadImageTask().executeOnExecutor(executor, entry);
    }

    @NonNull
    private String getChampionsUrl() {
        return String.format(versionedBaseUrl + "/data/en_GB/champion.json", version);
    }

    @NonNull
    private LoadImageTask.Entry getChampionSplashEntry(Champion champion, ImageCallback callback) {
        File dir = storage.getChampionSplashDir();
        File file = new File(dir, champion.getId() + ".jpg");
        return new LoadImageTask.Entry(file, champion, ImageType.SPLASH, callback);
    }

    @NonNull
    private LoadImageTask.Entry getChampionLoadingEntry(Champion champion, ImageCallback callback) {
        File dir = storage.getChampionLoadingDir();
        File file = new File(dir, champion.getId() + ".jpg");
        return new LoadImageTask.Entry(file, champion, ImageType.LOADING, callback);
    }

    @NonNull
    private LoadImageTask.Entry getChampionSquareEntry(Champion champion, ImageCallback callback) {
        File dir = storage.getChampionSquareDir();
        File file = new File(dir, champion.getId() + ".jpg");
        return new LoadImageTask.Entry(file, champion, ImageType.SQUARE, callback);
    }

    public DownloadImageTask downloadAllimages(List<Champion> champions, DownloadCallback<DownloadImageTask.Entry[]> callback) {
        List<DownloadImageTask.Entry> entryList = new ArrayList<>();
        for (Champion champion : champions) {
            entryList.add(getDownloadEntry(getChampionLoadingEntry(champion, null)));
            entryList.add(getDownloadEntry(getChampionSplashEntry(champion, null)));
            entryList.add(getDownloadEntry(getChampionSquareEntry(champion, null)));
        }
        DownloadImageTask.Entry[] entries = new DownloadImageTask.Entry[entryList.size()];
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        DownloadImageTask task = new DownloadImageTask(activeNetworkInfo, callback);
        task.executeOnExecutor(executor, entryList.toArray(entries));
        return task;
    }

    private DownloadImageTask.Entry getDownloadEntry(LoadImageTask.Entry entry) {
        return new DownloadImageTask.Entry(getChampionUrl(entry.getChampion(), entry.getType()), entry.getFile());
    }

    private String getChampionUrl(Champion champion, ImageType type) {
        switch (type) {
            case SQUARE:
                return getChampionSquareUrl(champion);
            case LOADING:
                return getChampionLoadingUrl(champion);
            case SPLASH:
                return getChampionSplashUrl(champion);
            default:
                return null;
        }
    }

    private String getChampionSplashUrl(Champion champion) {
        String champ = champion.getId();
        return String.format("%s/img/champion/splash/%s_0.jpg", BASE_URL, champ);
    }

    private String getChampionLoadingUrl(Champion champion) {
        String champ = champion.getId();
        return String.format("%s/img/champion/loading/%s_0.jpg", BASE_URL, champ);
    }

    private String getChampionSquareUrl(Champion champion) {
        String champ = champion.getId();
        return String.format("%s/img/champion/%s.png", versionedBaseUrl, champ);
    }

    @NonNull
    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            throw new RuntimeException("No network info could be found");
        }
        return cm.getActiveNetworkInfo();
    }
}
