package com.spiderbiggen.randomchampionselector.ddragon.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.spiderbiggen.randomchampionselector.ddragon.callback.ImageCallback;
import com.spiderbiggen.randomchampionselector.model.Champion;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created on 27-2-2018.
 *
 * @author Stefan Breetveld
 */
public class LoadImageTask extends AsyncTask<LoadImageTask.Entry, Void, LoadImageTask.Entry> {

    private static final String TAG = DownloadChampionsTask.class.getSimpleName();
    private Exception exception;

    public LoadImageTask() {
    }

    @Override
    protected Entry doInBackground(Entry... params) {
        try {
            if (params != null && params.length == 1) {
                if (isCancelled()) {
                    return null;
                }
                Entry entry = params[0];
                File file = entry.getFile();
                Bitmap bitmap = null;
                if (file.exists()) {
                    try (FileInputStream stream = new FileInputStream(file)) {
                        bitmap = BitmapFactory.decodeStream(stream);
                    }
                }
                entry.setResult(bitmap);
                return entry;
            } else {
                throw new IllegalArgumentException("Too many arguments");
            }
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Entry result) {
        super.onPostExecute(result);
        if (exception != null) {
            Log.e(TAG, "onPostExecute: ", exception);
        }
        result.onFinished();
    }

    public static class Entry {
        private final File file;
        private final Champion champion;
        private final ImageType type;
        private final ImageCallback callback;
        private Bitmap result;

        public Entry(@NonNull File file, @NonNull Champion champion, @NonNull ImageType imageType, ImageCallback callback) {
            this.file = file;
            this.champion = champion;
            this.type = imageType;
            this.callback = callback;
        }

        public ImageType getType() {
            return type;
        }

        private void setResult(Bitmap result) {
            this.result = result;
        }

        public Champion getChampion() {
            return champion;
        }

        public File getFile() {
            return file;
        }

        public void onFinished() {
            if (callback == null) return;
            callback.setImage(result, champion, type);
        }

        @Override
        public String toString() {
            return "Entry{" +
                    ", file=" + file +
                    ", champion=" + champion +
                    '}';
        }
    }

}
