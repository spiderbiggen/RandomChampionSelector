package com.spiderbiggen.randomchampionselector.ddragon;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadChampionsTask;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadImageTask;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadJsonTask;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;

import java.io.File;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


public class DDragon {

    private static final String TAG = DDragon.class.getSimpleName();
    private static final int CAPACITY = 256;
    private static final String BASE_URL = "http://ddragon.leagueoflegends.com";
    private static final String API_URL = BASE_URL + "/api";
    private static final String CDN_URL = BASE_URL + "/cdn";
    private static final String DEFAULT_VERSION = "8.4.1"; // Default version if versions endpoint fails
    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(0, 8, 1000L, TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<Runnable>(CAPACITY, true), new ThreadPoolExecutor.CallerRunsPolicy());
    private static final AtomicReference<String> version = new AtomicReference<>(DEFAULT_VERSION);

    private final Context context;

    public DDragon(Context context) {
        this.context = context;
    }

    public void updateVersion(ProgressCallback callback) {
        new DownloadJsonTask<>(getActiveNetworkInfo(), new VersionCallback(callback), String[].class)
                .executeOnExecutor(executor, API_URL + "/versions.json");
    }

    public String getVersion() {
        return version.get();
    }

    private String getVersionedCDNUrl() {
        return String.format("%s/%s", CDN_URL, version.get());
    }

    public void getChampionList(DownloadCallback<List<Champion>> callback) {
        new DownloadChampionsTask(getActiveNetworkInfo(), callback).executeOnExecutor(executor, getChampionsUrl());
    }

    public Bitmap getChampionBitmap(Champion champion, @NonNull ImageType type) {
        File file = getChampionFile(champion, type);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        if (bitmap == null && file.delete()) {
            downloadImage(champion, type);
        }
        return bitmap;
    }

    @NonNull
    private File getChampionFile(@NonNull Champion champion, @NonNull ImageType type) {
        return new FileStorage(context).getChampionImageFile(champion, type);
    }

    @NonNull
    private String getChampionsUrl() {
        return String.format("%s/data/en_GB/champion.json", getVersionedCDNUrl());
    }

    public void downloadAllImages(List<Champion> champions, DownloadCallback<DownloadImageTask.Entry[]> callback) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        AtomicInteger count = new AtomicInteger();
        ImageType[] types = ImageType.values();
        int typesLength = types.length;
        int total = champions.size() * typesLength;
        for (int i = 0, championsSize = champions.size(); i < championsSize; i++) {
            Champion champion = champions.get(i);
            DownloadImageTask.Entry[] entryList = new DownloadImageTask.Entry[typesLength];
            for (int i1 = 0; i1 < typesLength; i1++) {
                ImageType type = types[i1];
                entryList[i1] = getDownloadImageEntry(champion, type);
            }
            DownloadImageTask task = new DownloadImageTask(activeNetworkInfo, callback, count, total);
            task.executeOnExecutor(executor, entryList);
        }
    }

    private void downloadImage(Champion champion, ImageType imageType) {
        NetworkInfo activeNetworkInfo = getActiveNetworkInfo();
        DownloadImageTask task = new DownloadImageTask(activeNetworkInfo);
        DownloadImageTask.Entry entry = getDownloadImageEntry(champion, imageType);
        task.executeOnExecutor(executor, entry);
    }

    private DownloadImageTask.Entry getDownloadImageEntry(Champion champion, ImageType imageType) {
        return new DownloadImageTask.Entry(getChampionUrl(champion, imageType), getChampionFile(champion, imageType));
    }

    private String getChampionUrl(Champion champion, ImageType type) {
        return getChampionUrl(champion, type, 0);
    }

    private String getChampionUrl(Champion champion, ImageType type, int skinId) {
        String champ = champion.getId();
        String base = CDN_URL;
        String pattern;
        switch (type) {
            case SQUARE:
                pattern = "%s/img/champion/%s.png";
                base = getVersionedCDNUrl();
                break;
            case SPLASH:
                pattern = "%s/img/champion/splash/%s_" + skinId + ".jpg";
                break;
            default:
                return null;
        }
        return String.format(pattern, base, champ);
    }

    @NonNull
    private NetworkInfo getActiveNetworkInfo() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            throw new RuntimeException("No network info could be found");
        }
        return cm.getActiveNetworkInfo();
    }

    private static class VersionCallback implements DownloadCallback<String[]> {

        private final ProgressCallback callback;

        private VersionCallback(@NonNull ProgressCallback callback) {
            this.callback = callback;
        }

        @Override
        public void handleException(Exception exception) {
            // TODO maybe do something with this exception.
        }

        @Override
        public void updateFromDownload(String[] result) {
            if (result != null && result.length > 0)
                DDragon.version.set(result[0]);
        }

        @Override
        public void finishExecution() {
            callback.finishExecution();
        }

        @Override
        public void onProgressUpdate(int progressCode, int progress, int progressMax) {
            callback.onProgressUpdate(progressCode, progress, progressMax);
        }
    }
}
