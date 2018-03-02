package com.spiderbiggen.randomchampionselector.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.spiderbiggen.randomchampionselector.ButtonActivity;
import com.spiderbiggen.randomchampionselector.IDataInteractor;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.ddragon.DDragon;
import com.spiderbiggen.randomchampionselector.ddragon.callback.ImageCallback;
import com.spiderbiggen.randomchampionselector.ddragon.tasks.ImageType;
import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;

import java.util.List;
import java.util.Objects;


public class ChampionActivity extends ButtonActivity implements IDataInteractor.OnFinishedListener, ImageCallback {

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
        new DDragon(this).getChampionImage(champion, imageType, this);
        TextView chName = findViewById(R.id.nameValue);
        chName.setText(champion.getName());

        TextView role = findViewById(R.id.roleValue);
        Log.d(TAG, "populatePage: " + role.getX());
        role.setText(champion.getCapitalizedTitle());
    }

    private void updateRotation() {
        ImageView bg = findViewById(R.id.champion_background);
        Log.d(TAG, "updateRotation: " + bg);
        WindowManager systemService = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (systemService == null) return;
        Display display = systemService.getDefaultDisplay();
        int rotation = display.getRotation();
        imageType = !(rotation != Surface.ROTATION_0 && rotation != Surface.ROTATION_180) ? ImageType.LOADING : ImageType.SPLASH;
    }

    @Override
    public void onFinishedChampionListLoad(List<Champion> champions) {
        throw new UnsupportedOperationException("Function not implemented");
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

    @Override
    public void onFinishedRoleListLoad(List roles) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void setImage(Bitmap bitmap, Champion champion, ImageType type) {
        if (bitmap == null) return;
        if (type != imageType) return;
        if (!Objects.equals(this.champion, champion)) return;

        ImageView bg = findViewById(R.id.champion_background);
        if (bg == null) return;
        bg.setImageBitmap(bitmap);
        bg.setAlpha(0.8f);
        bg.setImageMatrix(getBackgroundMatrix(bg, bitmap));
    }

    private Matrix getBackgroundMatrix(ImageView view, Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.reset();
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float xScale = view.getWidth() / width;
        float yScale = view.getHeight() / height;
        float scale = xScale > yScale ? xScale : yScale;
        matrix.postScale(scale, scale);
        return matrix;
    }
}
