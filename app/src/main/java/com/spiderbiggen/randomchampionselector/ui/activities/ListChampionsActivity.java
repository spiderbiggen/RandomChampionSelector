package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.ui.adapters.ChampionAdapter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class ListChampionsActivity extends ButtonActivity implements View.OnClickListener {

    private final ChampionAdapter adapter;
    private RecyclerView recyclerView;
    private Disposable listFlowable;

    public ListChampionsActivity() {
        adapter = new ChampionAdapter(this, new ArrayList<>(), this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_champions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.championList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        openChampionList(null);
    }

    @Override
    protected void onDestroy() {
        if (listFlowable != null && !listFlowable.isDisposed()) {
            listFlowable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void openChampionList(View view) {
        if (listFlowable != null && !listFlowable.isDisposed()) {
            listFlowable.dispose();
        }
        listFlowable = DatabaseManager.getInstance().findChampionList(adapter::setChampions);
    }

    @Override
    public void openChampion(View view) {
        startActivity(getChampionIntent());
    }

    @Override
    public void onClick(View v) {
        int layoutPosition = recyclerView.getChildLayoutPosition(v);
        Champion champion = adapter.getChampion(layoutPosition);
        Intent intent = getChampionIntent();
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion.getKey());
        startActivity(intent);
    }
}
