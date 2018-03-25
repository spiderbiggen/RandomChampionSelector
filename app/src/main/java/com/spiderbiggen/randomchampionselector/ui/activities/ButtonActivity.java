package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.spiderbiggen.randomchampionselector.R;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public abstract class ButtonActivity extends AppCompatActivity {

    private static final String TAG = ButtonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        super.onCreate(savedInstanceState);
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openChampion(View view) {
        View fab = findViewById(R.id.fab);
        Intent intent = getChampionIntent();
        startActivity(intent);
    }

    protected Intent getChampionIntent() {
        return new Intent(this, ChampionActivity.class);
    }

    protected Intent getChampionListIntent() {
        return new Intent(this, ListChampionsActivity.class);
    }

    @Override
    public void startActivity(Intent intent) {
        ActivityOptionsCompat options = ActivityOptionsCompat.makeCustomAnimation(this, android.R.anim.fade_in, android.R.anim.fade_out);
        super.startActivity(intent, options.toBundle());
    }
}
