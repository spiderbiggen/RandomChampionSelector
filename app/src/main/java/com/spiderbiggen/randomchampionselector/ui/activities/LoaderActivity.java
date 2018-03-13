package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor;
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class LoaderActivity extends AppCompatActivity implements IDataInteractor.OnFinishedRolesListener {

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
        dDragon.updateVersion(this::downloadChampions);
    }

    private void downloadChampions(String[] strings) {
        dDragon.getChampionList(this::downloadAllImages);
    }

    private void downloadAllImages(List<Champion> champions) {
        champions.removeAll(Collections.<Champion>singletonList(null));
        databaseManager.addChampions(champions);
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

    private static class ImageCallback implements ProgressCallback {

        private LoaderActivity activity;
        private int lastProgressId;

        private ImageCallback(LoaderActivity activity) {
            this.activity = activity;
        }

        @Override
        public void onProgressUpdate(int progressCode, int progress, int progressMax) {
            if (progressCode == Progress.ERROR || progressCode >= lastProgressId) {
                activity.onProgressUpdate(progressCode, progress, progressMax);
                if (progressCode != Progress.ERROR) {
                    lastProgressId = progressCode;
                }
            }
        }

        @Override
        public void finishExecution() {
            activity.databaseManager.findRoleList(activity);
        }
    }
}
