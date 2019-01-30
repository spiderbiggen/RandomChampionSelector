package com.spiderbiggen.randomchampionselector.data.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import androidx.collection.LruCache
import com.spiderbiggen.randomchampionselector.domain.Champion

/**
 * Caches Bitmaps in memory. Also loads the Bitmap from storage if it isn't cached.
 *
 * @author Stefan Breetveld
 */
object BitmapCache {
    private const val DEFAULT_SIZE = 400
    private val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }
    }

    /**
     * Load the [Bitmap] identified by the given [champion] from memory if possible, otherwise load from disk storage.
     * Return the resulting [Bitmap] on the [callback]. If an error occurs an error message can be sent on the [error] callback.
     * Optionally [minWidth] and [minHeight] can be supplied to indicate the expected display size of the [Bitmap].
     *
     * @param champion the [Champion] for which we want a [Bitmap]
     * @param callback where the resulting [Bitmap] should be sent
     * @param error possible error message callback
     * @param minWidth possible minimum size
     * @param minHeight possible minimum size
     */
    fun loadBitmap(champion: Champion, callback: (Bitmap) -> Unit, error: ((String) -> Unit)? = null, minWidth: Int = DEFAULT_SIZE, minHeight: Int = DEFAULT_SIZE) {
        val (key, file) = champion.imageDescriptor
        val bitmap = mMemoryCache.get(key)
        if (bitmap != null) {
            callback(bitmap)
            return
        }
        if (file.exists() && file.canRead()) {
            val task = BitmapWorkerTask(callback, minWidth, minHeight)
            task.execute(key, file.path)
        } else {
            error?.invoke("File doesn't exist or can't be accessed")
        }
    }

    /**
     * Constructs a new [AsyncTask] that loads a [Bitmap] and sends the result to [bitmapCallback].
     *
     * @param bitmapCallback where the resulting [Bitmap] will be sent
     * @param minWidth minimum width of the [Bitmap]
     * @param minHeight minimum height of the [Bitmap]
     */
    private class BitmapWorkerTask(private val bitmapCallback: (Bitmap) -> Unit, private val minWidth: Int, private val minHeight: Int) : AsyncTask<String, Void, Bitmap>() {

        // Decode image in background.
        override fun doInBackground(vararg params: String): Bitmap {
            val bitmap = decodeSampledBitmapFromFile(params[1], minWidth, minHeight)
            mMemoryCache.put(params[0], bitmap)
            return bitmap
        }

        override fun onPostExecute(result: Bitmap) = bitmapCallback(result)
    }

    /**
     * Calculate the required [BitmapFactory.Options.inSampleSize] to make sure the resulting image
     * is at least wider than [minWidth] and higher than [minHeight].
     *
     * @param options [BitmapFactory Options][BitmapFactory.Options] that determine how the [Bitmap] should be loaded
     * @param minWidth minimum width for the [Bitmap]
     * @param minHeight minimum height for the [Bitmap]
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options, minWidth: Int, minHeight: Int): Int {
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
    private fun decodeSampledBitmapFromFile(file: String, minWidth: Int, minHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, options)
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, minWidth, minHeight)
        options.inPreferredConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Bitmap.Config.HARDWARE else Bitmap.Config.RGB_565
//        options.inPreferredConfig = Bitmap.Config.RGB_565
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }
}

