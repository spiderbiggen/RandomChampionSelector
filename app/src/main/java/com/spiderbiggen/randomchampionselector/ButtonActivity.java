package com.spiderbiggen.randomchampionselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.spiderbiggen.randomchampionselector.activities.ChampionActivity;
import com.spiderbiggen.randomchampionselector.activities.ListChampionsActivity;

import java.util.ArrayList;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public abstract class ButtonActivity extends AppCompatActivity {

    private static final String TAG = ButtonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public final boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public abstract void openChampionList(View view);

    public abstract void openChampion(View view);

    protected Intent getChampionIntent() {
        Intent intent = new Intent(this, ChampionActivity.class);
        return getButtonIntent(intent);
    }

    protected Intent getChampionListIntent() {
        Intent intent = new Intent(this, ListChampionsActivity.class);
        return getButtonIntent(intent);
    }

    private Intent getButtonIntent(Intent intent) {
        return intent;
    }
}
