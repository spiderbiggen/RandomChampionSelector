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
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

public class LoaderActivity extends AppCompatActivity implements ProgressCallback {

    private static final String TAG = LoaderActivity.class.getSimpleName();
    private DatabaseManager databaseManager;
    private DDragon dDragon;
    private List<Disposable> disposables = new ArrayList<>();
    private Progress lastProgressId = Progress.IDLE;

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

    @Override
    protected void onDestroy() {
        for (Disposable disposable : disposables) {
            if (!disposable.isDisposed())
                disposable.dispose();
        }
        super.onDestroy();
    }

    private void startLoading() {
        Log.d(TAG, "startLoading: ?");
        disposables.add(dDragon.updateVersion(this::downloadChampions));
    }

    private void downloadChampions() {
        Log.d(TAG, "downloadChampions: ?");
        disposables.add(dDragon.getChampionList(this::downloadAllImages));
    }

    private void downloadAllImages(List<Champion> champions) {
        disposables.add(databaseManager.addChampions(champions));
        try {
            disposables.add(dDragon.downloadAllImages(champions, this, this::fetchAllRoles));
        } catch (IOException e) {
            onProgressUpdate(Progress.ERROR, 0, 0);
            setProgressText(e.getMessage());
            Log.e(TAG, "downloadAllImages: ", e);
        }
    }

    private void fetchAllRoles() {
        disposables.add(databaseManager.findRoleList(this::openMainScreen));
    }

    private void openMainScreen(Collection<String> message) {
        Intent intent = new Intent(this, ListChampionsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProgressUpdate(Progress progressCode, int progress, int progressMax) {
        if (progressCode != Progress.ERROR && progressCode.getPriority() < lastProgressId.getPriority())
            return;

        if (progressCode != Progress.ERROR) lastProgressId = progressCode;

        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (progressCode == Progress.ERROR) {
            Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        }

        progressBar.setIndeterminate(progressCode.getPriority() < Progress.DOWNLOAD_SUCCESS.getPriority());
        progressBar.setProgress(progress);
        progressBar.setMax(progressMax);
        updateProgressText(progressCode, progress, progressMax);
    }

    @Override
    public void finishExecution() {
        lastProgressId = Progress.CONNECT_SUCCESS;
    }

    private void updateProgressText(Progress progressCode, int progress, int progressMax) {
        String text = "";
        switch (progressCode) {
            case ERROR:
                text = "Error!";
                break;
            case IDLE:
                text = "Checking For Updates";
                break;
            case CONNECT_SUCCESS:
            case GET_INPUT_STREAM_SUCCESS:
                text = "Setting up Connection";
                break;
            case PROCESS_INPUT_STREAM_IN_PROGRESS:
            case PROCESS_INPUT_STREAM_SUCCESS:
                text = "Processing...";
                break;
            case DOWNLOAD_SUCCESS:
                text = String.format(Locale.ENGLISH, "Downloaded %d/%d", progress, progressMax);
                break;
        }
        setProgressText(text);
    }

    private void setProgressText(String text) {
        TextView textView = findViewById(R.id.progressText);
        textView.setText(text);
    }

}
