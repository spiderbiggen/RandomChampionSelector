package com.spiderbiggen.randomchampionselector.ddragon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.spiderbiggen.randomchampionselector.util.async.ProgressCallback.Progress.VERIFY_SUCCESS;

/**
 * Created on 13-3-2018.
 *
 * @author Stefan Breetveld
 */
public class DDragon {

    private static final String BASE_URL = "http://ddragon.leagueoflegends.com";
    private static final String DEFAULT_VERSION = "8.4.1"; // Default version if versions endpoint fails
    private static final AtomicReference<String> version = new AtomicReference<>(DEFAULT_VERSION);
    private static final int MAX_CONCURRENCY = 8;

    private final DDragonService service;
    private final String locale;
    private final FileStorage storage;
    private final Bitmap.CompressFormat format;
    private final int quality;

    public DDragon(String locale, FileStorage storage, Bitmap.CompressFormat format, int quality) {
        this.service = createService();
        this.locale = locale;
        this.storage = storage;
        this.format = format;
        this.quality = quality;
    }

    public static DDragon createDDragon(Context context) {
        FileStorage fileStorage = new FileStorage(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String locale = preferences.getString("pref_language", "en_US");
        Bitmap.CompressFormat format = Bitmap.CompressFormat.valueOf(preferences.getString("pref_image_type", "WEBP"));
        int quality = preferences.getInt("pref_image_quality", 85);

        return new DDragon(locale, fileStorage, format, quality);
    }

    public Disposable updateVersion(@NonNull Action onComplete, @NonNull Consumer<Throwable> onError) {
        return service.getVersions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(s -> version.set(s[0]))
                .doOnEvent((strings, throwable) -> onComplete.run())
                .doOnError(onError)
                .subscribe();
    }

    public String getVersion() {
        return version.get();
    }


    public Disposable getChampionList(@NonNull Consumer<? super List<Champion>> subscriber, @NonNull Consumer<Throwable> onError) {
        return service.getChampions(getVersion(), locale)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(Observable::fromIterable)
                .filter(champion -> champion != null)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError)
                .subscribe(subscriber);
    }

    public Disposable verifyImages(List<Champion> champions, ProgressCallback callback, Consumer<List<ImageDescriptor>> consumer, @NonNull Consumer<Throwable> onError) throws IOException {
        final List<ImageDescriptor> newImages = getNewImages(champions, format);
        final AtomicInteger downloadCount = new AtomicInteger();
        final int total = newImages.size();
        return Observable.fromIterable(newImages)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.io())
                        .map(pair -> {
                            pair.setValid(pair.getFile().exists());
                            if (pair.isValid()) {
                                try (InputStream stream = new FileInputStream(pair.getFile())) {
                                    pair.setValid(BitmapFactory.decodeStream(stream) != null);
                                }
                            }
                            return pair;
                        }), MAX_CONCURRENCY)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(p -> callback.onProgressUpdate(VERIFY_SUCCESS, downloadCount.incrementAndGet(), total))
                .doOnComplete(callback::finishExecution)
                .filter(p -> !p.isValid())
                .distinct()
                .toList()
                .doOnSuccess(consumer)
                .doOnError(onError)
                .subscribe();
    }

    public Disposable downloadAllImages(List<ImageDescriptor> champions, @NonNull ProgressCallback callback, @NonNull Action onComplete, @NonNull Consumer<Throwable> onError) throws IOException {
        final AtomicInteger downloadCount = new AtomicInteger();
        final int total = champions.size();
        return Observable.fromIterable(champions)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.io())
                        .map(pair -> {
                            if (!pair.isValid()) {
                                ResponseBody body = getChampionCall(pair.getChampion(), pair.getType(), 0).blockingGet();
                                if (body != null) {
                                    try (InputStream stream = body.byteStream()) {
                                        Bitmap bitmap = BitmapFactory.decodeStream(stream);
                                        saveBitmap(pair.getFile(), bitmap, format, quality);
                                    }
                                }
                            }
                            return pair;
                        }), MAX_CONCURRENCY)

                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(p -> callback.onDownloadSuccess(downloadCount.incrementAndGet(), total))
                .doOnComplete(callback::finishExecution)
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe();
    }

    private List<ImageDescriptor> getNewImages(List<Champion> champions, Bitmap.CompressFormat format) throws IOException {
        List<ImageDescriptor> list = new ArrayList<>();
        for (Champion champion : champions) {
            for (ImageType type : ImageType.values()) {
                File file = getChampionFile(champion.getId(), type, format);
                list.add(new ImageDescriptor(champion.getId(), type, file));
            }
        }
        return list;
    }

    public boolean deleteChampionImages() throws IOException {
        File dir = storage.getChampionImageDir();
        return storage.deleteRecursive(dir);
    }

    @NonNull
    private File getChampionFile(@NonNull String champion, @NonNull ImageType type, Bitmap.CompressFormat format) throws IOException {
        return new File(storage.getChampionImageDir(), String.format("%s_%s.%s", champion, type.name().toLowerCase(), format.name().toLowerCase()));
    }

    public Bitmap getChampionBitmap(@NonNull Champion champion, @NonNull ImageType type) throws IOException {
        File file = getChampionFile(champion.getId(), type, format);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    private void saveBitmap(@NonNull final File file, final Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(compressFormat, quality, outputStream);
            }
        }
    }

    @NonNull
    private Maybe<ResponseBody> getChampionCall(String champion, ImageType type, int skinId) {
        switch (type) {
            case SQUARE:
                return service.getSquareImage(getVersion(), champion);
            case SPLASH:
            default:
                return service.getSplashImage(champion, skinId);
        }
    }

    private DDragonService createService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Champion>>() {}.getType(), getChampionsDeserializer())
                .create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build();
        return retrofit.create(DDragonService.class);
    }

    private JsonDeserializer<List<Champion>> getChampionsDeserializer() {
        return (json, typeOfT, context) -> {
            JsonObject object = json.getAsJsonObject();
            object = object.getAsJsonObject("data");
            List<Champion> list = new ArrayList<>();
            Set<Map.Entry<String, JsonElement>> entries = object.entrySet();
            for (Map.Entry<String, JsonElement> entry : entries) {
                Champion champion = context.deserialize(entry.getValue(), Champion.class);
                if (champion != null) {
                    list.add(champion);
                }
            }
            return list;
        };
    }

}
