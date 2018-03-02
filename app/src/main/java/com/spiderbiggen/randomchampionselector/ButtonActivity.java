package com.spiderbiggen.randomchampionselector;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.spiderbiggen.randomchampionselector.fragments.ChampionFragment;
import com.spiderbiggen.randomchampionselector.fragments.ListChampionsFragment;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;

import java.util.ArrayList;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public class ButtonActivity extends AppCompatActivity {

    public static final String SPINNER_DATA_KEY = "spinner_data";

    private static final String TAG = ButtonActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);

        ArrayList<String> spinnerData = getIntent().getStringArrayListExtra(SPINNER_DATA_KEY);
        Spinner s = findViewById(R.id.type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adapter.setDropDownViewTheme(R.style.AppTheme_Spinner_Dropdown);
        s.setAdapter(adapter);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        ChampionFragment fragment = new ChampionFragment();
        ListChampionsFragment listFragment = new ListChampionsFragment();
        if (fragmentManager.findFragmentByTag("rngChampion") == null) {
            fragmentTransaction.add(R.id.fragment_container, fragment, "rngChampion");
        }
        if (fragmentManager.findFragmentByTag("listChampion") == null) {
            fragmentTransaction.add(R.id.fragment_container, listFragment, "listChampion").hide(listFragment);
        }
        fragmentTransaction.commit();
    }

    public void openChampionList(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsFragment listFragment = (ListChampionsFragment) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().hide(championFragment).show(listFragment).commit();
        Spinner spinner = findViewById(R.id.type_spinner);
        DatabaseManager.getInstance().findChampionList(listFragment, spinner.getSelectedItem().toString());
    }

    public void pickRandomChampion(View view) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ChampionFragment championFragment = (ChampionFragment) fragmentManager.findFragmentByTag("rngChampion");
        ListChampionsFragment listFragment = (ListChampionsFragment) fragmentManager.findFragmentByTag("listChampion");
        fragmentManager.beginTransaction().show(championFragment).hide(listFragment).commit();
        Spinner spinner = findViewById(R.id.type_spinner);
        championFragment.reRollChampion(spinner.getSelectedItem().toString());
    }
}
