package com.spiderbiggen.randomchampionselector.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.spiderbiggen.randomchampionselector.ButtonActivity;
import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.adapters.ChampionAdapter;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.storage.database.callbacks.IDataInteractor;

import java.util.ArrayList;
import java.util.List;

public class ListChampionsActivity extends ButtonActivity implements IDataInteractor.OnFinishedChampionListListener, View.OnClickListener {

    private final ChampionAdapter adapter;
    private RecyclerView recyclerView;

    public ListChampionsActivity() {
        adapter = new ChampionAdapter(this, new ArrayList<Champion>(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_champions);
        recyclerView = findViewById(R.id.championList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        openChampionList(null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Spinner s = getRoleSpinner();
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseManager.getInstance().findChampionList(ListChampionsActivity.this, parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
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
                recyclerView.getLayoutManager().scrollToPosition(0);
                adapter.setChampions(champions);
            }
        });
    }

    @Override
    public void onClick(View v) {
        int layoutPosition = recyclerView.getChildLayoutPosition(v);
        Champion champion = adapter.getChampion(layoutPosition);
        Intent intent = getChampionIntent();
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion);
        startActivity(intent);
    }
}
