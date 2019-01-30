package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.spiderbiggen.randomchampionselector.BuildConfig
import com.spiderbiggen.randomchampionselector.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.data.PreferenceManager.locale
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.IProgressCallback
import com.spiderbiggen.randomchampionselector.model.IProgressCallback.Progress.VERIFY_SUCCESS
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.Executors
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
    private val LIMITED_IO_CONTEXT = Executors.newFixedThreadPool(MAX_CONCURRENCY)
            .asCoroutineDispatcher()
    private val version = AtomicReference(DEFAULT_VERSION)
    private val service
        get() = createService()

    /**
     * Update the current version in [DDragon].
     */
    suspend fun updateVersion() = version.set(getLastVersion())

    /**
     * Retrieve the current version of [DDragon].
     */
    suspend fun getLastVersion(): String = service.versions().await()[0]

    /**
     * Retrieve the current list of [Champions][Champion] of [DDragon].
     */
    suspend fun getChampionList(): Collection<Champion> = service.getChampions(version.get(), locale).await()

    /**
     * Verify if the images for all the given champions are valid.
     *
     * @param champions the champions for which we need to verify the [Bitmap]
     * @param progress progress callback
     */
    fun verifyImages(champions: Collection<Champion>, progress: IProgressCallback): Collection<ImageDescriptor> {
        val newImages = champions.map(Champion::imageDescriptor).toList()
        val verifyCount = AtomicInteger()
        val total = newImages.size
        return newImages.map { it.verifySavedFile() }
                .onEach {
                    onMainThread { progress.onProgressUpdate(VERIFY_SUCCESS, verifyCount.incrementAndGet(), total) }
                }
                .filter { it.invalid }
                .toList()
    }

    /**
     * Download images for all the given champions.
     *
     * @param descriptors the descriptors for which we need to download the [Bitmap]
     * @param progress progress callback
     */
    suspend fun downloadAllImages(descriptors: Collection<ImageDescriptor>, progress: IProgressCallback) {
        val downloadCount = AtomicInteger()
        val total = descriptors.size
        onMainThread { progress.onDownloadSuccess(downloadCount.get(), total) }
        val compressFormat = PreferenceManager.compressFormat
        val quality = PreferenceManager.quality
        descriptors.mapAsync {
            val bitmap = service.getSplashImage(it.champion, 0)
            saveBitmap(it.file, bitmap.await(), compressFormat, quality)
            onMainThread { progress.onDownloadSuccess(downloadCount.incrementAndGet(), total) }
        }
    }

    private fun <A, B> Iterable<A>.mapAsync(f: suspend (A) -> B): List<B> = runBlocking {
        map { async(LIMITED_IO_CONTEXT) { f(it) } }.map { it.await() }
    }

    private fun onMainThread(f: () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) { f() }
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
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        return retrofit.create(DDragonService::class.java)
    }

}
