package com.spiderbiggen.randomchampionselector.ddragon

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import android.provider.MediaStore.getVersion
import android.util.Log
import android.widget.ImageView
import com.spiderbiggen.randomchampionselector.R
import com.spiderbiggen.randomchampionselector.model.Champion
import com.spiderbiggen.randomchampionselector.model.ImageType
import com.spiderbiggen.randomchampionselector.storage.BitmapCache
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback.Progress.VERIFY_SUCCESS
import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/**
 * Created on 13-3-2018.
 *
 * @author Stefan Breetveld
 */
object DDragon {

    private val TAG = DDragon::class.java.simpleName
    private const val BASE_URL = "http://ddragon.leagueoflegends.com"
    // Default version if versions endpoint fails
    private const val DEFAULT_VERSION = "8.4.1"
    private const val MAX_CONCURRENCY = 8
    private val version = AtomicReference(DEFAULT_VERSION)

    private val service: DDragonService
    private var preferences: SharedPreferences? = null
    private var resources: Resources? = null

    private val championsDeserializer: Converter.Factory
        get() = JsonConverterFactory()

    private val quality: Int
        get() = preferences?.getInt("pref_image_quality", resources?.getInteger(R.integer.pref_image_quality_default)
                ?: 85) ?: 85

    private val local: String
        get() = preferences?.getString("pref_language", resources?.getString(R.string.pref_language_default))
                ?: "en_US"

    private val compressionMethod: Bitmap.CompressFormat
        get() = Bitmap.CompressFormat.valueOf(preferences?.getString("pref_image_type", resources?.getString(R.string.pref_title_image_type_default))
                ?: "WEBP")

    init {
        this.service = createService()
    }

    /**
     * Sets preferences.
     *
     * @param preferences the new value of preferences
     */
    fun setPreferences(preferences: SharedPreferences) {
        this.preferences = preferences
    }

    /**
     * Sets resources.
     *
     * @param resources the new value of resources
     */
    fun setResources(resources: Resources) {
        this.resources = resources
    }

    fun useContext(context: Context) {
        setPreferences(PreferenceManager.getDefaultSharedPreferences(context.applicationContext))
        setResources(context.applicationContext.resources)
    }

    fun updateVersion(onComplete: Action, onError: Consumer<Throwable>): Disposable {
        return service.versions
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { version.set(it[0]) }
                .doOnEvent { _, _ -> onComplete.run() }
                .doOnError(onError)
                .subscribe()
    }

    fun getChampionList(subscriber: Consumer<in List<Champion>>,
                        onError: Consumer<Throwable>): Disposable {
        return service.getChampions(version.get(), local)
                .subscribeOn(Schedulers.io())
                .flatMapObservable<Champion> { Observable.fromIterable(it) }
//                .filter { it != null }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError)
                .subscribe(subscriber)
    }

    fun verifyImages(champions: List<Champion>, callback: ProgressCallback, consumer: Consumer<List<ImageDescriptor>>, onError: Consumer<Throwable>): Disposable {
        val newImages = getNewImages(champions, compressionMethod)
        val downloadCount = AtomicInteger()
        val total = newImages.size
        return Observable.fromIterable(newImages)
                .subscribeOn(Schedulers.io())
                .flatMap({
                    Observable.just(it)
                            .subscribeOn(Schedulers.io())
                            .map<ImageDescriptor> { it.verifySavedFile() }
                }, MAX_CONCURRENCY)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { callback.onProgressUpdate(VERIFY_SUCCESS, downloadCount.incrementAndGet(), total) }
                .doOnComplete { callback.finishExecution() }
                .filter { it.invalid }
                .distinct()
                .toList()
                .doOnSuccess(consumer)
                .doOnError(onError)
                .subscribe()
    }

    fun downloadAllImages(champions: List<ImageDescriptor>, callback: ProgressCallback,
                          onComplete: Action, onError: Consumer<Throwable>): Disposable {
        val downloadCount = AtomicInteger()
        val total = champions.size
        val compressFormat = compressionMethod
        val quality = quality
        return Observable.fromIterable(champions)
                .subscribeOn(Schedulers.io())
                .flatMap({
                    Observable.just(it)
                            .subscribeOn(Schedulers.io())
                            .map { it.verifyDownload(compressFormat, quality) }
                }, MAX_CONCURRENCY)

                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { callback.onDownloadSuccess(downloadCount.incrementAndGet(), total) }
                .doOnComplete { callback.finishExecution() }
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe()
    }

    private fun getNewImages(champions: List<Champion>, format: Bitmap.CompressFormat): List<ImageDescriptor> {
        val list = ArrayList<ImageDescriptor>()
        for (champion in champions) {
            for (type in ImageType.values()) {
                try {
                    val file = getChampionFile(champion.id, type, format)
                    list.add(ImageDescriptor(champion.id, type, file))
                } catch (e: IOException) {
                    Log.e(TAG, "getNewImages: ", e)
                }

            }
        }
        return list
    }

    @Throws(IOException::class)
    fun deleteChampionImages() {
        val storage = FileStorage
        val dir = storage.championImageDir
        storage.deleteRecursive(dir)
    }

    @Throws(IOException::class)
    private fun getChampionFile(champion: String, type: ImageType, format: Bitmap.CompressFormat): File =
            File(FileStorage.championImageDir, "${champion}_${type.name.toLowerCase()}.${format.name.toLowerCase()}")

    @Deprecated("Should probably load this asynchronously.", replaceWith = ReplaceWith("getChampionBitmapFromCache"), level = DeprecationLevel.WARNING)
    @Throws(IOException::class)
    fun getChampionBitmap(champion: Champion, type: ImageType): Bitmap? {
        val file = getChampionFile(champion.id, type, compressionMethod)
        val options = BitmapFactory.Options()
        options.inMutable = false
        options.inPreferredConfig = Bitmap.Config.ARGB_4444
        return BitmapFactory.decodeFile(file.path, options)
    }

    fun getChampionBitmapFromCache(champion: Champion, type: ImageType, imageView: ImageView) {
        val file = getChampionFile(champion.id, type, compressionMethod)
//        val options = BitmapFactory.Options()
//        options.inMutable = false
//        options.inPreferredConfig = Bitmap.Config.ARGB_4444
        return BitmapCache.loadBitmap("${champion.key}_${type.name.toLowerCase()}", file, imageView)
    }

    @Throws(IOException::class)
    fun saveBitmap(file: File, bitmap: Bitmap?, compressFormat: Bitmap.CompressFormat, quality: Int) {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            FileOutputStream(file).use { bitmap.compress(compressFormat, quality, it) }
        }
    }

    fun getChampionCall(champion: String, type: ImageType, skinId: Int): Maybe<ResponseBody> {
        return when (type) {
            ImageType.SQUARE -> service.getSquareImage(version.get(), champion)
            ImageType.SPLASH -> service.getSplashImage(champion, skinId)
        }
    }

    private fun createService(): DDragonService {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(championsDeserializer)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build()
        return retrofit.create(DDragonService::class.java)
    }

}
