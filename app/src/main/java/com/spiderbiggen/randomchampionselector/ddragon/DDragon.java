package com.spiderbiggen.randomchampionselector.ddragon;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    private static final String TAG = DDragon.class.getSimpleName();
    private static final DDragon instance = new DDragon();
    private static final String BASE_URL = "http://ddragon.leagueoflegends.com";
    private static final String DEFAULT_VERSION = "8.4.1"; // Default version if versions endpoint fails
    private static final AtomicReference<String> version = new AtomicReference<>(DEFAULT_VERSION);
    private static final int MAX_CONCURRENCY = 8;
    private final DDragonService service;
    private SharedPreferences preferences;
    private Resources resources;

    private DDragon() {
        this.service = createService();
    }

    public static DDragon getInstance() {
        return instance;
    }

    /**
     * Sets preferences.
     *
     * @param preferences the new value of preferences
     */
    public void setPreferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }

    /**
     * Sets resources.
     *
     * @param resources the new value of resources
     */
    public void setResources(Resources resources) {
        this.resources = resources;
    }

    public Disposable updateVersion(@NonNull Action onComplete, @NonNull Consumer<Throwable> onError) {
        return service.getVersions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess(s -> version.set(s[0]))
                .doOnEvent((strings, throwable) -> onComplete.run())
                .doOnError(onError)
                .subscribe();
    }

    public String getVersion() {
        return version.get();
    }


    public Disposable getChampionList(@NonNull Consumer<? super List<Champion>> subscriber, @NonNull Consumer<Throwable> onError) {
        return service.getChampions(getVersion(), getLocal())
                .subscribeOn(Schedulers.io())
                .flatMapObservable(Observable::fromIterable)
                .filter(champion -> champion != null)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError)
                .subscribe(subscriber);
    }

    public Disposable verifyImages(List<Champion> champions, ProgressCallback callback, Consumer<List<ImageDescriptor>> consumer, @NonNull Consumer<Throwable> onError) {
        final List<ImageDescriptor> newImages = getNewImages(champions, getCompressionMethod());
        final AtomicInteger downloadCount = new AtomicInteger();
        final int total = newImages.size();
        return Observable.fromIterable(newImages)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.io())
                        .map(ImageDescriptor::verifySavedFile), MAX_CONCURRENCY)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(p -> callback.onProgressUpdate(VERIFY_SUCCESS, downloadCount.incrementAndGet(), total))
                .doOnComplete(callback::finishExecution)
                .filter(ImageDescriptor::isInValid)
                .distinct()
                .toList()
                .doOnSuccess(consumer)
                .doOnError(onError)
                .subscribe();
    }

    public Disposable downloadAllImages(List<ImageDescriptor> champions, @NonNull ProgressCallback callback, @NonNull Action onComplete, @NonNull Consumer<Throwable> onError) {
        final AtomicInteger downloadCount = new AtomicInteger();
        final int total = champions.size();
        final Bitmap.CompressFormat compressFormat = getCompressionMethod();
        final int quality = getQuality();
        return Observable.fromIterable(champions)
                .subscribeOn(Schedulers.io())
                .flatMap(item -> Observable.just(item)
                        .subscribeOn(Schedulers.io())
                        .map(imDesc -> imDesc.verifyDownload(this, compressFormat, quality)), MAX_CONCURRENCY)

                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(p -> callback.onDownloadSuccess(downloadCount.incrementAndGet(), total))
                .doOnComplete(callback::finishExecution)
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe();
    }

    private List<ImageDescriptor> getNewImages(List<Champion> champions, Bitmap.CompressFormat format) {
        List<ImageDescriptor> list = new ArrayList<>();
        for (Champion champion : champions) {
            for (ImageType type : ImageType.values()) {
                try {
                    File file = getChampionFile(champion.getId(), type, format);
                    list.add(new ImageDescriptor(champion.getId(), type, file));
                } catch (IOException e) {
                    Log.e(TAG, "getNewImages: ", e);
                }
            }
        }
        return list;
    }

    public void deleteChampionImages() throws IOException {
        FileStorage storage = FileStorage.getInstance();
        File dir = storage.getChampionImageDir();
        storage.deleteRecursive(dir);
    }

    @NonNull
    private File getChampionFile(@NonNull String champion, @NonNull ImageType type, @NonNull Bitmap.CompressFormat format) throws IOException {
        FileStorage storage = FileStorage.getInstance();
        return new File(storage.getChampionImageDir(), String.format("%s_%s.%s", champion, type.name().toLowerCase(), format.name().toLowerCase()));
    }

    public Bitmap getChampionBitmap(@NonNull Champion champion, @NonNull ImageType type) throws IOException {
        File file = getChampionFile(champion.getId(), type, getCompressionMethod());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = false;
        options.inPreferredConfig = Bitmap.Config.ARGB_4444;
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    protected void saveBitmap(@NonNull final File file, final Bitmap bitmap, Bitmap.CompressFormat compressFormat, int quality) throws IOException {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            try (FileOutputStream outputStream = new FileOutputStream(file)) {
                bitmap.compress(compressFormat, quality, outputStream);
            }
        }
    }

    @NonNull
    protected Maybe<ResponseBody> getChampionCall(String champion, ImageType type, int skinId) {
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

    private int getQuality() {
        return preferences.getInt("pref_image_quality", resources.getInteger(R.integer.pref_image_quality_default));
    }

    private String getLocal() {
        return preferences.getString("pref_language", resources.getString(R.string.pref_language_default));
    }

    private Bitmap.CompressFormat getCompressionMethod() {
        return Bitmap.CompressFormat.valueOf(preferences.getString("pref_image_type", resources.getString(R.string.pref_title_image_type_default)));
    }

}
