package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;

import java.io.IOException;

import io.reactivex.disposables.Disposable;


public class ChampionActivity extends ButtonActivity {

    public static final String CHAMPION_KEY = "champion";
    private static final String TAG = ChampionActivity.class.getSimpleName();
    private static final ImageType imageType = ImageType.SPLASH;
    private int championKey = -1;
    private Disposable championFlowable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
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

    private void loadChampion() {
        dispose();
        championFlowable = championKey < 0 ? reRollChampion(null) : getChampionById(championKey);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CHAMPION_KEY, championKey);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        loadChampion();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dispose();
        super.onPause();
    }

    public Disposable reRollChampion(String type) {
        dispose();
        return DatabaseManager.getInstance().findRandomChampion(this::setChampion, type, championKey);
    }

    public Disposable getChampionById(int id) {
        dispose();
        return DatabaseManager.getInstance().findChampion(this::setChampion, id);
    }

    public void setChampion(Champion champion) {
        if (champion == null) {
            championKey = -1;
            return;
        }
        championKey = champion.getKey();
        Bitmap bitmap = null;
        try {
            bitmap = new DDragon(this).getChampionBitmap(champion, imageType);
        } catch (IOException e) {
            Log.e(TAG, "setChampion: ", e);
        }
        ImageView bg = findViewById(R.id.champion_background);
        if (bg != null && bitmap != null) {
            bg.setImageBitmap(bitmap);
        }
        CollapsingToolbarLayout actionBar = findViewById(R.id.toolbar_layout);
        if (actionBar != null) {
            actionBar.setTitle(champion.getName());
        }
        TextView title = findViewById(R.id.champion_title);
        title.setText(champion.getCapitalizedTitle());
        TextView blurb = findViewById(R.id.champion_blurb);
        blurb.setText(champion.getLore());
    }

    private void dispose() {
        if (championFlowable != null && !championFlowable.isDisposed()) {
            championFlowable.dispose();
        }
    }

    @Override
    public void openChampion(View view) {
        reRollChampion(null);
    }
}
