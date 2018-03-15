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
import java.util.List;
import java.util.Locale;

import io.reactivex.disposables.Disposable;

import static com.spiderbiggen.randomchampionselector.ddragon.DDragon.createDDragon;

public class LoaderActivity extends AppCompatActivity implements ProgressCallback {

    private static final String TAG = LoaderActivity.class.getSimpleName();
    private DatabaseManager databaseManager;
    private DDragon dDragon;
    private List<Disposable> disposables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);

        databaseManager = DatabaseManager.getInstance();
        databaseManager.useContext(getApplicationContext());
        dDragon = createDDragon(this);
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
        disposables.add(dDragon.getChampionList(this::verifyImages));
    }

    private void verifyImages(List<Champion> champions) {
        try {
            disposables.add(dDragon.verifyImages(champions, this, this::downloadAllImages));
        } catch (IOException e) {
            onError();
            setProgressText(e.getMessage());
            Log.e(TAG, "verifyImages: ", e);
        }
    }

    private void downloadAllImages(List<Champion> champions) {
        disposables.add(databaseManager.addChampions(champions));
        try {
            disposables.add(dDragon.downloadAllImages(champions, this, this::openMainScreen));
        } catch (IOException e) {
            onError();
            setProgressText(e.getMessage());
            Log.e(TAG, "downloadAllImages: ", e);
        }
    }

    private void openMainScreen() {
        Intent intent = new Intent(this, ListChampionsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onProgressUpdate(Progress progress, int progressCount, int progressMax) {
        ProgressBar progressBar = findViewById(R.id.progressBar);

        if (progress == Progress.ERROR) {
            Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
            progressDrawable.setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            progressBar.setProgressDrawable(progressDrawable);
        }

        progressBar.setIndeterminate(progress.isIndeterminate());
        progressBar.setProgress(progressCount);
        progressBar.setMax(progressMax);
        updateProgressText(progress, progressCount, progressMax);
    }

    @Override
    public void finishExecution() {

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
                text = String.format(Locale.ENGLISH, "Downloading %d/%d", progress, progressMax);
                break;
            case VERIFY_SUCCESS:
                text = String.format(Locale.ENGLISH, "Verifying %d/%d", progress, progressMax);
        }
        setProgressText(text);
    }

    private void setProgressText(String text) {
        TextView textView = findViewById(R.id.progressText);
        textView.setText(text);
    }

}
