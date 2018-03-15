package com.spiderbiggen.randomchampionselector.storage.file;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Manage access to android file storage
 *
 * @author Stefan Breetveld
 */
public class FileStorage {

    private static final String ROOT_PATH = "root";
    private static final String IMG_ROOT_DIR = "img";
    private static final String CHAMPION_REL_DIR = "champion";

    private File root;

    public FileStorage(Context context) {
        this.root = context.getDir(ROOT_PATH, Context.MODE_PRIVATE);
    }

    /**
     * Create a directory.
     *
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    public File getSubDir(String child) throws IOException {
        return getSubDir(this.root, child);
    }

    /**
     * Create a directory.
     *
     * @param root root directory
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    public File getSubDir(String root, String child) throws IOException {
        return getSubDir(getSubDir(root), child);
    }

    /**
     * Create a directory.
     *
     * @param root root directory
     * @param child the effective directory
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    public File getSubDir(File root, String child) throws IOException {
        File file = new File(root, child);
        if (file.exists() || file.mkdirs()) {
            return file;
        }
        throw new IOException("Can't find or create a directory for " + file.getAbsolutePath());
    }

    /**
     * Create a directory for storing any Images.
     *
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    public File getImageDir() throws IOException {
        return getSubDir(IMG_ROOT_DIR);
    }

    /**
     * Create a directory for storing Champion Images.
     *
     * @return a directory that is guaranteed to exist
     * @throws IOException if the directory can't be created
     */
    public File getChampionImageDir() throws IOException {
        return getSubDir(getImageDir(), CHAMPION_REL_DIR);
    }

    /**
     * Deletes files and directories.
     * If the given root is a file it will delete it.
     * If the given root is a directory it will loop over the files in the directory and recursively call this function.
     *
     * @param root the root file/dir to delete
     * @return true if everything was successfully deleted
     */
    public boolean deleteRecursive(File root) {
        boolean deleted = true;
        if (root.isDirectory()) {
            for (File child : root.listFiles()) {
                deleted &= deleteRecursive(child);
            }
        }
        return deleted & root.delete();
    }
}
