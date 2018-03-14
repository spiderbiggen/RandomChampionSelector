package com.spiderbiggen.randomchampionselector.storage.file;

import android.content.Context;
import android.graphics.Bitmap;

import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;

import java.io.File;
import java.io.IOException;

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

    public File getChampionImageFile(Champion champion, ImageType imageType, Bitmap.CompressFormat format) {
        return new File(getSubDir("img/champion"), String.format("%s_%s.%s", champion.getId(), imageType.name().toLowerCase(), format.name().toLowerCase()));
    }

    public boolean deleteImages() throws IOException {
        File dir = getSubDir("img/champion");
        return dir.getCanonicalFile().delete();
    }
}
