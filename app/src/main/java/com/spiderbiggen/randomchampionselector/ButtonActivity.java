package com.spiderbiggen.randomchampionselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.spiderbiggen.randomchampionselector.fragments.ChampionActivity;
import com.spiderbiggen.randomchampionselector.fragments.ListChampionsActivity;

import java.util.ArrayList;

/**
 * Fullscreen activity
 * Created by Stefan on 9-5-2015.
 */
public abstract class ButtonActivity extends AppCompatActivity {

    public static final String SPINNER_DATA_KEY = "spinner_data";
    public static final String SPINNER_SELECTED_KEY = "spinner_selected";

    private static final String TAG = ButtonActivity.class.getSimpleName();

    private ArrayList<String> spinnerData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        spinnerData = intent.getStringArrayListExtra(SPINNER_DATA_KEY);
        Spinner s = getRoleSpinner();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, spinnerData);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        String selected = intent.getStringExtra(SPINNER_SELECTED_KEY);
        if (selected != null) {
            int index = spinnerData.indexOf(selected);
            if (index >= 0) {
                s.setSelection(index);
            }
        }
        super.onCreate(savedInstanceState);
    }


    protected Spinner getRoleSpinner() {
        return findViewById(R.id.type_spinner);
    }

    protected String getSelectedRole() {
        return spinnerData.get(getRoleSpinner().getSelectedItemPosition());
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
        intent.putExtra(SPINNER_DATA_KEY, spinnerData);
        Spinner s = getRoleSpinner();
        intent.putExtra(SPINNER_SELECTED_KEY, spinnerData.get(s.getSelectedItemPosition()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        return intent;
    }
}
