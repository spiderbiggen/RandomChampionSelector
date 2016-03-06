package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import spiderbiggen.randomchampionselector.champion.Champion;
import spiderbiggen.randomchampionselector.champion.Champions;
import spiderbiggen.randomchampionselector.util.StringHolder;

public class ChampionActivity extends Activity {

    Boolean isRandom;
    private Champion champion;
    private String championType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();

        isRandom = intent.getBooleanExtra("IsRandom", true);
        setContentView(R.layout.activity_champion);

        if(isRandom) {
            championType = (String) intent.getSerializableExtra("Type");
            champion = Champions.pickRandomChampion(champion, championType);
        }else {
            champion = (Champion) intent.getSerializableExtra("Champion");
            Button button = (Button) findViewById(R.id.buttonRandom);
            ((ViewGroup)button.getParent()).removeView(button);
        }

        populatePage();
    }

    private void populatePage() {
        StringHolder strings = StringHolder.getInstance();

        TextView chName = (TextView) findViewById(R.id.nameValue);
        setTitle(champion.getName());
        chName.setText(champion.getName());

        TextView role = (TextView) findViewById(R.id.roleValue);
        role.setText(strings.roleEnumToString(champion.getRole()));

        /*TextView health = (TextView) findViewById(R.id.healthValue);
        health.setText(String.format("%d", champion.getHealth()));*/

        int healthPercent = (champion.getHealth() - Champions.minHealth) * 100 / (Champions.maxHealth - Champions.minHealth);
        ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setProgress(healthPercent);

        TextView resource = (TextView) findViewById(R.id.resourceText);
        TextView resourceValue = (TextView) findViewById(R.id.resourceValue);
        if (champion.getResource() > 0) {
            resource.setText(strings.resourceTypeEnumToString(champion.getResourceType()));
            resourceValue.setText(String.format("%d", champion.getResource()));
            resource.setVisibility(View.VISIBLE);
            resourceValue.setVisibility(View.VISIBLE);
        } else {
            resource.setVisibility(View.INVISIBLE);
            resourceValue.setVisibility(View.INVISIBLE);
        }

        TextView range = (TextView) findViewById(R.id.rangeValue);
        range.setText(strings.attackTypeEnumToString(champion.getAttackType()));

        RelativeLayout grid = (RelativeLayout) findViewById(R.id.gridView);

        String formattedString = champion.getName().replace(" ", "").replace("'", "").replace(".", "").toLowerCase();
        int resID = getResources().getIdentifier(formattedString, "drawable", getPackageName());
        Drawable image = getMyDrawable(resID);
        image.setAlpha(100);
        grid.setBackground(image);
    }

    public void pickRandomChampion(View view) {
        champion = Champions.pickRandomChampion(champion, championType);
        populatePage();
    }

    private Drawable getMyDrawable(int id) {
        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
        int dpi = displayMetrics.densityDpi;
        if (Build.VERSION.SDK_INT >= 21) {
            return this.getResources().getDrawableForDensity(id, dpi, this.getTheme());
        } else {
            return this.getResources().getDrawableForDensity(id, dpi);
        }
    }
}
