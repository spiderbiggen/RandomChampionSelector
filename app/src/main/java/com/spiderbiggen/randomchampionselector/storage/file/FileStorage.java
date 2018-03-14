package com.spiderbiggen.randomchampionselector.storage.file;

import android.content.Context;

import java.io.File;

/**
 * Manage access to android file storage
 *
 * @author Stefan Breetveld
 */
public class FileStorage {

    private static final String IMG_ROOT_DIR = "img";
    private static final String CHAMPION_REL_DIR = "champion";

    private Context context;

    private FileStorage(Context context) {
        this.context = context;
    }

    public static FileStorage getInstance(Context context) {
        return new FileStorage(context);
    }

    public File getRootDir(String root) {
        return context.getDir(root.replaceAll("/", "_"), Context.MODE_PRIVATE);
    }

    public File getSubDir(String root, String child) {
        return getSubDir(getRootDir(root), child);
    }

    public File getSubDir(File root, String child) {
        File file = new File(root, child);
        if (file.exists() || file.mkdirs()) {
            return file;
        }
        return null;
    }

    public File getImageDir() {
        return getRootDir(IMG_ROOT_DIR);
    }

    public File getChampionImageDir() {
        return getSubDir(getImageDir(), CHAMPION_REL_DIR);
    }

    public boolean deleteChampionImageDir() {
        return deleteRecursive(getChampionImageDir());
    }

    /**
     * Deletes all files in the images folder.
     *
     * @return true if all images were successfully deleted
     */
    public boolean deleteImages() {
        return deleteRecursive(getImageDir());
    }

    /**
     * Deletes files and directories.
     * If the given root is a file it will delete it.
     * If the given root is a directory it will loop over the files in the directory and recursively call this function.
     *
     * @param root the root file/dir to delete
     * @return true if everything was successfully deleted
     */
    private boolean deleteRecursive(File root) {
        boolean deleted = true;
        if (root.isDirectory()) {
            for (File child : root.listFiles()) {
                deleted &= deleteRecursive(child);
            }
        }
        return deleted & root.delete();
    }
}
