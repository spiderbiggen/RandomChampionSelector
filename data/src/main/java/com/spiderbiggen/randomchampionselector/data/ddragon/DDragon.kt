package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.os.Build
import com.spiderbiggen.randomchampionselector.data.ddragon.models.ApiChampion
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.champions.models.DownloadProgress
import com.spiderbiggen.randomchampionselector.domain.coroutines.mapAsync
import com.spiderbiggen.randomchampionselector.domain.coroutines.weakLazy
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import com.spiderbiggen.randomchampionselector.domain.storage.models.CompressionFormat
import com.spiderbiggen.randomchampionselector.domain.storage.repositories.PreferenceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject


/**
 * Defines methods to get database from League of Legends Static Data endpoints.
 *
 * @author Stefan Breetveld
 */
class DDragon @Inject constructor(
    private val service: dagger.Lazy<DDragonService>,
    private val fileRepository: dagger.Lazy<FileRepository>,
    private val preferenceManager: dagger.Lazy<PreferenceRepository>,
) {

    private val bOptions by weakLazy { BitmapFactory.Options().apply { inJustDecodeBounds = true } }

    /**
     * Retrieve the current version of [DDragon].
     */
    suspend fun getLastVersion(): String = service.get().versions()[0]

    /**
     * Retrieve the current list of [Champions][Champion] of [DDragon].
     */
    suspend fun getChampionList(version: String): List<ApiChampion> =
        service.get().getChampions(version, preferenceManager.get().locale)

    /**
     * Verify if the images for all the given champions are valid.
     *
     * @param champions the champions for which we need to verify the [Bitmap]
     */
    fun verifyImages(champions: Collection<Champion>): Flow<DownloadProgress> = flow {
        val verifyCount = AtomicInteger()
        val total = champions.size
        val result = champions.onEach {
            emit(DownloadProgress.Validating(verifyCount.incrementAndGet(), total))
        }.filterNot(::verifySavedChampionBitmap)
        emit(DownloadProgress.Unvalidated(result))
    }

    /**
     *
     */
    private fun verifySavedChampionBitmap(champion: Champion): Boolean {
        val file = fileRepository.get().getBitmapFile(champion)
        return if (file.exists()) {
            BitmapFactory.decodeFile(file.path, bOptions)
            bOptions.outHeight > 0 && bOptions.outWidth > 0
        } else false
    }

    /**
     * Download images for all the given champions.
     *
     * @param descriptors the descriptors for which we need to download the [Bitmap]
     */
    suspend fun downloadAllImages(descriptors: Collection<Champion>): Flow<DownloadProgress> = channelFlow {
        if (descriptors.isEmpty()) {
            return@channelFlow send(DownloadProgress.DownloadedSuccess)
        }
        val downloadCount = AtomicInteger()
        val total = descriptors.size
        val quality = preferenceManager.get().quality
        val compressFormat = when (preferenceManager.get().compressFormat) {
            CompressionFormat.JPEG -> CompressFormat.JPEG
            CompressionFormat.PNG -> CompressFormat.PNG
            CompressionFormat.WEBP_LOSSY -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) CompressFormat.WEBP_LOSSY else CompressFormat.WEBP
            CompressionFormat.WEBP_LOSSLESS -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) CompressFormat.WEBP_LOSSLESS else CompressFormat.WEBP
        }
        descriptors.mapAsync {
            val bitmap = service.get().getSplashImage(it.id, 0)
            saveBitmap(fileRepository.get().getBitmapFile(it), bitmap, compressFormat, quality)
            send(DownloadProgress.Downloaded(downloadCount.incrementAndGet(), total))
        }
        send(DownloadProgress.DownloadedSuccess)
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

    companion object {
        const val BASE_URL = "https://ddragon.leagueoflegends.com"
    }
}
