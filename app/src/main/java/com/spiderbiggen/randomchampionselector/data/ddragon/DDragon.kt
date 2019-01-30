package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.util.Log
import com.spiderbiggen.randomchampionselector.BuildConfig
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.data.PreferenceManager.locale
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IProgressCallback.Progress.VERIFY_SUCCESS
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference


/**
 * Defines methods to get data from League of Legends Static Data endpoints.
 *
 * @author Stefan Breetveld
 */
object DDragon {

    private val TAG = DDragon::class.java.simpleName
    private const val BASE_URL = "http://ddragon.leagueoflegends.com"
    // Default version if versions endpoint fails
    private const val DEFAULT_VERSION = "9.2.1"
    private val MAX_CONCURRENCY = Runtime.getRuntime().availableProcessors()

    private val version = AtomicReference(DEFAULT_VERSION)
    private val service
        get() = createService()

    /**
     * Update the current version in [DDragon] and signal completion on [onComplete].
     *
     * @param onComplete tell the callback that we have updated the version successfully
     * @param onError tell the callback that we failed to update the version
     * @return [Disposable] that can be disposed to cancel the request
     */
    fun updateVersion(onComplete: () -> Unit, onError: (Throwable) -> Unit): Disposable {
        return getLastVersion({ version.set(it); onComplete() }, onError)
    }

    /**
     * Retrieve the current version of [DDragon] and send the result to [onComplete].
     *
     * @param onComplete tell the callback the current version
     * @param onError tell the callback that we failed to retrieve the version
     * @return [Disposable] that can be disposed to cancel the request
     */
    fun getLastVersion(onComplete: (String) -> Unit, onError: (Throwable) -> Unit): Disposable {
        return service.versions.observeOn(AndroidSchedulers.mainThread()).map { it[0] }
                .doOnError(onError).subscribe(onComplete)
    }

    /**
     * Retrieve the current list of [Champions][Champion] of [DDragon] and send the result to [onComplete].
     *
     * @param onComplete give the list of champions to the callback
     * @param onError tell the callback that we failed to retrieve the champions
     * @return [Disposable] that can be disposed to cancel the request
     */
    fun getChampionList(onComplete: (Collection<Champion>) -> Unit, onError: (Throwable) -> Unit): Disposable {
        return service.getChampions(version.get(), locale)
                .flatMapObservable<Champion> { Observable.fromIterable(it) }.toList()
                .observeOn(AndroidSchedulers.mainThread()).doOnError(onError).subscribe(onComplete)
    }

    /**
     * Helper method to flatMap an asynchronous call on objects of type [R] that convert them to type [T].
     * Will only run [MAX_CONCURRENCY] threads at a time.
     */
    private fun <T, R> Observable<R>.flatMapMultiple(map: (R) -> T): Observable<T> =
            flatMap({ Observable.just(it).subscribeOn(Schedulers.io()).map(map) }, MAX_CONCURRENCY)

    /**
     * Verify if the images for all the given champions are valid and send the result to [onComplete].
     *
     * @param champions the champions for which we need to verify the [Bitmap]
     * @param progress progress callback
     * @param onComplete tell the callback the collection of descriptors that failed verification
     * @param onError tell the callback that something went wrong during validation
     * @return [Disposable] that can be disposed to cancel the request
     */
    fun verifyImages(champions: Collection<Champion>, progress: IProgressCallback,
                     onComplete: (Collection<ImageDescriptor>) -> Unit, onError: (Throwable) -> Unit): Disposable {
        val newImages = champions.map(Champion::imageDescriptor).toList()
        val downloadCount = AtomicInteger()
        val total = newImages.size
        return Observable.fromIterable(newImages).subscribeOn(Schedulers.io())
                .flatMapMultiple { descriptor -> descriptor.verifySavedFile() }
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { progress.onProgressUpdate(VERIFY_SUCCESS, downloadCount.incrementAndGet(), total) }
                .filter { it.invalid }.toList().subscribe(onComplete, onError)
    }

    /**
     * Download images for all the given champions and signal [onComplete] when done.
     *
     * @param descriptors the descriptors for which we need to download the [Bitmap]
     * @param progress progress callback
     * @param onComplete called when all images have been downloaded
     * @param onError tell the callback that we failed to download all images
     * @return [Disposable] that can be disposed to cancel the request
     */
    fun downloadAllImages(descriptors: Collection<ImageDescriptor>, progress: IProgressCallback,
                          onComplete: () -> Unit, onError: (Throwable) -> Unit): Disposable {
        val downloadCount = AtomicInteger()
        val total = descriptors.size
        val compressFormat = PreferenceManager.compressFormat
        val quality = PreferenceManager.quality
        return Observable.fromIterable(descriptors).subscribeOn(Schedulers.io())
                .flatMap {
                    service.getSplashImage(it.champion, 0).flatMapObservable { bitmap ->
                        saveBitmap(it.file, bitmap, compressFormat, quality)
                        Observable.just(bitmap)
                    }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(Consumer { progress.onDownloadSuccess(downloadCount.incrementAndGet(), total) }, Consumer(onError), Action(onComplete))
    }

    private fun saveBitmap(file: File, bitmap: Bitmap?, compressFormat: Bitmap.CompressFormat, quality: Int) {
        if (bitmap != null && (file.exists() || file.createNewFile())) {
            try {
                FileOutputStream(file).use { bitmap.compress(compressFormat, quality, it) }
            } catch (e: IOException) { /* Do nothing */
            }
        }
    }

    private fun createService(): DDragonService {
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(httpClient.build())
                .addConverterFactory(CustomConverter())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build()
        return retrofit.create(DDragonService::class.java)
    }

}
