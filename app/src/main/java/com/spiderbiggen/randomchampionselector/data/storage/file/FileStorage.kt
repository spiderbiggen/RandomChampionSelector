package com.spiderbiggen.randomchampionselector.data.storage.file

import android.content.Context
import com.spiderbiggen.randomchampionselector.model.IRequiresContext

import java.io.File
import java.io.IOException

/**
 * Manage access to android file storage.
 *
 * @author Stefan Breetveld
 */
object FileStorage : IRequiresContext {
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
     * @throws IOException if the directory can't be created
     */
    private val imageDir: File
        @Throws(IOException::class)
        get() = getSubDir(IMG_ROOT_DIR)

    /**
     * Create a directory for storing Champion Images.
     *
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    val championImageDir: File
        @Throws(IOException::class)
        get() = getSubDir(imageDir, CHAMPION_REL_DIR)

    /**
     * Create a directory.
     *
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    @Throws(IOException::class)
    private fun getSubDir(child: String): File {
        return getSubDir(root, child)
    }

    /**
     * Create a directory.
     *
     * @param root  root directory
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    @Throws(IOException::class)
    private fun getSubDir(root: File, child: String): File {
        val file = File(root, child)
        if (file.exists() || file.mkdirs()) {
            return file
        }
        throw IOException("Can't find or create a directory for " + file.absolutePath)
    }

    /**
     * Deletes files and directories.
     * If the given root is a file it will delete it.
     * If the given root is a directory it will loop over the files in the directory and recursively call this function.
     *
     * @param root the root file/dir to delete
     * @return true if everything was successfully deleted
     */
    fun deleteRecursive(root: File): Boolean {
        var deleted = true
        if (root.isDirectory) {
            root.listFiles().forEach { child -> deleted = deleted and deleteRecursive(child) }
        }
        return deleted and root.delete()
    }
}
