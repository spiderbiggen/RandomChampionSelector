package com.spiderbiggen.randomchampionselector.data.storage.file

import android.content.Context
import com.spiderbiggen.randomchampionselector.domain.Champion
import com.spiderbiggen.randomchampionselector.model.Contextual

import java.io.File
import java.io.IOException

/**
 * Manage access to android file storage.
 *
 * @author Stefan Breetveld
 */
object FileStorage : Contextual {
    private const val IMG_ROOT_DIR = "img"

    private const val CHAMPION_REL_DIR = "champion"
    /**
     * The root directory.
     */
    private lateinit var root: File

    override fun useContext(context: Context) {
        if (!this::root.isInitialized) {
            root = context.applicationContext.filesDir
        }
    }


    /**
     * Create a directory for storing any Images.
     *
     * @return a directory that is guaranteed to exist
     */
    private val imageDir: File
        get() = getSubDir(IMG_ROOT_DIR)

    /**
     * Create a directory for storing Champion Images.
     *
     * @return a directory that is guaranteed to exist
     */
    private val championImageDir: File
        get() = getSubDir(imageDir, CHAMPION_REL_DIR)

    /**
     * Create a directory.
     *
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     */
    private fun getSubDir(child: String): File {
        return getSubDir(root, child)
    }

    /**
     * Create a directory.
     *
     * @param root  root directory
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     */
    private fun getSubDir(root: File, child: String): File {
        val file = File(root, child)
        if (file.exists() || file.mkdirs()) {
            return file
        }
        throw IOException("Can't find or create a directory for " + file.absolutePath)
    }

    /**
     * Delete everything in [championImageDir].
     * @see deleteRecursively
     */
    fun deleteChampionImages(): Boolean = championImageDir.deleteRecursively()

    /**
     * Get the location of the bitmap for this champion
     */
    val Champion.championBitmap: File
        get() = File(championImageDir, id)

}
