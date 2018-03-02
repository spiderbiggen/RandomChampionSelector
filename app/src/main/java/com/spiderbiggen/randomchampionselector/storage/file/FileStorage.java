package com.spiderbiggen.randomchampionselector.storage.file;

import android.content.Context;

import java.io.File;

/**
 * Created on 27-2-2018.
 *
 * @author Stefan Breetveld
 */
public class FileStorage {

    private Context context;

    public FileStorage(Context context) {
        this.context = context;
    }

    public File getBaseDir() {
        return context.getFilesDir();
    }

    public File getSubDir(String relPath) {
        return context.getDir(relPath.replaceAll("/", "_"), Context.MODE_PRIVATE);
    }

    public File getChampionSquareDir() {
        return getSubDir("img/champion/square");
    }

    public File getChampionSplashDir() {
        return getSubDir("img/champion/splash");
    }

    public File getChampionLoadingDir() {
        return getSubDir("img/champion/loading");
    }
}
