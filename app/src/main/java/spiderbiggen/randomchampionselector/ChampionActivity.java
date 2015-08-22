package spiderbiggen.randomchampionselector;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChampionActivity extends AppCompatActivity {

    private Champion champion;
    private String championType;
    Boolean isRandom;

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
        TextView chName = (TextView) findViewById(R.id.nameValue);
        setTitle(champion.getName());
        chName.setText(champion.getName());
        TextView role = (TextView) findViewById(R.id.roleValue);
        role.setText(champion.getRole());
        TextView health = (TextView) findViewById(R.id.healthValue);
        health.setText(champion.getHealth() + "");
        TextView resource = (TextView) findViewById(R.id.resourceText);
        TextView resourceValue = (TextView) findViewById(R.id.resourceValue);
        if (champion.getResource() != 0 && champion.getResourceType() != null) {
            resource.setVisibility(View.VISIBLE);
            resourceValue.setVisibility(View.VISIBLE);

            resource.setText(champion.getResourceType());
            resourceValue.setText(champion.getResource() + "");
        } else {
            resource.setVisibility(View.INVISIBLE);
            resourceValue.setVisibility(View.INVISIBLE);
        }
        TextView range = (TextView) findViewById(R.id.rangeValue);
        range.setText(champion.getRange() + "");
        TextView movSpeed = (TextView) findViewById(R.id.movSpeedValue);
        movSpeed.setText(champion.getMovementSpeed() + "");

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

    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        return isRandom ? new Intent(this, ButtonActivity.class) : new Intent(this, ListChampionsActivity.class);
    }
}
