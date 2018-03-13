package com.spiderbiggen.randomchampionselector.ddragon;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Pair;

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

/**
 * Created on 13-3-2018.
 *
 * @author Stefan Breetveld
 */

public class DDragon {

    private static final String TAG = DDragon.class.getSimpleName();
    private static final String BASE_URL = "http://ddragon.leagueoflegends.com";
    private static final String DEFAULT_VERSION = "8.4.1"; // Default version if versions endpoint fails
    private static final AtomicReference<String> version = new AtomicReference<>(DEFAULT_VERSION);

    private final Context context;
    private final DDragonService service;

    public DDragon(Context context) {
        this.context = context;
        this.service = createService();
    }

    public Disposable updateVersion(@NonNull ProgressCallback consumer, @NonNull Action onComplete) {
        return service.getVersions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterSuccess(s -> version.set(s[0]))
                .doOnError(e -> consumer.onError())
                .doOnEvent((strings, throwable) -> {
                    consumer.finishExecution();
                    onComplete.run();
                })
                .subscribe(l -> consumer.onDownloadSuccess(1, 1));
    }

    public String getVersion() {
        return version.get();
    }


    public Disposable getChampionList(ProgressCallback progress, @NonNull Consumer<? super List<Champion>> subscriber) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String locale = p.getString("pref_language", "en_US");
        AtomicInteger count = new AtomicInteger();
        AtomicInteger total = new AtomicInteger();
        Observable<Champion> championObservable = service.getChampions(getVersion(), locale)
                .subscribeOn(Schedulers.io())
                .flatMapObservable(source -> {
                    total.set(source.size());
                    return Observable.fromIterable(source);
                })
                .observeOn(AndroidSchedulers.mainThread());
        if (progress != null) {
            championObservable
                    .doOnComplete(progress::finishExecution)
                    .doOnNext(champion -> progress.onDownloadSuccess(count.incrementAndGet(), total.get()));
        }
        return championObservable
                .filter(champion -> champion != null)
                .toList()
                .subscribe(subscriber);
    }

    public Disposable downloadAllImages(List<Champion> champions, @NonNull ProgressCallback consumer, Action onComplete) {
        final List<Pair<Champion, ImageType>> newImages = getNewImages(champions);
        final AtomicInteger count = new AtomicInteger();
        final int total = newImages.size();
        return Observable.fromIterable(newImages)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.io())
                        .map(pair -> {
                            ResponseBody body = getChampionCall(pair.first, pair.second, 0).blockingGet();
                            File championFile = getChampionFile(pair.first, pair.second);
                            if (body != null) {
                                try (InputStream stream = body.byteStream()) {
                                    Bitmap bitmap = BitmapFactory.decodeStream(stream);
                                    saveBitmap(championFile, bitmap);
                                }
                            }
                            return true;
                        })
                )
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(consumer::finishExecution)
                .doOnComplete(onComplete)
                .doOnNext(bool -> consumer.onDownloadSuccess(count.incrementAndGet(), total))
                .subscribe();
    }

    @NonNull
    private File getChampionFile(@NonNull Champion champion, @NonNull ImageType type) {
        return new FileStorage(context).getChampionImageFile(champion, type);
    }

    private List<Pair<Champion, ImageType>> getNewImages(List<Champion> champions) {
        List<Pair<Champion, ImageType>> list = new ArrayList<>();
        File file;
        for (Champion champion : champions) {
            for (ImageType type : ImageType.values()) {
                file = getChampionFile(champion, type);
                if (file.exists()) continue;
                list.add(Pair.create(champion, type));
            }
        }
        return list;
    }

    public Bitmap getChampionBitmap(@NonNull Champion champion, @NonNull ImageType type) {
        File file = getChampionFile(champion, type);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    private void saveBitmap(@NonNull final File file, final Bitmap bitmap) throws IOException {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 85, outputStream);
            }
        }
    }

    @NonNull
    private Maybe<ResponseBody> getChampionCall(Champion champion, ImageType type, int skinId) {
        String champ = champion.getId();
        switch (type) {
            case SQUARE:
                return service.getSquareImage(getVersion(), champ);
            case SPLASH:
            default:
                return service.getSplashImage(champ, skinId);
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

    @NonNull
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
