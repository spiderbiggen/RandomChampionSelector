package com.spiderbiggen.randomchampionselector.ddragon.callback;

import android.graphics.Bitmap;

import com.spiderbiggen.randomchampionselector.ddragon.tasks.ImageType;
import com.spiderbiggen.randomchampionselector.model.Champion;

/**
 * Created on 27-2-2018.
 *
 * @author Stefan Breetveld
 */
public interface ImageCallback {
    void setImage(Bitmap bitmap, Champion champion, ImageType type);
}
