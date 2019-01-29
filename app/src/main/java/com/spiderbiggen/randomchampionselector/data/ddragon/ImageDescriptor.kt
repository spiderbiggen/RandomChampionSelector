package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.IOException

/**
 * Created on 15-3-2018.
 *
 * @author Stefan Breetveld
 */
data class ImageDescriptor(var champion: String, var file: File) {

    private var valid = false
    val invalid
        get() = !valid

    @Throws(IOException::class)
    fun verifySavedFile(): ImageDescriptor {
        if (file.exists()) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, options)
            valid = options.outHeight > 0 && options.outHeight > 0
        }
        return this
    }

    @Throws(IOException::class)
    fun download(): Bitmap? {
        if (invalid) {
            val body = DDragon.getChampionImage(champion, 0).blockingGet()
            body?.byteStream()?.use {
                return BitmapFactory.decodeStream(it)
            }
        }
        return null
    }
}
