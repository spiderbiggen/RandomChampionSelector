package com.spiderbiggen.randomchampionselector.presentation.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapFactory.Options
import android.os.Build
import androidx.collection.LruCache
import com.spiderbiggen.randomchampionselector.domain.champions.models.Champion
import com.spiderbiggen.randomchampionselector.domain.storage.FileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

/**
 * Caches Bitmaps in memory. Also loads the Bitmap from storage if it isn't cached.
 *
 * @author Stefan Breetveld
 */
class BitmapCache @Inject constructor(private val fileRepository: FileRepository) {
    private val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }
    }

    fun clear(champions: Collection<Champion>) {
        mMemoryCache.evictionCount()
        champions.forEach { mMemoryCache.remove(it.id) }
    }

    /**
     * Load the [Bitmap] identified by the given [champion] from memory if possible, otherwise load from disk storage.
     * Optionally [minWidth] and [minHeight] can be supplied to indicate the expected display size of the [Bitmap].
     *
     * @param champion the [Champion] for which we want a [Bitmap]
     * @param minWidth possible minimum size
     * @param minHeight possible minimum size
     * @return Bitmap
     */
    suspend fun loadBitmap(champion: Champion, minWidth: Int = DEFAULT_SIZE, minHeight: Int = DEFAULT_SIZE): Result<Bitmap> = kotlin.runCatching {
        val key = champion.id
        val file = fileRepository.getBitmapFile(champion)
        mMemoryCache.get(key) ?: if (!file.exists() || !file.canRead()) {
            throw IOException("File doesn't exist or can't be accessed")
        } else {
            val bitmap = decodeSampledBitmapFromFile(file.path, minWidth, minHeight)
            mMemoryCache.put(key, bitmap)
            bitmap
        }
    }

    /**
     * Calculate the required [BitmapFactory.Options.inSampleSize] to make sure the resulting image
     * is at least wider than [minWidth] and higher than [minHeight].
     *
     * @param options [BitmapFactory Options][BitmapFactory.Options] that determine how the [Bitmap] should be loaded
     * @param minWidth minimum width for the [Bitmap]
     * @param minHeight minimum height for the [Bitmap]
     */
    private fun calculateInSampleSize(options: Options, minWidth: Int, minHeight: Int): Int {
        val reqHeight: Int = if (minHeight > 0) minHeight else DEFAULT_SIZE
        val reqWidth = if (minWidth > 0) minWidth else DEFAULT_SIZE

        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var sampleSize = 1

        if (height > minHeight || width > minWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / sampleSize >= reqHeight && halfWidth / sampleSize >= reqWidth) {
                sampleSize *= 2
            }
        }

        return sampleSize
    }

    /**
     * Decode the [Bitmap] stored at [file] for optimal display and memory usage.
     * The resulting [Bitmap] will be at least [minWidth] and [minHeight]
     *
     * @param file the path to the [Bitmap] on disk storage.
     * @param minWidth minimum width for the [Bitmap]
     * @param minHeight minimum height for the [Bitmap]
     */
    private suspend fun decodeSampledBitmapFromFile(
        file: String,
        minWidth: Int,
        minHeight: Int
    ): Bitmap = withContext(Dispatchers.IO) {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight)
        options.inPreferredConfig =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Bitmap.Config.HARDWARE else Bitmap.Config.RGB_565
        options.inJustDecodeBounds = false
        BitmapFactory.decodeFile(file, options)
    }

    companion object {
        private const val DEFAULT_SIZE = 400
    }
}

