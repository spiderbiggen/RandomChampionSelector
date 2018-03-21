package com.spiderbiggen.randomchampionselector.ui.activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;

import java.io.IOException;

import io.reactivex.disposables.Disposable;

import static com.spiderbiggen.randomchampionselector.ddragon.DDragon.createDDragon;


public class ChampionActivity extends ButtonActivity {

    public static final String CHAMPION_KEY = "champion";
    private static final String TAG = ChampionActivity.class.getSimpleName();
    private static final ImageType imageType = ImageType.SPLASH;
    private int championKey = -1;
    private Disposable championFlowable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        supportPostponeEnterTransition();
        setContentView(R.layout.activity_champion);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null) {
            championKey = savedInstanceState.getInt(CHAMPION_KEY);
        }
        Intent intent = getIntent();
        championKey = intent.getIntExtra(CHAMPION_KEY, championKey);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CHAMPION_KEY, championKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        DatabaseManager dbInstance = DatabaseManager.getInstance();
        championFlowable = championKey < 0
                ? dbInstance.findRandomChampion(this::setChampion, null, championKey)
                : dbInstance.findChampion(this::setChampion, championKey);
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (championFlowable != null && !championFlowable.isDisposed()) {
            championFlowable.dispose();
        }
        super.onPause();
    }

    public void setChampion(Champion champion) {
        if (champion == null) {
            championKey = -1;
            return;
        }
        championKey = champion.getKey();
        Bitmap bitmap = null;
        try {
            bitmap = createDDragon(this).getChampionBitmap(champion, imageType);
        } catch (IOException e) {
            Log.e(TAG, "setChampion: ", e);
        }
        ImageView bg = findViewById(R.id.champion_splash);
        if (bg != null && bitmap != null) {
            bg.setImageBitmap(bitmap);
        }
        TextView name = findViewById(R.id.champion_name);
        name.setText(champion.getName());
        TextView title = findViewById(R.id.champion_title);
        title.setText(champion.getCapitalizedTitle());
        TextView blurb = findViewById(R.id.champion_blurb);
        blurb.setText(champion.getLore());
        supportStartPostponedEnterTransition();
    }
}
