package com.spiderbiggen.randomchampionselector;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.activities.ListChampionsActivity;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadImageTask;
import com.spiderbiggen.randomchampionselector.activities.ChampionActivity;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor;
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoaderActivity extends AppCompatActivity implements IDataInteractor.OnFinishedRolesListener, ProgressCallback {

    private static final String TAG = LoaderActivity.class.getSimpleName();
    private DatabaseManager databaseManager;
    private DDragon dDragon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        databaseManager = DatabaseManager.getInstance();
        databaseManager.useContext(getApplicationContext());
        dDragon = new DDragon(this);
        startLoading();
    }

    private void startLoading() {
        dDragon.updateVersion(this);
    }

    // Only called by version endpoint
    @Override
    public void finishExecution() {
        dDragon.getChampionList(new ChampionsCallback(this));
    }

    private void downloadAllImages(List<Champion> champions) {
        dDragon.downloadAllImages(champions, new ImageCallback(this));
    }

    public void onProgressUpdate(int progressCode, int progress, int progressMax) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (progressCode == Progress.ERROR) {
            Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        }

        progressBar.setIndeterminate(progressCode < Progress.DOWNLOAD_SUCCESS);
        progressBar.setProgress(progress);
        progressBar.setMax(progressMax);
        updateProgressText(progressCode, progress, progressMax);
    }

    private void openMainScreen(ArrayList<String> message) {
        Intent intent = new Intent(this, ListChampionsActivity.class);
        intent.putStringArrayListExtra(ButtonActivity.SPINNER_DATA_KEY, message);
        startActivity(intent);
    }

    public void throwException(Exception exception) {
        Log.e(TAG, "handleException: ", exception);
    }

    private void updateProgressText(int progressCode, int progress, int progressMax) {
        String text = "";
        switch (progressCode) {
            case Progress.ERROR:
                text = "Error!";
                break;
            case Progress.CONNECT_SUCCESS:
            case Progress.GET_INPUT_STREAM_SUCCESS:
                text = "Setting up Connection";
                break;
            case Progress.PROCESS_INPUT_STREAM_IN_PROGRESS:
            case Progress.PROCESS_INPUT_STREAM_SUCCESS:
                text = "Processing...";
                break;
            case Progress.DOWNLOAD_SUCCESS:
                text = String.format(Locale.ENGLISH, "Downloaded %d/%d", progress, progressMax);
                break;
        }
        TextView textView = findViewById(R.id.progressText);
        textView.setText(text);
    }

    @Override
    public void onFinishedRoleListLoad(List<String> roles) {
        Log.d(TAG, "onFinishedRoleListLoad() called with: roles = [" + roles + "]");
        openMainScreen(new ArrayList<>(roles));
    }

    private static class ImageCallback implements DownloadCallback<DownloadImageTask.Entry[]> {

        private LoaderActivity activity;
        private boolean finished;

        private ImageCallback(LoaderActivity activity) {
            this.activity = activity;
        }

        @Override
        public void handleException(Exception exception) {
            activity.throwException(exception);
        }

        @Override
        public void updateFromDownload(DownloadImageTask.Entry[] result) {
            //Do nothing;
        }

        @Override
        public void onProgressUpdate(int progressCode, int progress, int progressMax) {
            finished = progress == progressMax;
            activity.onProgressUpdate(progressCode, progress, progressMax);
        }

        @Override
        public void finishExecution() {
            if (finished) {
                activity.databaseManager.findRoleList(activity);
            }
        }
    }

    private static class ChampionsCallback implements DownloadCallback<List<Champion>> {

        private List<Champion> champions;
        private LoaderActivity activity;

        private ChampionsCallback(LoaderActivity loaderActivity) {
            this.activity = loaderActivity;
        }

        @Override
        public void handleException(Exception exception) {
            activity.throwException(exception);
        }

        @Override
        public void updateFromDownload(List<Champion> result) {
            champions = result;
        }

        @Override
        public void onProgressUpdate(int progressCode, int progress, int progressMax) {
            activity.onProgressUpdate(progressCode, progress, progressMax);
        }

        @Override
        public void finishExecution() {
            activity.downloadAllImages(champions);
        }
    }
}
