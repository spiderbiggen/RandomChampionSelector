package com.spiderbiggen.randomchampionselector.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.spiderbiggen.randomchampionselector.ButtonActivity;
import com.spiderbiggen.randomchampionselector.IDataInteractor;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.adapters.ChampionAdapter;
import com.spiderbiggen.randomchampionselector.model.Ability;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

public class ListChampionsActivity extends ButtonActivity implements IDataInteractor.OnFinishedListener {

    private ChampionAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_champions);
        ListView lv = findViewById(R.id.championList);
        adapter = new ChampionAdapter(this, R.layout.list_champion_item, new ArrayList<Champion>());
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Champion champion = (Champion) parent.getItemAtPosition(position);
                onFinishedChampionLoad(champion);
            }
        });
        super.onCreate(savedInstanceState);
        openChampionList(null);
    }

    @Override
    public void openChampionList(View view) {
        DatabaseManager.getInstance().findChampionList(this, getSelectedRole());
    }

    @Override
    public void openChampion(View view) {
        startActivity(getChampionIntent());
    }

    @Override
    public void onFinishedChampionListLoad(final List<Champion> champions) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.setChampions(champions);
            }
        });
    }

    @Override
    public void onFinishedChampionLoad(final Champion champion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getChampionIntent();
                intent.putExtra(ChampionActivity.CHAMPION_KEY, champion);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFinishedRoleListLoad(List<String> roles) {
        throw new UnsupportedOperationException("Function not implemented");
    }

    @Override
    public void onFinishedAbilitiesLoad(List<Ability> abilities) {
        throw new UnsupportedOperationException("Function not implemented");
    }
}
