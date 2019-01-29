package com.spiderbiggen.randomchampionselector.data.cache

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Build
import androidx.collection.LruCache
import com.spiderbiggen.randomchampionselector.data.ddragon.DDragon
import com.spiderbiggen.randomchampionselector.domain.Champion

/**
 * Created on 5-7-2018.
 * @author Stefan Breetveld
 */
object BitmapCache {
    private val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {

        override fun sizeOf(key: String, bitmap: Bitmap): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap.byteCount / 1024
        }
    }

    fun loadBitmap(champion: Champion, callback: BitmapCallback, vWidth: Int = DEFAULT_SIZE, vHeight: Int = DEFAULT_SIZE) {
        val (key, file) = DDragon.getImageDescriptorForChampion(champion)
        val bitmap = getBitmapFromMemCache(key)
        if (bitmap != null) {
            callback.loadImageSuccess(bitmap)
        } else {
            val task = BitmapWorkerTask(callback, vWidth, vHeight)
            task.execute(key, file.path)
        }
    }


    fun addBitmapToMemoryCache(key: String, bitmap: Bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap)
        }
    }

    private fun getBitmapFromMemCache(key: String): Bitmap? {
        return mMemoryCache.get(key)
    }

    class BitmapWorkerTask(private val bitmapCallback: BitmapCallback, private val vWidth: Int, private val vHeight: Int) : AsyncTask<String, Void, Bitmap>() {

        // Decode image in background.
        override fun doInBackground(vararg params: String): Bitmap {
            val bitmap = decodeSampledBitmapFromFile(params[1], vWidth, vHeight)
            addBitmapToMemoryCache(params[0], bitmap)
            return bitmap
        }

        override fun onPostExecute(result: Bitmap) = bitmapCallback.loadImageSuccess(result)
    }

    interface BitmapCallback {
        fun loadImageSuccess(bitmap: Bitmap)
    }

    private const val DEFAULT_SIZE = 400

    private fun calculateInSampleSize(options: BitmapFactory.Options, minWidth: Int, minHeight: Int): Int {
        val reqHeight: Int = if (minHeight > 0) minHeight else DEFAULT_SIZE
        val reqWidth = if (minWidth > 0) minWidth else DEFAULT_SIZE

        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > minHeight || width > minWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun decodeSampledBitmapFromFile(file: String, reqWidth: Int, reqHeight: Int): Bitmap {
        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        options.inPreferredConfig = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Bitmap.Config.HARDWARE else Bitmap.Config.RGB_565
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(file, options)
    }
}

