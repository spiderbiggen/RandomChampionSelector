package spiderbiggen.randomchampionselector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class ChampionActivity extends Activity {

    private Champion champ;
    private String championType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        championType = (String) intent.getSerializableExtra("Type");
        setContentView(R.layout.activity_champion);
        champ = Champions.pickRandomChampion(champ, championType);
        populatePage();
    }


    private void populatePage() {
        TextView chName = (TextView) findViewById(R.id.nameValue);
        chName.setText(champ.getName());
        TextView role = (TextView) findViewById(R.id.roleValue);
        role.setText(champ.getRole());
        TextView health = (TextView) findViewById(R.id.healthValue);
        health.setText(champ.getHealth() + "");
        TextView resource = (TextView) findViewById(R.id.resourceText);
        TextView resourceValue = (TextView) findViewById(R.id.resourceValue);
        if (champ.getResource() != 0 && champ.getResourceType() != null) {
            resource.setVisibility(View.VISIBLE);
            resourceValue.setVisibility(View.VISIBLE);

            resource.setText(champ.getResourceType());
            resourceValue.setText(champ.getResource() + "");
        } else {
            resource.setVisibility(View.INVISIBLE);
            resourceValue.setVisibility(View.INVISIBLE);
        }
        TextView range = (TextView) findViewById(R.id.rangeValue);
        range.setText(champ.getRange() + "");
        TextView movSpeed = (TextView) findViewById(R.id.movSpeedValue);
        movSpeed.setText(champ.getMovementSpeed() + "");

        RelativeLayout grid = (RelativeLayout) findViewById(R.id.gridView);

        String formattedString = champ.getName().replace(" ", "").replace("'", "").replace(".", "").toLowerCase();
        int resID = getResources().getIdentifier(formattedString, "drawable", getPackageName());
        Drawable image = getMyDrawable(resID);
        image.setAlpha(100);
        grid.setBackground(image);
    }

    public void pickRandomChampion(View view) {
        champ = Champions.pickRandomChampion(champ, championType);
        populatePage();
    }

    @SuppressWarnings("deprecation")
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
