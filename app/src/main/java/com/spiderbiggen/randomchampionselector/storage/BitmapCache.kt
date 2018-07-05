package com.spiderbiggen.randomchampionselector.storage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.support.v4.util.LruCache
import android.widget.ImageView
import java.io.File


/**
 * Created on 5-7-2018.
 * @author Stefan Breetveld
 */
object BitmapCache {
    private val cacheSize = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private var mMemoryCache = object : LruCache<String, Bitmap>(cacheSize) {
        override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
            // The cache size will be measured in kilobytes rather than
            // number of items.
            return bitmap!!.byteCount / 1024
        }
    }

    fun loadBitmap(key: String, file: File, imageView: ImageView) {
        val bitmap = getBitmapFromMemCache(key)
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap)
        } else {
            imageView.setImageBitmap(null)
            val task = BitmapWorkerTask(imageView)
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

    class BitmapWorkerTask(private val imageView: ImageView) : AsyncTask<String, Void, Bitmap>() {
        // TODO create callback instead of passing imageview.
        private val reqWidth: Int
            get() = if (imageView.width > 0) imageView.width else 400
        private val reqHeight: Int
            get() = if (imageView.height > 0) imageView.height else 400

        // Decode image in background.
        override fun doInBackground(vararg params: String): Bitmap {
            val bitmap = decodeSampledBitmapFromFile(params[1], reqWidth, reqHeight)
            addBitmapToMemoryCache(params[0], bitmap)
            return bitmap
        }

        override fun onPostExecute(result: Bitmap?) = imageView.setImageBitmap(result)

        private fun calculateInSampleSize(options: BitmapFactory.Options, minWidth: Int, minHeight: Int): Int {
            // Raw height and width of image
            val height = options.outHeight
            val width = options.outWidth
            var inSampleSize = 1

            if (height > minHeight || width > minWidth) {

                val halfHeight = height / 2
                val halfWidth = width / 2

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while (halfHeight / inSampleSize >= minHeight && halfWidth / inSampleSize >= minWidth) {
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

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeFile(file, options)
        }
    }

}