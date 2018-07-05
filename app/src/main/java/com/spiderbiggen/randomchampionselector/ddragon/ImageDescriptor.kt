package com.spiderbiggen.randomchampionselector.ddragon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.spiderbiggen.randomchampionselector.model.ImageType
import java.io.File
import java.io.FileInputStream
import java.io.IOException

/**
 * Created on 15-3-2018.
 *
 * @author Stefan Breetveld
 */
class ImageDescriptor(var champion: String, var type: ImageType, var file: File) {
    private var valid = false
    val invalid
        get() = !valid

    @Throws(IOException::class)
    fun verifySavedFile(): ImageDescriptor {
        if (file.exists()) {
            FileInputStream(file).use { stream -> valid = (BitmapFactory.decodeStream(stream) != null) }
        }
        return this
    }

    @Throws(IOException::class)
    fun verifyDownload(compressFormat: Bitmap.CompressFormat, quality: Int): ImageDescriptor {
        if (invalid) {
            val body = DDragon.getChampionCall(champion, type, 0).blockingGet()
            body?.byteStream()?.use {
                val bitmap = BitmapFactory.decodeStream(it)
                DDragon.saveBitmap(file, bitmap, compressFormat, quality)
            }
        }
        return this
    }
}
