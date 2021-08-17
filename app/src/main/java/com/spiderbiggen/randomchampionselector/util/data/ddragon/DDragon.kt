package com.spiderbiggen.randomchampionselector.util.data.ddragon

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import com.spiderbiggen.randomchampionselector.BuildConfig
import com.spiderbiggen.randomchampionselector.util.data.PreferenceManager
import com.spiderbiggen.randomchampionselector.util.data.PreferenceManager.locale
import com.spiderbiggen.randomchampionselector.util.data.mapAsync
import com.spiderbiggen.randomchampionselector.util.data.onMainThread
import com.spiderbiggen.randomchampionselector.util.data.storage.file.FileStorage.championBitmap
import com.spiderbiggen.randomchampionselector.models.Champion
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback
import com.spiderbiggen.randomchampionselector.interfaces.IProgressCallback.Progress.VERIFY_SUCCESS
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger


/**
 * Defines methods to get database from League of Legends Static Data endpoints.
 *
 * @author Stefan Breetveld
 */
object DDragon {

    private val TAG = DDragon::class.java.simpleName
    private const val BASE_URL = "http://ddragon.leagueoflegends.com"
    // Default version if versions endpoint fails
    private val service by lazy(this::createService)

    /**
     * Retrieve the current version of [DDragon].
     */
    suspend fun getLastVersion(): String = service.versions()[0]

    /**
     * Retrieve the current list of [Champions][Champion] of [DDragon].
     */
    suspend fun getChampionList(version: String): Collection<Champion> =
        service.getChampions(version, locale)

    /**
     * Verify if the images for all the given champions are valid.
     *
     * @param champions the champions for which we need to verify the [Bitmap]
     * @param progress progress callback
     */
    suspend fun verifyImages(
        champions: Collection<Champion>,
        progress: IProgressCallback
    ): Collection<Champion> {
        val verifyCount = AtomicInteger()
        val total = champions.size
        return champions.onEach {
            onMainThread { progress.update(VERIFY_SUCCESS, verifyCount.incrementAndGet(), total) }
        }.filterNot(::verifySavedChampionBitmap).toList()
    }

    /**
     *
     */
    private fun verifySavedChampionBitmap(champion: Champion): Boolean {
        val file = champion.championBitmap
        return if (file.exists()) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, options)
            options.outHeight > 0 && options.outWidth > 0
        } else false
    }

    /**
     * Download images for all the given champions.
     *
     * @param descriptors the descriptors for which we need to download the [Bitmap]
     * @param progress progress callback
     */
    suspend fun downloadAllImages(
        descriptors: Collection<Champion>,
        progress: IProgressCallback
    ) {
        val downloadCount = AtomicInteger()
        val total = descriptors.size
        val compressFormat = PreferenceManager.compressFormat
        val quality = PreferenceManager.quality
        descriptors.mapAsync {
            val bitmap = service.getSplashImage(it.id, 0)
            saveBitmap(it.championBitmap, bitmap, compressFormat, quality)
            onMainThread { progress.update(downloadCount.incrementAndGet(), total) }
        }
    }

    /**
     * Saves the given [image] to [file] as [type] with a quality of [quality].
     *
     * @param file Reference to the desired location of the [Bitmap]
     * @param image the [Bitmap] that needs to be saved.
     * @param type the [CompressFormat] of the saved image.
     * @param quality the quality in [0-100]%
     *
     * @see [Bitmap.compress]
     */
    private fun saveBitmap(file: File, image: Bitmap, type: CompressFormat, quality: Int) {
        if (file.exists() || file.createNewFile()) {
            try {
                FileOutputStream(file).use { image.compress(type, quality, it) }
            } catch (e: IOException) { /* Do nothing */
            }
        }
    }

    /**
     * Initialize retrofit for Coroutines with [CustomConverter] and if [BuildConfig.DEBUG] also add logging.
     *
     * @return initialized [DDragonService]
     */
    private fun createService(): DDragonService {
        val httpClient = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BASIC
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder().baseUrl(BASE_URL).client(httpClient.build())
            .addConverterFactory(CustomConverter())
            .build()
        return retrofit.create(DDragonService::class.java)
    }

}
