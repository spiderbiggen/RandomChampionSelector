package com.spiderbiggen.randomchampionselector.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.spiderbiggen.randomchampionselector.R;
import com.spiderbiggen.randomchampionselector.model.Champion;
import com.spiderbiggen.randomchampionselector.storage.database.DatabaseManager;
import com.spiderbiggen.randomchampionselector.ui.adapters.ChampionAdapter;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

public class ListChampionsActivity extends ButtonActivity {

    private final ChampionAdapter adapter;
    private RecyclerView recyclerView;
    private Disposable listFlowable;

    public ListChampionsActivity() {
        adapter = new ChampionAdapter(new ArrayList<>(), this::onClick);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_list_champions);
        recyclerView = findViewById(R.id.champion_list);
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

    private void onClick(View v) {
        int position = recyclerView.getChildAdapterPosition(v);
        Champion champion = adapter.getChampion(position);
        ImageView img = v.findViewById(R.id.champion_splash);

        Intent intent = getChampionIntent();
        intent.putExtra(ChampionActivity.CHAMPION_KEY, champion.getKey());
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, img, getString(R.string.champion_splash_transition_key));
        intent.putExtra(ChampionActivity.UP_ON_BACK_KEY, false);
        startActivity(intent, options.toBundle());
    }
}
