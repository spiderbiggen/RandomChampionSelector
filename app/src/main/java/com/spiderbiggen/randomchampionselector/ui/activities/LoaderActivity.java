package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.ImageDescriptor;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.Disposable;

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
        FileStorage.getInstance().setRootFromContext(this);
        dDragon = DDragon.getInstance();
        dDragon.setPreferences(PreferenceManager.getDefaultSharedPreferences(this));
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
        disposables.add(dDragon.updateVersion(this::downloadChampions, this::catchError));
    }

    private void downloadChampions() {
        disposables.add(dDragon.getChampionList(this::handleChampionList, this::catchError));
    }

    private void handleChampionList(List<Champion> champions) {
        disposables.add(databaseManager.addChampions(champions));
        try {
            disposables.add(dDragon.verifyImages(champions, this, this::downloadMissingOrCorruptImages, this::catchError));
        } catch (IOException e) {
            catchError(e);
        }
    }

    private void catchError(Throwable t) {
        Log.e(TAG, "catchError: ", t);
        onError();
    }

    private void downloadMissingOrCorruptImages(List<ImageDescriptor> champions) {
        try {
            disposables.add(dDragon.downloadAllImages(champions, this, this::openMainScreen, this::catchError));
        } catch (IOException e) {
            catchError(e);
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

        TextView textView = findViewById(R.id.progressText);
        textView.setText(getString(progress.getStringResource(), progressCount, progressMax));
    }

    @Override
    public void finishExecution() {

    }

}
