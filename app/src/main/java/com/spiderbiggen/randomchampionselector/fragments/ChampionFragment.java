package com.spiderbiggen.randomchampionselector.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

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


public class ChampionFragment extends Fragment implements IDataInteractor.OnFinishedListener, ImageCallback {

    public static final String KEY = "champion";
    private static final String TAG = ChampionFragment.class.getSimpleName();
    private Champion champion = null;
    private ImageType imageType = ImageType.LOADING;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            reRollChampion(null);
        } else {
            updateChampion((Champion) savedInstanceState.getSerializable(KEY));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(KEY, champion);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_champion, container, false);
        populatePage(view);
        return view;
    }

    public void reRollChampion(String type) {
        DatabaseManager.getInstance().findRandomChampion(this, type, champion);
    }

    public void updateChampion(Champion champion) {
        Log.d(TAG, "updateChampion() called with: champion = [" + champion + "]");
        this.champion = champion;
        populatePage(getView());
    }

    private void populatePage(View view) {
        if (view == null || champion == null) {
            return;
        }
        updateRotation(view);
        new DDragon(getContext()).getChampionImage(champion, imageType, this);
        TextView chName = view.findViewById(R.id.nameValue);
        chName.setText(champion.getName());

        TextView role = view.findViewById(R.id.roleValue);
        Log.d(TAG, "populatePage: " + role.getX());
        role.setText(champion.getCapitalizedTitle());
    }

    private void updateRotation(View view) {
        ImageView bg = getActivity().findViewById(R.id.champion_background);
        Log.d(TAG, "updateRotation: " + bg);
        WindowManager systemService = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
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
        getActivity().runOnUiThread(new Runnable() {
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

        ImageView bg = getActivity().findViewById(R.id.champion_background);
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
