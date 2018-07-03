package com.spiderbiggen.randomchampionselector.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.ImageDescriptor;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.file.FileStorage;
import com.spiderbiggen.randomchampionselector.util.async.ProgressCallback;

import io.reactivex.disposables.Disposable;

public class LoaderActivity extends AppCompatActivity implements ProgressCallback {

    public static final String FORCE_REFRESH = "FORCE_REFRESH";
    public static final int DEFAULT_TIME_MILLIS = -1;

    private static final String TAG = LoaderActivity.class.getSimpleName();

    private DatabaseManager databaseManager;
    private DDragon dDragon;
    private List<Disposable> disposables = new ArrayList<>();

    public static Intent createStartIntent(Context context, boolean forceRefresh) {
        Intent intent = new Intent(context, LoaderActivity.class);
        intent
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(LoaderActivity.FORCE_REFRESH, forceRefresh);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initStorage();
        initApis();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldRefresh = getIntent().getBooleanExtra(FORCE_REFRESH, false);
        if (!shouldRefresh) {
            long timeMillis = preferences.getLong(getString(R.string.pref_last_sync_key), DEFAULT_TIME_MILLIS);
            int syncTime = Integer.parseInt(preferences.getString(getString(R.string.pref_title_sync_frequency),
                getString(R.string.pref_sync_frequency_default)));
            Date nextSync = new Date(timeMillis + syncTime);
            Date now = new Date();
            shouldRefresh = timeMillis == DEFAULT_TIME_MILLIS || now.after(nextSync);
        }
        if (shouldRefresh) {
            setContentView(R.layout.activity_loader);
            startLoading();
        } else {
            openMainScreen();
        }
    }

    private void initApis() {
        Context applicationContext = getApplicationContext();
        dDragon = DDragon.getInstance();
        dDragon.useContext(applicationContext);
    }

    private void initStorage() {
        Context applicationContext = getApplicationContext();
        FileStorage.getInstance().useContext(applicationContext);
        databaseManager = DatabaseManager.getInstance();
        databaseManager.useContext(applicationContext);

    }

    @Override
    protected void onDestroy() {
        for (Disposable disposable : disposables) {
            if (!disposable.isDisposed()) {
                disposable.dispose();
            }
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
        disposables.add(dDragon
            .verifyImages(champions, this, this::downloadMissingOrCorruptImages, this::catchError));
    }

    private void catchError(Throwable t) {
        Log.e(TAG, "catchError: ", t);
        onProgressUpdate(Progress.ERROR);
    }

    private void downloadMissingOrCorruptImages(List<ImageDescriptor> champions) {
        disposables.add(dDragon
            .downloadAllImages(champions, this, this::openMainScreen, this::catchError));
    }

    private void openMainScreen() {
        PreferenceManager.getDefaultSharedPreferences(this)
            .edit()
            .putLong(getString(R.string.pref_last_sync_key), new Date().getTime())
            .apply();
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
        //Empty
    }

}
