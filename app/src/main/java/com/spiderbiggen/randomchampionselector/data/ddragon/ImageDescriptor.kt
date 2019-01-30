package com.spiderbiggen.randomchampionselector.data.ddragon

import android.graphics.BitmapFactory
import java.io.File

/**
 * Created on 15-3-2018.
 *
 * @author Stefan Breetveld
 */
data class ImageDescriptor(var champion: String, var file: File) {

    var valid = false
        private set

    fun verifySavedFile(): ImageDescriptor {
        if (file.exists()) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.path, options)
            valid = options.outHeight > 0 && options.outWidth > 0
        }
        return this
    }
}
