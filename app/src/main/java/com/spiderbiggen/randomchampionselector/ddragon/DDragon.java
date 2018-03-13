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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
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

    public void updateVersion(@NonNull Consumer<? super String[]> onAfterSuccess) {
        service.getVersions()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doAfterSuccess(s -> version.set(s[0]))
                .subscribe(onAfterSuccess);
    }

    public String getVersion() {
        return version.get();
    }


    public void getChampionList(@NonNull Consumer<? super List<Champion>> onAfterSuccess) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(context);
        String locale = p.getString("pref_language", "en_US");
        service.getChampions(getVersion(), locale)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(onAfterSuccess);
    }

    public Bitmap getChampionBitmap(@NonNull Champion champion, @NonNull ImageType type) {
        File file = getChampionFile(champion, type);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    @NonNull
    private File getChampionFile(@NonNull Champion champion, @NonNull ImageType type) {
        return new FileStorage(context).getChampionImageFile(champion, type);
    }

    public void downloadAllImages(List<Champion> champions, @NonNull ProgressCallback consumer) {
        final AtomicInteger count = new AtomicInteger();
        final ImageType[] types = ImageType.values();
        final int typesLength = types.length;
        final int total = champions.size() * typesLength;
        final Observable<Pair<Champion, ImageType>> observable = Observable.create(emitter -> {
            for (Champion champion : champions) {
                for (ImageType type : types) {
                    emitter.onNext(Pair.create(champion, type));
                }
            }
            emitter.onComplete();
        });
        observable.observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnComplete(consumer::finishExecution)
                .subscribe(imageBlock -> {
                    Champion champion = imageBlock.first;
                    ImageType type = imageBlock.second;
                    Call<ResponseBody> maybe = getChampionCall(champion, type, 0);
                    Response<ResponseBody> response = maybe.execute();
                    if (response.isSuccessful() && response.body() != null) {
                        try (InputStream stream = response.body().byteStream()) {
                            Bitmap bitmap = BitmapFactory.decodeStream(stream);
                            saveBitmap(getChampionFile(champion, type), bitmap);
                        }
                    }
                    consumer.onProgressUpdate(Progress.DOWNLOAD_SUCCESS, count.incrementAndGet(), total);
                });
    }

    private void saveBitmap(@NonNull final File file, final Bitmap bitmap) throws IOException {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 85, outputStream);
            }
        }
    }

    @NonNull
    private Call<ResponseBody> getChampionCall(Champion champion, ImageType type, int skinId) {
        String champ = champion.getId();
        switch (type) {
            case SQUARE:
                return service.getSquareImage(getVersion(), champ);
            case SPLASH:
                return service.getSplashImage(champ, skinId);
            default:
                return null;
        }
    }

    private DDragonService createService() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(new TypeToken<List<Champion>>() {}.getClass(), getChampionsDeserializer())
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
        return new JsonDeserializer<List<Champion>>() {
            @Override
            public List<Champion> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
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
            }
        };
    }
}
