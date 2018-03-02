package com.spiderbiggen.randomchampionselector;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.DownloadImageTask;
import com.spiderbiggen.randomchampionselector.fragments.ChampionActivity;
import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.util.async.Progress;
import com.spiderbiggen.randomchampionselector.util.internet.DownloadCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LoaderActivity extends AppCompatActivity implements IDataInteractor.OnFinishedListener {

    private static final String TAG = LoaderActivity.class.getSimpleName();
    DatabaseManager databaseManager;
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
        downloadChampions();
    }

    private void downloadChampions() {
        dDragon.getChampionList(new ChampionsCallback(this));
    }

    private void downloadAllImages(List<Champion> champions) {
        dDragon.downloadAllimages(champions, new ImageCallback(this));
    }

    private void openMainScreen(ArrayList<String> message) {
        Intent intent = new Intent(this, ChampionActivity.class);
        intent.putStringArrayListExtra(ButtonActivity.SPINNER_DATA_KEY, message);
        startActivity(intent);
    }

    public void throwException(Exception exception) {
        Log.e(TAG, "throwException: ", exception);
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
    public void onFinishedChampionListLoad(List<Champion> champions) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedChampionLoad(Champion champion) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedRoleListLoad(List<String> roles) {
        Log.d(TAG, "onFinishedRoleListLoad() called with: roles = [" + roles + "]");
        openMainScreen(new ArrayList<>(roles));
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    private static class ImageCallback implements DownloadCallback<DownloadImageTask.Entry[]> {

        private LoaderActivity activity;

        private ImageCallback(LoaderActivity activity) {
            this.activity = activity;
        }

        @Override
        public void throwException(Exception exception) {
            activity.throwException(exception);
        }

        @Override
        public void updateFromDownload(DownloadImageTask.Entry[] result) {
            //Do nothing;
        }

        @Override
        public void onProgressUpdate(int progressCode, int progress, int progressMax) {
            activity.onProgressUpdate(progressCode, progress, progressMax);
        }

        @Override
        public void finishExecution() {
            activity.databaseManager.findRoleList(activity);
        }
    }

    private static class ChampionsCallback implements DownloadCallback<List<Champion>> {

        private List<Champion> champions;
        private LoaderActivity activity;

        private ChampionsCallback(LoaderActivity loaderActivity) {
            this.activity = loaderActivity;
        }

        @Override
        public void throwException(Exception exception) {
            activity.throwException(exception);
        }

        @Override
        public void updateFromDownload(List<Champion> result) {
            System.out.println(result.get(0));
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
