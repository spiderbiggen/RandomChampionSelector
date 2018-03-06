package com.spiderbiggen.randomchampionselector.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.ButtonActivity;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.model.ImageType;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor;


public class ChampionActivity extends ButtonActivity implements IDataInteractor.OnFinishedChampionListener {

    public static final String CHAMPION_KEY = "champion";
    private static final String TAG = ChampionActivity.class.getSimpleName();
    private Champion champion = null;
    private ImageType imageType = ImageType.LOADING;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_champion);

        if (savedInstanceState != null) {
            champion = (Champion) savedInstanceState.getSerializable(CHAMPION_KEY);
        }
        Intent intent = getIntent();
        champion = intent.hasExtra(CHAMPION_KEY) ? (Champion) intent.getSerializableExtra(CHAMPION_KEY) : champion;
        super.onCreate(savedInstanceState);
        if (champion == null) {
            reRollChampion(getSelectedRole());
        } else {
            populatePage();
        }
    }

    @Override
    public void openChampionList(View view) {
        startActivity(getChampionListIntent());
    }

    @Override
    public void openChampion(View view) {
        reRollChampion(getSelectedRole());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CHAMPION_KEY, champion);
        super.onSaveInstanceState(outState);
    }

    public void reRollChampion(String type) {
        DatabaseManager.getInstance().findRandomChampion(this, type, champion);
    }

    public void updateChampion(Champion champion) {
        Log.d(TAG, "updateChampion() called with: champion = [" + champion + "]");
        this.champion = champion;
        populatePage();
    }

    private void populatePage() {
        if (champion == null) {
            return;
        }
        updateRotation();
        Bitmap bitmap = new DDragon(this).getChampionBitmap(champion, imageType);
        ImageView bg = findViewById(R.id.champion_background);
        if (bg != null && bitmap != null) {
            bg.setImageBitmap(bitmap);
        }
        TextView chName = findViewById(R.id.nameValue);
        chName.setText(champion.getName());
        TextView role = findViewById(R.id.roleValue);
        role.setText(champion.getCapitalizedTitle());
    }

    private void updateRotation() {
        WindowManager systemService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (systemService == null) return;
        Display display = systemService.getDefaultDisplay();
        int rotation = display.getRotation();
        imageType = (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ? ImageType.LOADING : ImageType.SPLASH;
    }

    @Override
    public void onFinishedChampionLoad(final Champion champion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateChampion(champion);
            }
        });
    }
}
