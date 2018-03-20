package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    }

    @Override
    protected void onResume() {
        openChampionList();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dispose();
        super.onPause();
    }

    public void openChampionList() {
        dispose();
        listFlowable = DatabaseManager.getInstance().findChampionList(adapter::setChampions, null);
    }

    private void dispose() {
        if (listFlowable != null && !listFlowable.isDisposed()) {
            listFlowable.dispose();
        }
    }

    @Override
    public void openChampion(View view) {
        startActivity(getChampionIntent());
    }

    @Override
    public void onClick(View v) {
        int layoutPosition = recyclerView.getChildLayoutPosition(v);
        ImageView img = v.findViewById(R.id.champion_splash);
        TextView name = v.findViewById(R.id.champion_name);
        TextView title = v.findViewById(R.id.champion_title);

        Champion champion = adapter.getChampion(layoutPosition);
        Intent intent = getChampionIntent();
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion.getKey());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                Pair.create(img, getString(R.string.champion_splash_transition_key)),
                Pair.create(name, getString(R.string.champion_name_transition_key)),
                Pair.create(title, getString(R.string.champion_title_transition_key))
        );
        startActivity(intent, options.toBundle());
    }
}
