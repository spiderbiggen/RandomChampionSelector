package spiderbiggen.randomchampionselector;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import spiderbiggen.randomchampionselector.champion.Ability;
import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.ChampionRole;
import spiderbiggen.randomchampionselector.util.StringHolder;
import spiderbiggen.randomchampionselector.util.database.DatabaseManager;

import static spiderbiggen.randomchampionselector.champion.ChampionRole.ASSASSIN;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.FIGHTER;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.MAGE;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.MARKSMAN;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.NONE;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.SUPPORT;
import static spiderbiggen.randomchampionselector.champion.ChampionRole.TANK;

public class ChampionFragment extends Fragment implements IDataInteractor.OnFinishedListener{

    private Champion champion;
    private int maxHealth;
    private int minHealth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseManager dbManager = DatabaseManager.getInstance();
        if (savedInstanceState == null) {
            dbManager.findRandomChampion(this, NONE, null);
        }else {
            updateChampion((Champion) savedInstanceState.getSerializable("champion"));
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable("champion", champion);
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_champion, container, false);
        maxHealth = DatabaseManager.getInstance().getMaxHealth();
        minHealth = DatabaseManager.getInstance().getMinHealth();
        populatePage(view);
        return view;
    }

    public void reRollChampion(String type) {
        StringHolder stringHolder = StringHolder.getInstance();
        ChampionRole role;
        if (type.equals(stringHolder.FIGHTER)) {
            role = FIGHTER;
        } else if (type.equals(stringHolder.TANK)) {
            role = TANK;
        } else if (type.equals(stringHolder.MAGE)) {
            role = MAGE;
        } else if (type.equals(stringHolder.SUPPORT)) {
            role = SUPPORT;
        } else if (type.equals(stringHolder.MARKSMAN)) {
            role = MARKSMAN;
        } else if (type.equals(stringHolder.ASSASSIN)) {
            role = ASSASSIN;
        } else {
            role = NONE;
        }
        DatabaseManager.getInstance().findRandomChampion(this, role, champion);
    }

    public void updateChampion(Champion champion) {
        this.champion = champion;
        populatePage(getView());
    }

    private void populatePage(View view) {
        if(view == null || champion == null) {
            return;
        }
        StringHolder strings = StringHolder.getInstance();

        TextView chName = (TextView) view.findViewById(R.id.nameValue);
        chName.setText(champion.getName());

        TextView role = (TextView) view.findViewById(R.id.roleValue);
        role.setText(strings.roleEnumToString(champion.getRole()));

        int healthPercent = (champion.getHealth() - minHealth) * 100 / (maxHealth - minHealth);
        ProgressBar pb = (ProgressBar) view.findViewById(R.id.progressBar);
        pb.setProgress(healthPercent);

        TextView resource = (TextView) view.findViewById(R.id.resourceText);
        TextView resourceValue = (TextView) view.findViewById(R.id.resourceValue);
        if (champion.getResource() > 0) {
            ViewGroup.LayoutParams params = resource.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            resource.setLayoutParams(params);

            params = resourceValue.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            resourceValue.setLayoutParams(params);

            resource.setText(strings.resourceTypeEnumToString(champion.getResourceType()));
            resourceValue.setText(String.format("%d", champion.getResource()));
            resource.setVisibility(View.VISIBLE);
            resourceValue.setVisibility(View.VISIBLE);
        } else {
            ViewGroup.LayoutParams params = resource.getLayoutParams();
            params.height = 0;
            resource.setLayoutParams(params);

            params = resourceValue.getLayoutParams();
            params.height = 0;
            resourceValue.setLayoutParams(params);

            resource.setVisibility(View.INVISIBLE);
            resourceValue.setVisibility(View.INVISIBLE);
        }

        TextView range = (TextView) view.findViewById(R.id.rangeValue);
        range.setText(strings.attackTypeEnumToString(champion.getAttackType()));

        ImageView bg = (ImageView) view.findViewById(R.id.champion_background);
        String formattedString = champion.getName().replace(" ", "").replace("'", "").replace(".", "").toLowerCase();

        Display display = ((WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        if (rotation != Surface.ROTATION_0 && rotation != Surface.ROTATION_180) {
            formattedString += "square";
        }
        int resID = getResources().getIdentifier(formattedString, "drawable", view.getContext().getPackageName());
        Drawable image = getMyDrawable(resID, view);
        image.setAlpha(100);
        bg.setImageDrawable(image);
    }

    private Drawable getMyDrawable(int id, View view) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        if (Build.VERSION.SDK_INT >= 21) {
            return this.getResources().getDrawableForDensity(id, dpi, view.getContext().getTheme());
        } else {
            return this.getResources().getDrawableForDensity(id, dpi);
        }
    }

    @Override
    public void onFinishedChampionListLoad(List<Champion> champions) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedChampionLoad(Champion champion) {
        this.updateChampion(champion);
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }
}
