package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.util.Log
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.data.PreferenceManager.locale
import com.spiderbiggen.randomchampionselector.data.cache.BitmapCache
import com.spiderbiggen.randomchampionselector.data.storage.file.FileStorage
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IProgressCallback.Progress.VERIFY_SUCCESS
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
    private const val DEFAULT_VERSION = "8.13.1"
    private const val MAX_CONCURRENCY = 8
    private val version = AtomicReference(DEFAULT_VERSION)

    private val service = createService()

    private val championsDeserializer: Converter.Factory
        get() = JsonConverterFactory()


    fun updateVersion(onComplete: Action, onError: Consumer<Throwable>): Disposable {
        return service.versions
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { version.set(it[0]) }
                .doOnEvent { _, _ -> onComplete.run() }
                .doOnError(onError)
                .subscribe()
    }

    fun getChampionList(subscriber: Consumer<Collection<Champion>>,
                        onError: Consumer<Throwable>): Disposable {
        return service.getChampions(version.get(), locale)
                .subscribeOn(Schedulers.io())
                .flatMapObservable<Champion> { Observable.fromIterable(it) }
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(onError)
                .subscribe(subscriber)
    }

    fun verifyImages(champions: Collection<Champion>, callback: IProgressCallback, consumer: Consumer<Collection<ImageDescriptor>>, onError: Consumer<Throwable>): Disposable {
        val newImages = getNewImages(champions)
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

    fun downloadAllImages(champions: Collection<ImageDescriptor>, callback: IProgressCallback,
                          onComplete: Action, onError: Consumer<Throwable>): Disposable {
        val downloadCount = AtomicInteger()
        val total = champions.size
        val compressFormat = PreferenceManager.compressFormat
        val quality = PreferenceManager.quality
        return Observable.fromIterable(champions)
                .subscribeOn(Schedulers.io())
                .flatMap({
                    Observable.just(it)
                            .subscribeOn(Schedulers.io())
                            .map { saveBitmap(it.file, it.download(), compressFormat, quality) }
                }, MAX_CONCURRENCY)

                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { callback.onDownloadSuccess(downloadCount.incrementAndGet(), total) }
                .doOnComplete { callback.finishExecution() }
                .doOnComplete(onComplete)
                .doOnError(onError)
                .subscribe()
    }

    private fun getNewImages(champions: Collection<Champion>): List<ImageDescriptor> {
        val list = ArrayList<ImageDescriptor>()
        champions.forEach {
            try {
                list.add(getImageDescriptorForChampion(it))
            } catch (e: IOException) {
                Log.e(TAG, "getNewImages: ", e)
            }
        }
        return list
    }

    fun getImageDescriptorForChampion(champion: Champion): ImageDescriptor {
        return ImageDescriptor(champion.id, getChampionFile(champion))
    }

    @Throws(IOException::class)
    fun deleteChampionImages() {
        val storage = FileStorage
        val dir = storage.championImageDir
        storage.deleteRecursive(dir)
    }

    @Throws(IOException::class)
    private fun getChampionFile(champion: Champion): File =
            File(FileStorage.championImageDir, champion.id)

    fun getChampionBitmapFromCache(champion: Champion, callback: BitmapCache.BitmapCallback) {
        BitmapCache.loadBitmap(champion, callback)
    }

    @Throws(IOException::class)
    private fun saveBitmap(file: File, bitmap: Bitmap?, compressFormat: Bitmap.CompressFormat, quality: Int) {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            FileOutputStream(file).use { bitmap.compress(compressFormat, quality, it) }
        }
    }

    fun getChampionImage(champion: String, skinId: Int): Maybe<ResponseBody> =
            service.getSplashImage(champion, skinId)


    private fun createService(): DDragonService {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(championsDeserializer)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
                .build()
        return retrofit.create(DDragonService::class.java)
    }

}
