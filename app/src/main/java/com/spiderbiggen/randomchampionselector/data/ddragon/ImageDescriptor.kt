package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import io.reactivex.Maybe
import io.reactivex.schedulers.Schedulers
import java.io.File

/**
 * Created on 15-3-2018.
 *
 * @author Stefan Breetveld
 */
data class ImageDescriptor(var champion: String, var file: File) {

    private var valid = false
    val invalid
        get() = !valid

    fun verifySavedFile(): ImageDescriptor {
        if (file.exists()) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, options)
            valid = options.outHeight > 0 && options.outWidth > 0
        }
        return this
    }

    fun download(): Maybe<Bitmap> {
        return if (invalid) {
            DDragon.getChampionImage(champion, 0)
                    .subscribeOn(Schedulers.io())
                    .map { body -> body.byteStream().use { BitmapFactory.decodeStream(it) } }
                    .doOnError { Log.d("ImageDescriptor", "lmao", it) }
        } else {
            Maybe.empty<Bitmap>()
        }
    }
}
